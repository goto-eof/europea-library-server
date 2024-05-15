package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.stripe.PriceConvertMapper;
import com.andreidodu.europealibrary.mapper.stripe.StripeProductMapper;
import com.andreidodu.europealibrary.mapper.stripe.StripePriceMapper;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.stripe.StripePrice;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.StripePriceRepository;
import com.andreidodu.europealibrary.repository.StripeProductRepository;
import com.andreidodu.europealibrary.service.StripeProductAssemblerService;
import com.andreidodu.europealibrary.service.StripeRemotePriceService;
import com.andreidodu.europealibrary.service.StripeRemoteProductService;
import com.andreidodu.europealibrary.util.ValidationUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeProductAssemblerServiceImpl implements StripeProductAssemblerService {
    private final StripeRemoteProductService stripeRemoteProductService;
    private final StripeRemotePriceService stripeRemotePriceService;

    private final StripeProductRepository stripeProductRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final StripePriceRepository stripePriceRepository;

    private final StripeProductMapper stripeProductMapper;
    private final PriceConvertMapper priceConvertMapper;
    private final StripePriceMapper stripePriceMapper;

    @Override
    public StripePriceDTO getProductAssembly(Long fileMetaInfoId) {
        return null;
    }

    @Override
    public StripePriceDTO createNewStripePriceAssembly(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO, "dto could not be null");

        StripeProduct stripeProduct = createStripeProduct(stripePriceDTO);
        StripePrice stripePrice = createStripePrice(stripePriceDTO, stripeProduct);
        stripeProduct.setCurrentStripePrice(stripePrice);
        this.stripeProductRepository.save(stripeProduct);
        return this.stripePriceMapper.toDTO(stripePrice);
    }

    private StripeProduct createStripeProduct(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct(), "StripeProduct could not be null");
        StripeProductDTO stripeProductDTO = stripePriceDTO.getStripeProduct();
        ValidationUtil.assertNotNull(stripeProductDTO.getFileMetaInfoId(), "FileMetaInfoId could not be null");

        if (stripeProductRepository.existsByFileMetaInfo_id(stripeProductDTO.getFileMetaInfoId())) {
            throw new ValidationException("Product already exists");
        }

        FileMetaInfo fileMetaInfo = checkExistenceFileMetaInfo(stripeProductDTO);
        fillStripeProductNameIfNull(stripeProductDTO, fileMetaInfo);
        fillStripeProductDescriptionIfNull(stripeProductDTO, fileMetaInfo);

        ValidationUtil.assertNotNull(stripeProductDTO.getName(), "StripeProduct name could not be null");
        ValidationUtil.assertNotNull(stripeProductDTO.getDescription(), "StripeProduct description could not be null");

        StripeProduct stripeProduct = this.stripeProductMapper.toModel(stripeProductDTO);
        stripeProduct.setFileMetaInfo(fileMetaInfo);

        Product product = this.stripeRemoteProductService.createNewStripeProduct(stripeProductDTO.getName(), stripeProductDTO.getDescription());

        stripeProduct.setStripeProductId(product.getId());
        return this.stripeProductRepository.save(stripeProduct);
    }


    private StripePrice createStripePrice(StripePriceDTO stripePriceDTO, StripeProduct stripeProduct) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getAmount(), "StripePrice amount could not be null");
        ValidationUtil.assertNotNull(stripeProduct.getStripeProductId(), "StripeProductId could not be null");
        ValidationUtil.assertNotNull(stripeProduct, "StripeProduct could not be null");

        StripePrice oldProductPrice = stripeProduct.getCurrentStripePrice();

        BigDecimal oldPrice = oldProductPrice.getAmount();
        BigDecimal newPrice = stripePriceDTO.getAmount();

        String oldCurrency = oldProductPrice.getCurrency();
        String newCurrency = stripePriceDTO.getCurrency();

        // TODO the multiplication by 100 => find a more elegant solution
        if (!areDifferent(oldPrice, newPrice.multiply(BigDecimal.valueOf(100))) && !areDifferent(oldCurrency, newCurrency)) {
            return stripeProduct.getCurrentStripePrice();
        }

        StripePrice stripePrice = this.stripePriceMapper.toModel(stripePriceDTO);
        stripePrice.setStripeProduct(stripeProduct);
        Price price = this.stripeRemotePriceService.createNewStripePrice(stripeProduct.getStripeProductId(), stripePrice.getAmount().longValue(), null);
        stripePrice.setStripePriceId(price.getId());
        stripePrice = this.stripePriceRepository.save(stripePrice);
        return stripePrice;
    }

    // TODO move it in a different place
    private <T extends Comparable<T>> boolean areDifferent(T a, T b) {
        if (a == null && b != null) {
            return true;
        }
        if (a != null && b == null) {
            return true;
        }

        if (a.compareTo(b) != 0) {
            return true;
        }

        return false;
    }

    @Override
    public StripePriceDTO updateStripeProductAssembly(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO, "dto could not be null");

        // TODO check if there are any changes, if not, do not call Stripe


        StripeProduct stripeProduct = this.updateStripeProduct(stripePriceDTO);

        archivePrice(stripePriceDTO);

        StripePrice stripePrice = createStripePrice(stripePriceDTO, stripeProduct);

        stripeProduct.setCurrentStripePrice(stripePrice);
        this.stripeProductRepository.save(stripeProduct);

        return this.stripePriceMapper.toDTO(stripePrice);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void archivePrice(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct(), "StripeProduct could not be null");
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct().getFileMetaInfoId(), "FileMetaInfoId could not be null");

        StripePrice stripePrice = this.stripeProductRepository.findById(stripePriceDTO.getStripeProduct().getId()).get().getCurrentStripePrice();// this.stripePriceRepository.findByStripeProduct_FileMetaInfo_id(stripePriceDTO.getStripeProduct().getFileMetaInfoId());
        if (stripePrice != null) {
            this.stripeRemotePriceService.deactivate(stripePrice.getStripePriceId());
            this.stripePriceRepository.archive(true, stripePrice.getId());
        }
    }

    private StripeProduct updateStripeProduct(StripePriceDTO stripePriceDTO) throws StripeException {
        ValidationUtil.assertNotNull(stripePriceDTO.getStripeProduct(), "StripeProduct could not be null");
        StripeProductDTO stripeProductDTO = stripePriceDTO.getStripeProduct();
        ValidationUtil.assertNotNull(stripeProductDTO.getFileMetaInfoId(), "FileMetaInfoId could not be null");

        FileMetaInfo fileMetaInfo = checkExistenceFileMetaInfo(stripeProductDTO);
        fillStripeProductNameIfNull(stripeProductDTO, fileMetaInfo);
        fillStripeProductDescriptionIfNull(stripeProductDTO, fileMetaInfo);

        ValidationUtil.assertNotNull(stripeProductDTO.getName(), "StripeProduct name could not be null");
        ValidationUtil.assertNotNull(stripeProductDTO.getDescription(), "StripeProduct description could not be null");


        StripeProduct stripeProduct = this.stripeProductRepository.findByFileMetaInfo_id(stripeProductDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("StripeProduct does not exists"));

        if (!areDifferent(stripeProductDTO.getName(), stripeProduct.getName()) && !areDifferent(stripeProductDTO.getDescription(), stripeProduct.getDescription())) {
            return stripeProduct;
        }

        this.stripeProductMapper.map(stripeProduct, stripeProductDTO);

        stripeProduct.setFileMetaInfo(fileMetaInfo);

        this.stripeRemoteProductService.updateStripeProduct(stripeProduct.getStripeProductId(), stripeProductDTO.getName(), stripeProductDTO.getDescription());

        return this.stripeProductRepository.save(stripeProduct);
    }

    private FileMetaInfo checkExistenceFileMetaInfo(StripeProductDTO stripeProductDTO) {
        return this.fileMetaInfoRepository.findById(stripeProductDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("FileMetaInfo does not exists"));
    }

    private static void fillStripeProductDescriptionIfNull(StripeProductDTO stripeProductDTO, FileMetaInfo fileMetaInfo) {
        if (stripeProductDTO.getDescription() == null) {
            stripeProductDTO.setDescription(StringUtils.abbreviate(fileMetaInfo.getDescription(), 350));
        }
    }

    private static void fillStripeProductNameIfNull(StripeProductDTO stripeProductDTO, FileMetaInfo fileMetaInfo) {
        if (stripeProductDTO.getName() == null) {
            stripeProductDTO.setName(StringUtils.abbreviate(fileMetaInfo.getTitle(), 100));
        }
    }

}
