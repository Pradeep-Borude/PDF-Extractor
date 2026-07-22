package com.pradeep.pdfextractor.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.pradeep.pdfextractor.excel.ExcelExporter;
import com.pradeep.pdfextractor.model.Invoice;
import com.pradeep.pdfextractor.model.ProcessingSummary;

/**
 * Processes a whole folder of PDFs. Responsible for folder-level
 * orchestration ONLY:
 *
 * - list every PDF in the folder (ignore non-PDF files)
 * - hand each one to InvoiceProcessingService, which stays
 * responsible for exactly one PDF
 * - skip duplicates via DuplicateManager
 * - collect the resulting Invoice objects
 * - call ExcelExporter exactly once for the whole batch
 * - return a ProcessingSummary
 */
public class BatchProcessingService {

    private final InvoiceProcessingService invoiceProcessingService;
    private final DuplicateManager duplicateManager;
    private final ExcelExporter excelExporter;

    public BatchProcessingService(
            InvoiceProcessingService invoiceProcessingService,
            DuplicateManager duplicateManager,
            ExcelExporter excelExporter) {

        this.invoiceProcessingService = invoiceProcessingService;
        this.duplicateManager = duplicateManager;
        this.excelExporter = excelExporter;

    }
// processes folders
    public ProcessingSummary processFolder(File pdfFolder, File outputExcel) throws IOException {

        ProcessingSummary summary = new ProcessingSummary();

        File[] pdfFiles = pdfFolder.listFiles(this::isPdfFile);

        if (pdfFiles == null || pdfFiles.length == 0) {
            return summary;
        }

        Arrays.sort(pdfFiles, Comparator.comparing(File::getName));

        List<Invoice> invoicesToExport = new ArrayList<>();

        for (File pdfFile : pdfFiles) {

            summary.incrementTotalPdfs();

            try {

                Invoice invoice = invoiceProcessingService.process(pdfFile);

                String invoiceNo = invoice.getInvoiceNo();

                if (invoiceNo == null || invoiceNo.isBlank()) {
                    summary.addFailedFile(pdfFile.getName(), "Unable to parse Invoice Number");
                    continue;
                }

                if (duplicateManager.isDuplicate(invoiceNo)) {
                    summary.incrementDuplicates();
                    continue;
                }

                invoicesToExport.add(invoice);

                duplicateManager.markProcessed(invoiceNo);

                summary.incrementProcessed();

            } catch (Exception e) {

                summary.addFailedFile(pdfFile.getName(), reasonFor(e));

            }

        }

        if (!invoicesToExport.isEmpty()) {
            excelExporter.appendInvoices(outputExcel, invoicesToExport);
        }

        return summary;

    }


    // processes files

    public ProcessingSummary processFiles(List<File> pdfFiles, File outputExcel) throws IOException {

        ProcessingSummary summary = new ProcessingSummary();

        List<Invoice> invoicesToExport = new ArrayList<>();

        for (File pdfFile : pdfFiles) {

            summary.incrementTotalPdfs();

            try {

                Invoice invoice = invoiceProcessingService.process(pdfFile);

                String invoiceNo = invoice.getInvoiceNo();

                if (invoiceNo == null || invoiceNo.isBlank()) {
                    summary.addFailedFile(
                            pdfFile.getName(),
                            "Unable to parse Invoice Number");
                    continue;
                }

                if (duplicateManager.isDuplicate(invoiceNo)) {
                    summary.incrementDuplicates();
                    continue;
                }

                invoicesToExport.add(invoice);

                duplicateManager.markProcessed(invoiceNo);

                summary.incrementProcessed();

            } catch (Exception e) {

                summary.addFailedFile(
                        pdfFile.getName(),
                        reasonFor(e));

            }
        }

        if (!invoicesToExport.isEmpty()) {
            excelExporter.appendInvoices(outputExcel, invoicesToExport);
        }

        return summary;
    }

    private boolean isPdfFile(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(".pdf");
    }

    private String reasonFor(Exception e) {

        String message = e.getMessage();

        if (message == null || message.isBlank()) {
            return e.getClass().getSimpleName();
        }

        return message;

    }

}
