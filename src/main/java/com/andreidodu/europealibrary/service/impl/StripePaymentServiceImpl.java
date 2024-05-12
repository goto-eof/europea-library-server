package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.StripeCheckoutSessionRequestDTO;
import com.andreidodu.europealibrary.dto.StripeCheckoutSessionResponseDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.model.stripe.StripeCustomer;
import com.andreidodu.europealibrary.repository.StripeCustomerRepository;
import com.andreidodu.europealibrary.repository.security.UserRepository;
import com.andreidodu.europealibrary.service.StripePaymentService;
import com.nimbusds.jose.shaded.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements StripePaymentService {

    @Value("${com.andreidodu.europea-library.stripe.secret-key}")
    private String secretKey;

    @Value("${com.andreidodu.europea-library.stripe.webhook-secret-key}")
    private String webhookSecretKey;

    @Value("${com.andreidodu.europea-library.stripe.public-key}")
    private String publicKey;

    private final StripeCustomerRepository stripeCustomerRepository;
    private final UserRepository userRepository;

    @PostConstruct
    private void postConstruct() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public StripeCheckoutSessionResponseDTO initCheckoutSession(StripeCheckoutSessionRequestDTO stripeCheckoutSessionRequestDTO) {
        StripeCheckoutSessionResponseDTO stripeCheckoutSessionResponseDTO = new StripeCheckoutSessionResponseDTO();
        return stripeCheckoutSessionResponseDTO;
    }

    @Override
    public HttpStatus checkoutSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        String payload = httpServletRequest.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
        String sigHeader = httpServletRequest.getHeader("Stripe-Signature");
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
        if (dataObjectDeserializer.getObject().isPresent()) {
            StripeObject stripeObject = dataObjectDeserializer.getObject().get();
            // TODO change purchase session status to completed
            // TODO save stripe customer id and reuse it for the next payment
        } else {
            return HttpStatus.BAD_REQUEST;
        }
        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded": {
                // Then define and call a function to handle the event payment_intent.succeeded
                break;
            }
            // ... handle other event types
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }


        return HttpStatus.OK;


    }

    @Override
    public OperationStatusDTO checkPurchaseSessionStatus(Long purchaseSessionId) {
        return new OperationStatusDTO(true, "ok");
    }

    @Override
    public HttpStatus checkoutSubscriptionSessionCompleted(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return null;
    }

    private Price createPrice(String currency, Long amount, PriceCreateParams.Recurring.Interval recurringInterval, String planName) throws StripeException {
        PriceCreateParams params =
                PriceCreateParams.builder()
                        .setCurrency(currency)
                        .setUnitAmount(amount)
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(recurringInterval)
                                        .build()
                        )
                        .setProductData(
                                PriceCreateParams.ProductData.builder()
                                        .setName(planName)
                                        .build()
                        )
                        .build();
        return Price.create(params);
    }

    private Session createSubscriptionCheckoutSession(String checkoutBaseUrl, String subscribePricingPlanId, String stripeCustomerId, String clientReferenceId, String priceStripeId, Long quantity, SessionCreateParams.PaymentMethodType paymentMethodType) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setSuccessUrl(checkoutBaseUrl + "&ongoingPurchaseSessionId=" + clientReferenceId)
                        .setCancelUrl(checkoutBaseUrl + "&ongoingPurchaseSessionId=" + clientReferenceId)
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

    private Session createOneShotCheckoutSession(String checkoutBaseUrl, String stripeCustomerId, String clientReferenceId, String priceStripeId, Long quantity, SessionCreateParams.PaymentMethodType paymentMethodType) throws StripeException {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setSuccessUrl(checkoutBaseUrl + "&ongoingPurchaseSessionId=" + clientReferenceId)
                        .setCancelUrl(checkoutBaseUrl + "&ongoingPurchaseSessionId=" + clientReferenceId)
                        .setClientReferenceId(clientReferenceId)
                        .addPaymentMethodType(paymentMethodType)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(priceStripeId)
                                        .setQuantity(quantity)
                                        .build()
                        )
                        .setCustomer(stripeCustomerId)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .build();
        return Session.create(params);
    }


    private StripeCustomer createStripeCustomer(String firstName, String lastName, String email) {
        StripeCustomer stripeCustomer = new StripeCustomer();
        stripeCustomer.setFirstName(firstName);
        stripeCustomer.setLastName(lastName);
        stripeCustomer.setUser(this.userRepository.findByEmail(email).orElseThrow(() -> new ValidationException("User not found")));
        stripeCustomer.setStripeCustomerId(null);
        return this.stripeCustomerRepository.save(stripeCustomer);
    }

    private StripeCustomer updateStripeCustomerId(String email, String stripeCustomerId) {
        StripeCustomer stripeCustomer = this.stripeCustomerRepository.findByUser_email(email).orElseThrow(() -> new ValidationException("Stripe Customer not found"));
        stripeCustomer.setStripeCustomerId(stripeCustomerId);
        return this.stripeCustomerRepository.save(stripeCustomer);
    }

//    private StripeCustomer loadCustomer(String email) throws StripeException {
//        Optional<StripeCustomer> stripeCustomerOptional = this.stripeCustomerRepository.findByUser_email(email);
//        if (stripeCustomerOptional.isEmpty()) {
//            StripeCustomer stripeCustomer = new StripeCustomer();
//
//
//            User user = this.userRepository.findByEmail(email)
//                    .orElseThrow(() -> new ValidationException("User not found"));
//            Customer customer = this.createStripeCustomer(user.getFirstName().toLowerCase() + " " + user.getLastName().toLowerCase(), email);
//            stripeCustomer.setStripeCustomerId(customer.getId());
//            stripeCustomer.setUser(user);
//            return this.stripeCustomerRepository.save(stripeCustomer);
//        }
//        return stripeCustomerOptional.get();
//    }

    private Customer createStripeCustomer(String name, String email) throws StripeException {
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(name)
                        .setEmail(email)
                        .build();
        return Customer.create(params);
    }
}
