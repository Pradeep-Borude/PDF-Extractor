package com.pradeep.pdfextractor.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages processed-invoices.txt only.
 *
 * Responsibilities:
 *   - create the file if it does not exist
 *   - load previously processed invoice numbers
 *   - check whether an invoice number was already processed
 *   - append newly processed invoice numbers
 *
 * File format: one invoice number per line. No timestamps, no logs.
 */
public class DuplicateManager {

    private final Path file;
    private final Set<String> processedInvoiceNumbers = new HashSet<>();

    public DuplicateManager(Path file) throws IOException {
        this.file = file;
        ensureFileExists();
        loadProcessedInvoiceNumbers();
    }

    private void ensureFileExists() throws IOException {

        if (Files.notExists(file)) {

            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }

            Files.createFile(file);

        }

    }

    private void loadProcessedInvoiceNumbers() throws IOException {

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        for (String line : lines) {

            String trimmed = line.trim();

            if (!trimmed.isEmpty()) {
                processedInvoiceNumbers.add(trimmed);
            }

        }

    }

    /**
     * @return true if this invoice number has already been processed
     *         in a previous or current batch.
     */
    public boolean isDuplicate(String invoiceNo) {

        if (invoiceNo == null) {
            return false;
        }

        return processedInvoiceNumbers.contains(invoiceNo.trim());

    }

    /**
     * Records the invoice number as processed: adds it to the
     * in-memory set immediately (so later PDFs in the SAME batch are
     * also caught as duplicates) and appends it to
     * processed-invoices.txt.
     */
    public void markProcessed(String invoiceNo) throws IOException {

        if (invoiceNo == null || invoiceNo.isBlank()) {
            return;
        }

        String trimmed = invoiceNo.trim();

        if (processedInvoiceNumbers.contains(trimmed)) {
            return;
        }

        processedInvoiceNumbers.add(trimmed);

        Files.writeString(
                file,
                trimmed + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);

    }

}
