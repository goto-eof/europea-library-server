package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.util.PdfUtil;
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
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;
    @Value("${com.andreidodu.europea-library.disable-pdf-metadata-extractor}")
    private boolean disablePDFMetadataExtractor;

    private final MetaInfoExtractorStrategyCommon metaInfoExtractorStrategyCommon;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(String filename, FileSystemItem fileSystemItem) {
        return metaInfoExtractorStrategyCommon.isFileExtensionInWhiteList(pdfUtil.getPdfFileExtension()) &&
                !disablePDFMetadataExtractor &&
                pdfUtil.isPdf(filename) &&
                metaInfoExtractorStrategyCommon.wasNotAlreadyProcessed(fileSystemItem);
    }


    @Override
    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem) {
        log.info("applying strategy: {}", getStrategyName());
        try {
            File file = new File(filename);
            PDDocument pdf = Loader.loadPDF(file);
            PDDocumentInformation documentInformation = pdf.getDocumentInformation();

            if (documentInformation.getTitle() == null) {
                return Optional.empty();
            }
            if (documentInformation.getAuthor() == null) {
                return Optional.empty();
            }

            FileMetaInfo fileMetaInfo = fileSystemItem.getFileMetaInfo() == null ? new FileMetaInfo() : fileSystemItem.getFileMetaInfo();
            fileMetaInfo.setTitle(documentInformation.getTitle());

            BookInfo bookInfo = buildBookInfo(pdf, fileMetaInfo.getBookInfo());
            bookInfo.setFileMetaInfo(fileMetaInfo);

            fileMetaInfo.setBookInfo(bookInfo);

            log.info("PDF metadata extracted: {}", fileMetaInfo);
            fileMetaInfo.getBookInfo().setIsInfoExtractedFromFile(true);
            return Optional.of(fileMetaInfo);
        } catch (IOException e) {
            log.error("Unable to parse PDF");
        }
        return Optional.empty();
    }

    private BookInfo buildBookInfo(PDDocument pdDocument, BookInfo bookInfoOld) throws IOException {
        BookInfo bookInfo = bookInfoOld == null ? new BookInfo() : bookInfoOld;
        bookInfo.setLanguage(pdDocument.getDocumentCatalog().getLanguage());
        bookInfo.setNumberOfPages(pdDocument.getNumberOfPages());
        bookInfo.setAuthors(pdDocument.getDocumentInformation().getAuthor());
        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.pdfUtil.retrieveISBN(pdDocument);
        dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
        dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        return bookInfo;
    }


}
