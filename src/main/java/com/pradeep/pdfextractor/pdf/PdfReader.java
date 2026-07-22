package com.pradeep.pdfextractor.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public final class PdfReader {

    private PdfReader() {
    }

    /**
     * Reads complete PDF text while preserving layout as much as possible.
     */
    public static String read(File pdfFile) throws IOException {

        if (pdfFile == null) {
            throw new IllegalArgumentException("PDF file cannot be null.");
        }

        if (!pdfFile.exists()) {
            throw new IOException("File not found : " + pdfFile.getAbsolutePath());
        }

        if (!pdfFile.isFile()) {
            throw new IOException("Invalid PDF file : " + pdfFile.getAbsolutePath());
        }

        try (PDDocument document = Loader.loadPDF(pdfFile)) {

            PDFTextStripper stripper = new PDFTextStripper();

            /*
             * Preserve reading order.
             */
            stripper.setSortByPosition(true);

            /*
             * Read all pages.
             */
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());

            /*
             * Preserve line structure.
             */
            stripper.setLineSeparator("\n");

            /*
             * Preserve spacing.
             */
            stripper.setWordSeparator(" ");

            String text = stripper.getText(document);
            // System.out.println(text);

            return normalize(text);

        }

    }

    /**
     * Normalizes PDFBox output without destroying invoice structure.
     */
    private static String normalize(String text) {

        if (text == null) {
            return "";
        }

        /*
         * Windows → Unix
         */
        text = text.replace("\r\n", "\n");
        text = text.replace('\r', '\n');

        /*
         * Remove tabs.
         */
        text = text.replace('\t', ' ');

        /*
         * Remove repeated spaces.
         */
        text = text.replaceAll("[ ]{2,}", " ");

        /*
         * Remove trailing spaces.
         */
        text = text.replaceAll("[ ]+\n", "\n");

        /*
         * Collapse many blank lines.
         */
        text = text.replaceAll("\n{3,}", "\n\n");

        return text.trim();

    }

}