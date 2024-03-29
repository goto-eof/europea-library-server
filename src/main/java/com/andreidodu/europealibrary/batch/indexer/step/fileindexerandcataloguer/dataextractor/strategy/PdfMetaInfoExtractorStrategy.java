package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.util.PdfUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class PdfMetaInfoExtractorStrategy implements MetaInfoExtractorStrategy {
    final private static String STRATEGY_NAME = "pdf-meta-info-extractor-strategy";

    private final PdfUtil pdfUtil;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final BookInfoRepository bookInfoRepository;
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.disable-pdf-metadata-extractor}")
    private boolean disablePDFMetadataExtractor;


    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(String filename, FileSystemItem fileSystemItem) {
        return dataExtractorStrategyUtil.isFileExtensionInWhiteList(pdfUtil.getPdfFileExtension()) &&
                !disablePDFMetadataExtractor &&
                pdfUtil.isPdf(filename) &&
                dataExtractorStrategyUtil.wasNotAlreadyProcessed(fileSystemItem);
    }


    @Override
    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem) {
        log.info("applying strategy: {}", getStrategyName());
        try {
            return buildMetainfoFromFileMetainfo(filename, fileSystemItem);
        } catch (IOException e) {
            log.error("failed to load pdf metadata: {}", filename);
            return Optional.of(buildMetainfoFromFileName(filename, fileSystemItem, FileExtractionStatusEnum.FAILED));
        }
    }

    private Optional<FileMetaInfo> buildMetainfoFromFileMetainfo(String filename, FileSystemItem fileSystemItem) throws IOException {
        File file = new File(filename);
        PDDocument pdf = Loader.loadPDF(file);
        PDDocumentInformation documentInformation = pdf.getDocumentInformation();
        if (StringUtil.isEmpty(documentInformation.getTitle())) {
            log.warn("pdf metadata is empty: {}", filename);
            return Optional.of(buildMetainfoFromFileName(filename, fileSystemItem, FileExtractionStatusEnum.SUCCESS_EMPTY));
        }
        FileMetaInfo fileMetaInfoEntity = fileSystemItem.getFileMetaInfo();
        FileMetaInfo fileMetaInfo = fileMetaInfoEntity == null ? new FileMetaInfo() : fileMetaInfoEntity;
        fileMetaInfo.setTitle(StringUtil.clean(documentInformation.getTitle()));
        fileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);
        BookInfo bookInfo = buildBookInfo(pdf, fileMetaInfo.getBookInfo());
        bookInfo.setFileMetaInfo(fileMetaInfo);
        log.info("PDF METADATA extracted: {}", fileMetaInfo);
        bookInfo.setFileExtractionStatus(FileExtractionStatusEnum.SUCCESS.getStatus());
        this.bookInfoRepository.save(bookInfo);
        return Optional.of(fileMetaInfo);
    }

    private FileMetaInfo buildMetainfoFromFileName(String filename, FileSystemItem fileSystemItem, FileExtractionStatusEnum fileExtractionStatusEnum) {
        FileMetaInfo fileMetaInfoEntity = fileSystemItem.getFileMetaInfo();
        FileMetaInfo fileMetaInfo = fileMetaInfoEntity == null ? new FileMetaInfo() : fileMetaInfoEntity;
        fileMetaInfo.setTitle(filename);
        fileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);
        BookInfo bookInfo = buildBookInfo(fileMetaInfo);
        bookInfo.setFileMetaInfo(fileMetaInfo);
        bookInfo.setFileExtractionStatus(fileExtractionStatusEnum.getStatus());
        bookInfoRepository.save(bookInfo);
        return fileMetaInfo;
    }

    private BookInfo buildBookInfo(FileMetaInfo fileMetaInfo) {
        BookInfo bookInfoOld = fileMetaInfo.getBookInfo();
        return bookInfoOld == null ? new BookInfo() : bookInfoOld;
    }

    private BookInfo buildBookInfo(PDDocument pdDocument, BookInfo bookInfoOld) throws IOException {
        BookInfo bookInfo = bookInfoOld == null ? new BookInfo() : bookInfoOld;
        bookInfo.setLanguage(StringUtil.clean(pdDocument.getDocumentCatalog().getLanguage()));
        bookInfo.setNumberOfPages(pdDocument.getNumberOfPages());
        bookInfo.setAuthors(StringUtil.clean(pdDocument.getDocumentInformation().getAuthor()));
        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.pdfUtil.retrieveISBN(pdDocument);
        dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
        dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        return bookInfo;
    }


}
