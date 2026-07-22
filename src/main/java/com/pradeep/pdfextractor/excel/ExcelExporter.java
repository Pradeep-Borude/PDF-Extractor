package com.pradeep.pdfextractor.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pradeep.pdfextractor.model.Invoice;

/**
 * Excel only. Responsible for writing Invoice rows into Output.xlsx.
 *
 * Batch contract:
 *   - accepts the whole List<Invoice> for one processing batch
 *   - opens Output.xlsx once (or creates it if missing)
 *   - appends every invoice after the current last row
 *     (never overwrites existing rows)
 *   - leaves exactly 3 blank rows after the batch
 *   - saves the workbook once
 */
public class ExcelExporter {

    private static final String SHEET_NAME = "Invoices";
    private static final int BLANK_ROWS_AFTER_BATCH = 3;
    private static final int COLUMN_COUNT = 16;

    private static final String[] HEADERS = new String[COLUMN_COUNT];

    static {
        HEADERS[ExcelColumns.INVOICE_NO] = "Invoice No";
        HEADERS[ExcelColumns.DATE] = "Date";
        HEADERS[ExcelColumns.VEHICLE] = "Vehicle No";
        HEADERS[ExcelColumns.TRANSPORTER] = "Transporter";
        HEADERS[ExcelColumns.CUSTOMER] = "Customer";
        HEADERS[ExcelColumns.PART_NO] = "Part No";
        HEADERS[ExcelColumns.QTY] = "Quantity";
        HEADERS[ExcelColumns.TOTAL_WEIGHT] = "Total Weight";
        HEADERS[ExcelColumns.WEIGHT_PER_PART] = "Weight / Part";
        HEADERS[ExcelColumns.PARTS_PER_BIN] = "Parts / Bin";
        HEADERS[ExcelColumns.TOTAL_BINS] = "Total Bins";
        HEADERS[ExcelColumns.BIN_WEIGHT] = "Bin Weight";
        HEADERS[ExcelColumns.TOTAL_BIN_WEIGHT] = "Total Bin Weight";
        HEADERS[ExcelColumns.NET_WEIGHT] = "Net Weight";
    }

    public void appendInvoices(File outputFile, List<Invoice> invoices) throws IOException {

        if (invoices == null || invoices.isEmpty()) {
            return;
        }

        Workbook workbook;
        Sheet sheet;

        if (!outputFile.exists()) {

            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(SHEET_NAME);
            writeHeader(sheet);

        } else {

            try (FileInputStream fis = new FileInputStream(outputFile)) {
                workbook = new XSSFWorkbook(fis);
            }

            sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                sheet = workbook.createSheet(SHEET_NAME);
                writeHeader(sheet);
            }

        }

        int nextRow = sheet.getLastRowNum() + 1;

        for (Invoice invoice : invoices) {
            writeInvoiceRow(sheet, nextRow, invoice);
            nextRow++;
        }

        for (int i = 0; i < BLANK_ROWS_AFTER_BATCH; i++) {
            sheet.createRow(nextRow);
            nextRow++;
        }

        for (int col = 0; col < COLUMN_COUNT; col++) {
            sheet.autoSizeColumn(col);
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            workbook.write(fos);
        }

        workbook.close();

    }

    private void writeHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        for (int col = 0; col < COLUMN_COUNT; col++) {
            header.createCell(col).setCellValue(HEADERS[col]);
        }

    }

    private void writeInvoiceRow(Sheet sheet, int rowIndex, Invoice invoice) {

        Row row = sheet.createRow(rowIndex);

        row.createCell(ExcelColumns.INVOICE_NO).setCellValue(nullToEmpty(invoice.getInvoiceNo()));
        row.createCell(ExcelColumns.DATE).setCellValue(nullToEmpty(invoice.getInvoiceDate()));
        row.createCell(ExcelColumns.VEHICLE).setCellValue(nullToEmpty(invoice.getVehicleNo()));
        row.createCell(ExcelColumns.TRANSPORTER).setCellValue(nullToEmpty(invoice.getTransporter()));
        row.createCell(ExcelColumns.CUSTOMER).setCellValue(nullToEmpty(invoice.getCustomer()));
        row.createCell(ExcelColumns.PART_NO).setCellValue(nullToEmpty(invoice.getPartNo()));
        row.createCell(ExcelColumns.QTY).setCellValue(invoice.getQuantity());
        row.createCell(ExcelColumns.TOTAL_WEIGHT).setCellValue(invoice.getTotalWeight());
        row.createCell(ExcelColumns.WEIGHT_PER_PART).setCellValue(invoice.getWeightPerPart());
        row.createCell(ExcelColumns.PARTS_PER_BIN).setCellValue(invoice.getPartsPerBin());
        row.createCell(ExcelColumns.TOTAL_BINS).setCellValue(invoice.getTotalBins());
        row.createCell(ExcelColumns.BIN_WEIGHT).setCellValue(invoice.getBinWeight());
        row.createCell(ExcelColumns.TOTAL_BIN_WEIGHT).setCellValue(invoice.getTotalBinWeight());
        row.createCell(ExcelColumns.NET_WEIGHT).setCellValue(invoice.getNetWeight());

    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

}