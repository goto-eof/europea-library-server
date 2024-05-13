package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCheckoutSessionResponseDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeCustomerDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeUserRegistrationRequestDTO;
import com.andreidodu.europealibrary.enums.StripePurchaseSessionStatus;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.stripe.StripeCustomerMapper;
import com.andreidodu.europealibrary.model.stripe.StripeCustomer;
import com.andreidodu.europealibrary.model.stripe.StripeCustomerProductsOwned;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import com.andreidodu.europealibrary.model.stripe.StripePurchaseSession;
import com.andreidodu.europealibrary.repository.StripeCustomerProductsOwnedRepository;
import com.andreidodu.europealibrary.repository.StripeCustomerRepository;
import com.andreidodu.europealibrary.repository.StripeProductRepository;
import com.andreidodu.europealibrary.repository.StripePurchaseSessionRepository;
import com.andreidodu.europealibrary.repository.security.UserRepository;
import com.andreidodu.europealibrary.service.StripePaymentService;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements StripePaymentService {

    public static final String STRIPE_SIGNATURE_HEADER = "Stripe-Signature";
    public static final String STRIPE_EVENT_CHECKOUT_SESSION_COMPLETED = "checkout.session.completed";
    public static final String ONGOING_PURCHASE_SESSION_ID_QUERY_PARAMETER = "ongoingPurchaseSessionId";
    public static final String SUCCESS_QUERY_PARAMETER = "success";

    @Value("${com.andreidodu.europea-library.stripe.secret-key}")
    private String secretKey;

    @Value("${com.andreidodu.europea-library.stripe.webhook-secret-key}")
    private String webhookSecretKey;

    @Value("${com.andreidodu.europea-library.stripe.public-key}")
    private String publicKey;

    private final StripeCustomerProductsOwnedRepository stripeCustomerProductsOwnedRepository;
    private final StripePurchaseSessionRepository stripePurchaseSessionRepository;
    private final StripeCustomerRepository stripeCustomerRepository;
    private final StripeProductRepository stripeProductRepository;
    private final UserRepository userRepository;

    private final StripeCustomerMapper stripeCustomerMapper;

    @PostConstruct
    private void postConstruct() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public OperationStatusDTO isStripeCustomer(String username) {
        return new OperationStatusDTO(this.stripeCustomerRepository.existsByUser_username(username));
    }

    @Override
    public StripeCustomerDTO createStripeCustomer(StripeUserRegistrationRequestDTO stripeUserRegistrationRequestDTO) {
        StripeCustomer stripeCustomer = new StripeCustomer();
        stripeCustomer.setUser(this.userRepository.findById(stripeUserRegistrationRequestDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("user not found")));
        stripeCustomer.setFirstName(stripeUserRegistrationRequestDTO.getFirstName());
        stripeCustomer.setLastName(stripeUserRegistrationRequestDTO.getLastName());
        return this.stripeCustomerMapper.toDTO(this.stripeCustomerRepository.save(stripeCustomer));
    }

    @Override
    public StripeCheckoutSessionResponseDTO initCheckoutSession(StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO, String username) throws StripeException {
        StripeCheckoutSessionResponseDTO stripeCheckoutSessionResponseDTO = new StripeCheckoutSessionResponseDTO();

        StripeCustomer stripeCustomer = this.stripeCustomerRepository.findByUser_username(username).orElseThrow(() -> new ValidationException("User not found"));

        StripePurchaseSession stripePurchaseSession = new StripePurchaseSession();
        stripePurchaseSession.setStripeCustomer(stripeCustomer);
        StripeProduct stripeProduct = this.stripeProductRepository.findByFileMetaInfo_id(stripeCheckoutSessionRequestDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("Product not found"));
        stripePurchaseSession.setStripeProduct(stripeProduct);
        stripePurchaseSession.setStripePurchaseSessionStatus(StripePurchaseSessionStatus.CHECKOUT_IN_PROGRESS);
        stripePurchaseSession = this.stripePurchaseSessionRepository.save(stripePurchaseSession);

        String clientReferenceId = String.valueOf(stripePurchaseSession.getId());
        String price = String.valueOf(stripeProduct.getAmount());
        long quantity = stripeCheckoutSessionRequestDTO.getQuantity();

        Session session = this.createOneShotCheckoutSession(stripeCheckoutSessionRequestDTO.getCheckoutBaseUrl(),
                stripeCustomer.getStripeCustomerId(),
                clientReferenceId,
                price,
                quantity,
                SessionCreateParams.PaymentMethodType.CARD
        );

        stripeCheckoutSessionResponseDTO.setSessionId(session.getId());
        stripeCheckoutSessionResponseDTO.setStripePublicKey(publicKey);
        return stripeCheckoutSessionResponseDTO;
    }

    @Override
    public HttpStatus checkoutSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        String payload = httpServletRequest.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
        String sigHeader = httpServletRequest.getHeader(STRIPE_SIGNATURE_HEADER);
        Event event = null;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecretKey);
        } catch (JsonSyntaxException e) {
            // Invalid payload
            return HttpStatus.BAD_REQUEST;
        } catch (SignatureVerificationException e) {
            // Invalid signature
            return HttpStatus.BAD_REQUEST;
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            return HttpStatus.BAD_REQUEST;
        }
        // Handle the event
        if (event.getType().equals(STRIPE_EVENT_CHECKOUT_SESSION_COMPLETED)) {
            Session session = (Session) stripeObject;
            String customerStripeId = session.getCustomer();
            long purchaseSessionId = Long.parseLong(session.getClientReferenceId());
            Optional<StripePurchaseSession> stripePurchaseSessionOptional = this.stripePurchaseSessionRepository.findById(purchaseSessionId);
            if (stripePurchaseSessionOptional.isEmpty()) {
                return HttpStatus.BAD_REQUEST;
            }
            StripePurchaseSession stripePurchaseSession = stripePurchaseSessionOptional.get();
            stripePurchaseSession.setStripePurchaseSessionStatus(StripePurchaseSessionStatus.CHECKOUT_COMPLETED);
            final StripePurchaseSession savedStripePurchaseSession = this.stripePurchaseSessionRepository.save(stripePurchaseSession);

            this.stripeCustomerRepository.findByUser_email(session.getCustomerEmail())
                    .ifPresent(stripeCustomer -> {
                        stripeCustomer.setStripeCustomerId(customerStripeId);
                        stripeCustomer = this.stripeCustomerRepository.save(stripeCustomer);

                        StripeProduct stripeProduct = savedStripePurchaseSession.getStripeProduct();
                        StripeCustomerProductsOwned stripeCustomerProductsOwned = new StripeCustomerProductsOwned();
                        stripeCustomerProductsOwned.setStripeCustomer(stripeCustomer);
                        stripeCustomerProductsOwned.setStripeProduct(stripeProduct);
                        this.stripeCustomerProductsOwnedRepository.save(stripeCustomerProductsOwned);
                    });
            return HttpStatus.OK;
        } else {
            System.out.println("Unhandled event type: " + event.getType());
        }
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public OperationStatusDTO isCheckoutPurchaseSessionCompleted(Long purchaseSessionId) {
        StripePurchaseSessionStatus status = this.stripePurchaseSessionRepository.findById(purchaseSessionId)
                .map(StripePurchaseSession::getStripePurchaseSessionStatus)
                .orElseGet(() -> StripePurchaseSessionStatus.UNDEFINED);
        return new OperationStatusDTO(status.equals(StripePurchaseSessionStatus.CHECKOUT_COMPLETED), status.toString());
    }

    private Session createSubscriptionCheckoutSession(String checkoutBaseUrl, String subscribePricingPlanId, String stripeCustomerId, String clientReferenceId, String priceStripeId, Long quantity, SessionCreateParams.PaymentMethodType paymentMethodType) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setSuccessUrl(checkoutBaseUrl + "?" + SUCCESS_QUERY_PARAMETER + "=true&" + ONGOING_PURCHASE_SESSION_ID_QUERY_PARAMETER + "=" + clientReferenceId)
                        .setCancelUrl(checkoutBaseUrl + "?" + SUCCESS_QUERY_PARAMETER + "=false&" + ONGOING_PURCHASE_SESSION_ID_QUERY_PARAMETER + "=" + clientReferenceId)
                        .setClientReferenceId(clientReferenceId)
                        .addPaymentMethodType(paymentMethodType)
                        .setSubscriptionData(
                                SessionCreateParams.SubscriptionData.builder()
                                        .setDescription("")
                                        .build()
                        )
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .addLineItem(new SessionCreateParams.LineItem.Builder()
                                // For metered billing, do not pass quantity
                                .setQuantity(quantity)
                                .setPrice(priceStripeId)
                                .build()
                        )
                        .setCustomer(stripeCustomerId)
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .build();
        return Session.create(params);
    }

    public Subscription createSubscription(String priceStripeId, String customerStripeId) throws StripeException {
        SubscriptionCreateParams params =
                SubscriptionCreateParams.builder()
                        .setCustomer(customerStripeId)
                        .addItem(
                                SubscriptionCreateParams.Item.builder()
                                        .setPrice(priceStripeId)
                                        .build()
                        )
                        .build();
        return Subscription.create(params);
    }

    public Price createPrice() throws StripeException {
        PriceCreateParams params =
                PriceCreateParams.builder()
                        .setCurrency("eur")
                        .setUnitAmount(1000L)
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .setProductData(
                                PriceCreateParams.ProductData.builder().setName("Gold Plan").build()
                        )
                        .build();
        return Price.create(params);
    }

    private Session createOneShotCheckoutSession(String checkoutBaseUrl, String stripeCustomerId, String clientReferenceId, String price, Long quantity, SessionCreateParams.PaymentMethodType paymentMethodType) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setSuccessUrl(checkoutBaseUrl + "?" + SUCCESS_QUERY_PARAMETER + "=true&" + ONGOING_PURCHASE_SESSION_ID_QUERY_PARAMETER + "=" + clientReferenceId)
                        .setCancelUrl(checkoutBaseUrl + "?" + SUCCESS_QUERY_PARAMETER + "=false&" + ONGOING_PURCHASE_SESSION_ID_QUERY_PARAMETER + "=" + clientReferenceId)
                        .setClientReferenceId(clientReferenceId)
                        .addPaymentMethodType(paymentMethodType)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(price)
                                        .setQuantity(quantity)
                                        .build()
                        )
                        .setCustomer(stripeCustomerId)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .build();
        return Session.create(params);
    }

}
