package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.dto.stripe.StripeProductDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.mapper.stripe.StripeProductMapper;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.stripe.StripeProduct;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.StripeProductRepository;
import com.andreidodu.europealibrary.service.StripeProductService;
import com.andreidodu.europealibrary.service.StripeRemoteProductService;
import com.andreidodu.europealibrary.util.ValidationUtil;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripeProductServiceImpl implements StripeProductService {
    private final StripeRemoteProductService stripeRemoteProductService;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final StripeProductRepository stripeProductRepository;
    private final StripeProductMapper stripeProductMapper;

    @Override
    public StripeProductDTO getByFileMetaInfoId(Long fileMetaInfoId) {
        ValidationUtil.assertNotNull(fileMetaInfoId, "FileMetaInfoId could not be null");
        StripeProduct stripeProduct = this.stripeProductRepository.findByFileMetaInfo_id(fileMetaInfoId)
                .orElseThrow(() -> new ValidationException("StripePrice does not exists"));
        return this.stripeProductMapper.toDTO(stripeProduct);
    }

    @Override
    public StripeProductDTO create(StripeProductDTO stripeProductDTO) throws StripeException {
        FileMetaInfo fileMetaInfo = this.fileMetaInfoRepository.findById(stripeProductDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("FileMetaInfo does not exists"));
        StripeProduct stripeProduct = this.stripeProductMapper.toModel(stripeProductDTO);
        stripeProduct.setFileMetaInfo(fileMetaInfo);
        String stripeProductId = this.stripeRemoteProductService.createNewStripeProduct(stripeProductDTO.getName(), stripeProductDTO.getDescription());
        stripeProduct.setStripeProductId(stripeProductId);
        stripeProduct = this.stripeProductRepository.save(stripeProduct);
        return this.stripeProductMapper.toDTO(stripeProduct);
    }

    @Override
    public StripeProductDTO update(StripeProductDTO stripeProductDTO) throws StripeException {
        StripeProduct stripeProduct = this.stripeProductRepository.findByFileMetaInfo_id(stripeProductDTO.getFileMetaInfoId())
                .orElseThrow(() -> new ValidationException("Product does not exists"));

        this.stripeProductMapper.map(stripeProduct, stripeProductDTO);

        if (stripeProduct.getStripeProductId() == null) {
            String stripeProductId = this.stripeRemoteProductService.createNewStripeProduct(stripeProductDTO.getName(), stripeProductDTO.getDescription());
            stripeProduct.setStripeProductId(stripeProductId);
        }

        stripeProduct = this.stripeProductRepository.save(stripeProduct);

        return this.stripeProductMapper.toDTO(stripeProduct);
    }

    @Override
    public OperationStatusDTO delete(Long fileMetaInfoId) {
        StripeProduct stripeProduct = this.stripeProductRepository.findByFileMetaInfo_id(fileMetaInfoId)
                .orElseThrow(() -> new ValidationException("Product does not exists"));
        this.stripeProductRepository.delete(stripeProduct);
        return new OperationStatusDTO(true, "Entity deleted successfully");
    }


}
