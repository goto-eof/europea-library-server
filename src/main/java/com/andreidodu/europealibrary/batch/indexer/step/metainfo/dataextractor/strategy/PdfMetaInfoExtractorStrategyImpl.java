package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.common.StepUtil;
import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.service.TmpAssociationService;
import com.andreidodu.europealibrary.util.FileUtil;
import com.andreidodu.europealibrary.util.PdfUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Order(2)
@Component
@Transactional
@RequiredArgsConstructor
public class PdfMetaInfoExtractorStrategyImpl implements MetaInfoExtractorStrategy {
    final private static String STRATEGY_NAME = "pdf-meta-info-extractor-strategy";
    private final PdfUtil pdfUtil;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final BookInfoRepository bookInfoRepository;
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;
    private final OtherMetaInfoExtractorStrategyImpl otherMetaInfoExtractorStrategy;
    private final FileUtil fileUtil;
    private final StepUtil stepUtil;
    private final TmpAssociationService tmpAssociationService;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.disable-pdf-metadata-extractor}")
    private boolean disablePDFMetadataExtractor;
    @Value("${com.andreidodu.europea-library.job.indexer.step-meta-info-writer.disable-isbn-extractor}")
    private boolean disableIsbExtractor;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(String fullPathAndName, FileSystemItem fileSystemItem) {
        return !this.fileUtil.isDirectory(fullPathAndName) && dataExtractorStrategyUtil.isFileExtensionInWhiteList(pdfUtil.getPdfFileExtension()) &&
                !disablePDFMetadataExtractor &&
                pdfUtil.isPdf(fullPathAndName) &&
                dataExtractorStrategyUtil.wasNotAlreadyProcessed(fileSystemItem);
    }


    @Override
    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem) {
        log.debug("applying strategy: {}", getStrategyName());
        try {
            return buildMetaInfoFromFile(filename, fileSystemItem);
        } catch (IOException e) {
            log.debug("failed to load pdf metadata: {}", filename);
            return this.otherMetaInfoExtractorStrategy.extract(filename, fileSystemItem, true);
        }
    }

    private Optional<FileMetaInfo> buildMetaInfoFromFile(String fullPath, FileSystemItem fileSystemItem) throws IOException {
        File file = new File(fullPath);
        PDDocument pdf = Loader.loadPDF(file);
        PDDocumentInformation documentInformation = pdf.getDocumentInformation();

        FileMetaInfo fileMetaInfoEntity = fileSystemItem.getFileMetaInfo();
        FileMetaInfo fileMetaInfo = fileMetaInfoEntity == null ? new FileMetaInfo() : fileMetaInfoEntity;

        tryToSetBookTitle(fullPath, documentInformation, fileMetaInfo);

        final FileMetaInfo savedFileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);

        BookInfo bookInfo = buildBookInfo(pdf, fileMetaInfo.getBookInfo(), fullPath);
        bookInfo.setFileMetaInfo(fileMetaInfo);

        log.debug("PDF METADATA extracted: {}", fileMetaInfo);
        bookInfo.setFileExtractionStatus(FileExtractionStatusEnum.SUCCESS.getStatus());
        this.bookInfoRepository.save(bookInfo);

        tryToSetTags(fullPath, documentInformation, savedFileMetaInfo);

        return Optional.of(savedFileMetaInfo);
    }

    private void tryToSetTags(String fullPath, PDDocumentInformation documentInformation, FileMetaInfo savedFileMetaInfo) {
        try {
            final String keywordsStringTrimmed = StringUtil.cleanAndTrimToNull(documentInformation.getKeywords());
            List<String> keywords = new ArrayList<>();
            keywords.add(keywordsStringTrimmed);
            this.tmpAssociationService.addItemsToTmpAssociationTable(savedFileMetaInfo.getId(), keywords, DataPropertiesConst.TAG_NAME_MAX_LENGTH);
        } catch (Exception e) {
            log.debug("invalid pdf book keywords for '{}'", fullPath);
        }
    }

    private void tryToSetBookTitle(String fullPath, PDDocumentInformation documentInformation, FileMetaInfo fileMetaInfo) {
        try {
            final String title = StringUtil.cleanAndTrimToNullSubstring(documentInformation.getTitle(), DataPropertiesConst.FILE_META_INFO_TITLE_MAX_LENGTH);
            Optional.ofNullable(title)
                    .ifPresentOrElse(fileMetaInfo::setTitle,
                            () -> setFilenameAsBookTitle(fileMetaInfo, fullPath));
        } catch (Exception e) {
            log.debug("invalid pdf book title for '{}', will be used fullPath as title", fullPath);
            setFilenameAsBookTitle(fileMetaInfo, fullPath);
        }
    }

    public void setFilenameAsBookTitle(FileMetaInfo fileMetaInfo, String fullPath) {
        fileMetaInfo.setTitle(StringUtil.cleanAndTrimToNullSubstring(fileUtil.calculateFileBaseName(fullPath), DataPropertiesConst.FILE_META_INFO_TITLE_MAX_LENGTH));
    }

    private BookInfo buildBookInfo(PDDocument pdDocument, BookInfo bookInfoOld, String fullPath) throws IOException {
        BookInfo bookInfo = bookInfoOld == null ? new BookInfo() : bookInfoOld;
        tryToSetLanguage(pdDocument, fullPath, bookInfo);
        tryToSetNumberOfPages(pdDocument, fullPath, bookInfo);
        tryToSetAuthors(pdDocument, fullPath, bookInfo);
        if (!disableIsbExtractor) {
            tryToSetIsbn(pdDocument, fullPath, bookInfo);
        }
        return bookInfo;
    }

    private void tryToSetIsbn(PDDocument pdDocument, String fullPath, BookInfo bookInfo) {
        try {
            BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.pdfUtil.retrieveISBN(pdDocument);
            dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
            dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        } catch (Exception e) {
            log.debug("invalid pdf book content for '{}'", fullPath);
        }
    }

    private static void tryToSetAuthors(PDDocument pdDocument, String fullPath, BookInfo bookInfo) {
        try {
            bookInfo.setAuthors(StringUtil.cleanAndTrimToNullSubstring(pdDocument.getDocumentInformation().getAuthor(), DataPropertiesConst.BOOK_INFO_PUBLISHER_MAX_LENGTH));
        } catch (Exception e) {
            log.debug("invalid pdf book authors of pages for '{}'", fullPath);
        }
    }

    private static void tryToSetNumberOfPages(PDDocument pdDocument, String fullPath, BookInfo bookInfo) {
        try {
            bookInfo.setNumberOfPages(pdDocument.getNumberOfPages());
        } catch (Exception e) {
            log.debug("invalid pdf book number of pages for '{}'", fullPath);
        }
    }

    private static void tryToSetLanguage(PDDocument pdDocument, String fullPath, BookInfo bookInfo) {
        try {
            bookInfo.setLanguage(StringUtil.cleanAndTrimToNullLowerCaseSubstring(pdDocument.getDocumentCatalog().getLanguage(), DataPropertiesConst.BOOK_INFO_LANGUAGE_MAX_LENGTH));
        } catch (Exception e) {
            log.debug("invalid pdf book language for '{}'", fullPath);
        }
    }


}