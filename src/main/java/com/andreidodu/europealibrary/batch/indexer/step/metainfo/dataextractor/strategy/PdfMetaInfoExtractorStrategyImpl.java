package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
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
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.disable-pdf-metadata-extractor}")
    private boolean disablePDFMetadataExtractor;
    @Value("${com.andreidodu.europea-library.job.indexer.step-meta-info-writer.disable-isbn-extractor}")
    private boolean disableIsbExtractor;

    private final PdfUtil pdfUtil;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final BookInfoRepository bookInfoRepository;
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;
    private final OtherMetaInfoExtractorStrategyImpl otherMetaInfoExtractorStrategy;
    private final FileUtil fileUtil;

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
            return buildMetainfoFromFileMetainfo(filename, fileSystemItem);
        } catch (IOException e) {
            log.error("failed to load pdf metadata: {}", filename);
            return this.otherMetaInfoExtractorStrategy.extract(filename, fileSystemItem);
        }
    }

    private Optional<FileMetaInfo> buildMetainfoFromFileMetainfo(String filename, FileSystemItem fileSystemItem) throws IOException {
        File file = new File(filename);
        PDDocument pdf = Loader.loadPDF(file);
        PDDocumentInformation documentInformation = pdf.getDocumentInformation();

        FileMetaInfo fileMetaInfoEntity = fileSystemItem.getFileMetaInfo();
        FileMetaInfo fileMetaInfo = fileMetaInfoEntity == null ? new FileMetaInfo() : fileMetaInfoEntity;

        final String title = StringUtil.cleanAndTrimToNullSubstring(documentInformation.getTitle(), DataPropertiesConst.FILE_META_INFO_TITLE_MAX_LENGTH);
        Optional.ofNullable(title)
                .ifPresentOrElse(fileMetaInfo::setTitle,
                        () -> fileMetaInfo.setTitle(StringUtil.cleanAndTrimToNullSubstring(fileUtil.calculateFileBaseName(filename), DataPropertiesConst.FILE_META_INFO_TITLE_MAX_LENGTH)));

        final FileMetaInfo savedFileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);
        BookInfo bookInfo = buildBookInfo(pdf, fileMetaInfo.getBookInfo());
        bookInfo.setFileMetaInfo(fileMetaInfo);
        log.debug("PDF METADATA extracted: {}", fileMetaInfo);
        bookInfo.setFileExtractionStatus(FileExtractionStatusEnum.SUCCESS.getStatus());
        this.bookInfoRepository.save(bookInfo);

        final String keywordsStringTrimmed = StringUtil.cleanAndTrimToNull(documentInformation.getKeywords());
        return Optional.of(Optional.ofNullable(keywordsStringTrimmed).map((keywordsString) -> {
            List<String> keywords = new ArrayList<>();
            keywords.add(keywordsStringTrimmed);
            return dataExtractorStrategyUtil.createAndAssociateTags(keywords, savedFileMetaInfo);
        }).orElse(savedFileMetaInfo));
    }

    private BookInfo buildBookInfo(PDDocument pdDocument, BookInfo bookInfoOld) throws IOException {
        BookInfo bookInfo = bookInfoOld == null ? new BookInfo() : bookInfoOld;
        bookInfo.setLanguage(StringUtil.cleanAndTrimToNullLowerCaseSubstring(pdDocument.getDocumentCatalog().getLanguage(), DataPropertiesConst.BOOK_INFO_LANGUAGE_MAX_LENGTH));
        bookInfo.setNumberOfPages(pdDocument.getNumberOfPages());
        bookInfo.setAuthors(StringUtil.cleanAndTrimToNullSubstring(pdDocument.getDocumentInformation().getAuthor(), DataPropertiesConst.BOOK_INFO_PUBLISHER_MAX_LENGTH));
        if (!disableIsbExtractor) {
            BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.pdfUtil.retrieveISBN(pdDocument);
            dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
            dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        }
        return bookInfo;
    }


}