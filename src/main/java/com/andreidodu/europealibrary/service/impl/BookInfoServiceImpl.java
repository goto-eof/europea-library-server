package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.constants.BookInfoConst;
import com.andreidodu.europealibrary.dto.*;
import com.andreidodu.europealibrary.dto.common.FileMetaInfoDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.mapper.BookInfoMapper;
import com.andreidodu.europealibrary.mapper.FileMetaInfoMapper;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.service.*;
import com.stripe.exception.StripeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookInfoServiceImpl implements BookInfoService {
    private final StripeProductAssemblerService stripeProductAssemblerService;
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileMetaInfoMapper fileMetaInfoMapper;
    private final BookInfoRepository bookInfoRepository;
    private final CacheLoaderService cacheLoaderService;
    private final FileMetaInfoRepository repository;
    private final CategoryService categoryService;
    private final BookInfoMapper bookInfoMapper;
    private final TagService tagService;

    private final static int BOOK_COVER_IMAGE_TARGET_WIDTH = 250;

    @Value("${com.andreidodu.europea-library.book-covers-directory}")
    private String bookCoversDirectory;

    @Value("${com.andreidodu.europea-library.server.url}")
    private String serverUrl;

    @Value("${com.andreidodu.europea-library.server.book-cover-path}")
    private String bookCoverPath;

    private static void validateUpdateInput(Long id, FileMetaInfoDTO dto) {
        if (id == null || !id.equals(dto.getId())) {
            throw new ApplicationException("IDs does not match");
        }
    }

    @Override
    public OperationStatusDTO delete(Long id) {
        return this.repository.findById(id).map(model -> {
                    this.fileSystemItemRepository.deleteAllInBatch(model.getFileSystemItemList());
                    this.repository.delete(model);
                    return new OperationStatusDTO(true, "deleted");
                })
                .orElse(new OperationStatusDTO(false, "not found"));
    }

    @Override
    public OperationStatusDTO bulkLanguageRename(Authentication authentication, RenameDTO renameDTO) {
        int count = this.bookInfoRepository.renameLanguage(renameDTO.getOldName(), renameDTO.getNewName());
        if (count > 0) {
            this.cacheLoaderService.reloadLanguagesInCache(authentication);
        }
        return new OperationStatusDTO(count > 0, "language renamed from \"" + renameDTO.getOldName() + "\" to \"" + renameDTO.getNewName() + "\"");
    }

    private FileSystemItem checkFileSystemItemExistence(Long fileSystemItemId) {
        return this.fileSystemItemRepository.findById(fileSystemItemId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

    private FileMetaInfo checkFileMetaInfoExistence(Long id) {
        return this.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

    @Override
    public OperationStatusDTO bulkPublisherRename(Authentication authentication, RenameDTO renameDTO) {
        int count = this.bookInfoRepository.renamePublisher(renameDTO.getOldName(), renameDTO.getNewName());
        if (count > 0) {
            this.cacheLoaderService.reloadPublishersInCache(authentication);
        }
        return new OperationStatusDTO(count > 0, "publisher renamed from \"" + renameDTO.getOldName() + "\" to \"" + renameDTO.getNewName() + "\"");
    }

    @Override
    public OperationStatusDTO bulkPublishedDateRename(Authentication authentication, RenameDTO renameDTO) {
        int count = this.bookInfoRepository.renamePublishedDate(renameDTO.getOldName(), renameDTO.getNewName());
        if (count > 0) {
            this.cacheLoaderService.reloadPublishedDatesInCache(authentication);
        }
        return new OperationStatusDTO(count > 0, "published date renamed from \"" + renameDTO.getOldName() + "\" to \"" + renameDTO.getNewName() + "\"");
    }

    @Override
    public OperationStatusDTO lock(Long fileMetaInfoId) {
        return changeManualLockValue(fileMetaInfoId, BookInfoConst.MANUAL_LOCK_LOCKED);
    }

    @Override
    public OperationStatusDTO unlock(Long fileMetaInfoId) {
        return changeManualLockValue(fileMetaInfoId, BookInfoConst.MANUAL_LOCK_UNLOCKED);
    }

    private OperationStatusDTO changeManualLockValue(Long fileMetaInfoId, int lockType) {
        FileMetaInfo model = this.checkFileMetaInfoExistence(fileMetaInfoId);
        Integer manualLock = model.getBookInfo().getManualLock();
        if (manualLock == null || !manualLock.equals(lockType)) {
            model.getBookInfo().setManualLock(lockType);
            this.repository.save(model);
            return new OperationStatusDTO(true, "item locked");
        }
        return new OperationStatusDTO(false, "item already locked");
    }

    @Override
    public OperationStatusDTO uploadBookCover(Long metaInfoId, MultipartFile file) {
        try {
            String filename = saveBookCoverFile(file);
            updateFileMetaInfoImageUrl(metaInfoId, filename);
            return new OperationStatusDTO(true, "file saved successfully");
        } catch (Exception e) {
            return new OperationStatusDTO(false, "error while trying to save the file");
        }
    }

    @Override
    public FileMetaInfoDTO updateFileMetaInfoFull(Long fileMetaInfoId, FileMetaInfoDTO dto) throws StripeException {
        validateUpdateInput(fileMetaInfoId, dto);

        FileMetaInfo model = checkFileMetaInfoExistence(fileMetaInfoId);
        this.fileMetaInfoMapper.map(model, dto);
        model.setTagList(dto.getTagList()
                .stream()
                .map(TagDTO::getName)
                .map(String::toLowerCase)
                .map(String::trim)
                .distinct()
                .map(this.tagService::loadOrCreateTagEntity)
                .collect(Collectors.toList()));
        FileMetaInfo newModel = this.repository.save(model);

        BookInfoDTO bookInfoDTO = dto.getBookInfo();
        if (bookInfoDTO != null) {
            BookInfo bookInfo = model.getBookInfo();
            this.bookInfoMapper.map(bookInfo, bookInfoDTO);
            bookInfo.setCategoryList(dto.getBookInfo().getCategoryList()
                    .stream()
                    .map(CategoryDTO::getName)
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .distinct()
                    .map(this.categoryService::createCategoryEntity)
                    .collect(Collectors.toList()));
            bookInfo.setManualLock(BookInfoConst.MANUAL_LOCK_LOCKED);
            this.bookInfoRepository.save(bookInfo);
        }

        if (BooleanUtils.isTrue(dto.getOnSale())) {
            StripePriceDTO stripePriceDTO = dto.getStripePrice();
            if (stripePriceDTO != null) {
                if (stripePriceDTO.getId() != null) {
                    this.stripeProductAssemblerService.updateStripeProductAssembly(stripePriceDTO);
                } else {
                    this.stripeProductAssemblerService.createNewStripePriceAssembly(stripePriceDTO);
                }
            }
        }

        return this.fileMetaInfoMapper.toDTO(newModel);
    }

    private void updateFileMetaInfoImageUrl(Long metaInfoId, String filename) {
        String imageUrl = this.serverUrl + this.bookCoverPath + "/" + filename;
        FileMetaInfo fileMetaInfo = this.repository.findById(metaInfoId)
                .orElseThrow(() -> new EntityNotFoundException("entity not found"));
        fileMetaInfo.getBookInfo().setImageUrl(imageUrl);
        fileMetaInfo.getBookInfo().setManualLock(BookInfoConst.MANUAL_LOCK_LOCKED);
        this.repository.save(fileMetaInfo);
    }

    private String saveBookCoverFile(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();
        String filename = (uuid + "." + file.getOriginalFilename() + ".jpg").replace(" ", "_");
        Path fileNameAndPath = Paths.get(this.bookCoversDirectory, filename);
        byte[] bytes = resizeImage(file);
        Files.write(fileNameAndPath, bytes);
        return filename;
    }

    private static byte[] resizeImage(MultipartFile file) throws IOException {
        InputStream is = new ByteArrayInputStream(file.getBytes());
        BufferedImage bi = ImageIO.read(is);
        bi = Scalr.resize(bi, BOOK_COVER_IMAGE_TARGET_WIDTH);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", baos);
        return baos.toByteArray();
    }

}
