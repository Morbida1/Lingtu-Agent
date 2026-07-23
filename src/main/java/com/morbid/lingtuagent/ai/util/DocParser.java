package com.morbid.lingtuagent.ai.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class DocParser {
    public String parse(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String ext = filename.substring(filename
                .lastIndexOf(".") + 1)
                .toLowerCase();
        return switch (ext) {
            case "pdf" -> parsePdf(file.getInputStream());
            case "docx", "doc" -> parseWord(file.getInputStream());
            case "md" ,"markdown" -> parseMarkdown(file.getInputStream());
            case "txt" -> parseText(file.getInputStream());
            default -> throw new IllegalArgumentException("不支持的文件类型"+ ext);
        };
    }
    private String parsePdf(java.io.InputStream is) throws Exception {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(is.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    private String parseWord(java.io.InputStream is) throws Exception {
            try (XWPFDocument document = new XWPFDocument(is)) {
                StringBuilder sb = new StringBuilder();
                document.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
                return sb.toString();
        }
    }
    private String parseMarkdown(java.io.InputStream is) throws Exception {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    private String parseText(java.io.InputStream is) throws Exception {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}
