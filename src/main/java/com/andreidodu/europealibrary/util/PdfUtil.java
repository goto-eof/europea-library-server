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

    public boolean isPdf(String fullPath) {
        return !(new File(fullPath).isDirectory()) && PDF_FILE_EXTENSION.equals(this.fileUtil.getExtension(fullPath));
    }

    public BookCodesDTO<Optional<String>, Optional<String>> retrieveISBN(PDDocument pdDocument) throws IOException {

        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(pdDocument);
        BookCodesDTO<Optional<String>, Optional<String>> result = new BookCodesDTO<>(Optional.empty(), Optional.empty());
        result.setIsbn13(this.regularExpressionUtil.extractISBN13(text));
        result.setIsbn10(this.regularExpressionUtil.extractISBN10(text));
        return result;
    }

}