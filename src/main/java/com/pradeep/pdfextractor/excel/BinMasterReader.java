package com.pradeep.pdfextractor.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class BinMasterReader {

    /*
     * Key   = Part Number
     * Value = Bin Weight
     */
    private final Map<String, Double> binWeightMap = new HashMap<>();

    public BinMasterReader(File excelFile) throws IOException {

        load(excelFile);

    }

    private void load(File excelFile) throws IOException {

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            /*
             * Skip Header
             */

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null)
                    continue;

                Cell partCell = row.getCell(0);
                Cell weightCell = row.getCell(1);

                if (partCell == null || weightCell == null)
                    continue;

                String partNo = getString(partCell).trim();

                if (partNo.isBlank())
                    continue;

                double weight = getDouble(weightCell);

                binWeightMap.put(partNo, weight);

            }

        }

    }

    public double getBinWeight(String partNo) {

        if (partNo == null)
            return 0;

        return binWeightMap.getOrDefault(partNo.trim(), 0.0);

    }

    private String getString(Cell cell) {

        DataFormatter formatter = new DataFormatter();

        return formatter.formatCellValue(cell);

    }

    private double getDouble(Cell cell) {

        switch (cell.getCellType()) {

            case NUMERIC:
                return cell.getNumericCellValue();

            case STRING:

                String value = cell.getStringCellValue().trim();

                if (value.isEmpty())
                    return 0;

                return Double.parseDouble(value);

            default:
                return 0;

        }

    }

}