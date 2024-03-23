package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.dto.BookCodesDTO;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PdfUtil {
    private static final String PDF_FILE_EXTENSION = "pdf";
    private final FileUtil fileUtil;
    private final RegularExpressionUtil regularExpressionUtil;


    public BookCodesDTO<Optional<String>, Optional<String>> retrieveISBN(PDDocument pdDocument) throws IOException {
        PDFTextStripper reader = new PDFTextStripper();
        reader.setStartPage(1);
        reader.setEndPage(5);
        String text = reader.getText(pdDocument);
        BookCodesDTO<Optional<String>, Optional<String>> result = new BookCodesDTO<>(Optional.empty(), Optional.empty());
        result.setIsbn13(this.regularExpressionUtil.extractISBN13(text));
        result.setIsbn10(this.regularExpressionUtil.extractISBN10(text));
        return result;
    }

    public String getPdfFileExtension() {
        return PDF_FILE_EXTENSION;
    }

    public boolean isPdf(String fullPath) {
        return !(new File(fullPath).isDirectory()) && PDF_FILE_EXTENSION.equalsIgnoreCase(this.fileUtil.getExtension(fullPath));
    }
}
