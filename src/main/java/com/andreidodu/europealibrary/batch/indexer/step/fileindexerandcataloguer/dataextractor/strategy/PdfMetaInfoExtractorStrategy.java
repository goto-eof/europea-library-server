package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.util.PdfUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfMetaInfoExtractorStrategy implements MetaInfoExtractorStrategy {
    final private static String STRATEGY_NAME = "epub-meta-info-extractor-strategy";
    private final PdfUtil pdfUtil;
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;


    @Override
    public boolean accept(String filename) {
        return pdfUtil.isPdf(filename);
    }

    @Override
    public Optional<FileMetaInfo> extract(String filename) {
        try {
            File file = new File(filename);
            PDDocument pdf = Loader.loadPDF(file);
            PDDocumentInformation documentInformation = pdf.getDocumentInformation();

            FileMetaInfo fileMetaInfo = new FileMetaInfo();
            fileMetaInfo.setTitle(documentInformation.getTitle());

            BookInfo bookInfo = buildBookInfo(pdf);
            bookInfo.setFileMetaInfo(fileMetaInfo);

            fileMetaInfo.setBookInfo(bookInfo);

            return Optional.of(fileMetaInfo);
        } catch (IOException e) {
            log.error("Unable to parse PDF");
        }
        return Optional.empty();
    }

    private BookInfo buildBookInfo(PDDocument pdDocument) throws IOException {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setLanguage(pdDocument.getDocumentCatalog().getLanguage());
        bookInfo.setNumberOfPages(pdDocument.getNumberOfPages());
        bookInfo.setAuthors(pdDocument.getDocumentInformation().getAuthor());
        bookInfo.setPublisher(pdDocument.getDocumentInformation().getCreator());
        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.pdfUtil.retrieveISBN(pdDocument);
        dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
        dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        Optional.ofNullable(pdDocument.getDocumentInformation().getCreationDate())
                .ifPresent(date -> bookInfo.setPublishedDate(date.toString()));
        return bookInfo;
    }


}
