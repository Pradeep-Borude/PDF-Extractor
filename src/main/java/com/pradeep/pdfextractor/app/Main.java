package com.pradeep.pdfextractor.app;

import java.io.File;

import com.pradeep.pdfextractor.excel.BinMasterReader;
import com.pradeep.pdfextractor.model.Invoice;
import com.pradeep.pdfextractor.service.InvoiceProcessingService;

public class Main {

    public static void main(String[] args) {

        try {

            // Change invoice here while testing
            File pdf = new File("PDFs/invoice2.pdf");

            // Bin Master
            File binMaster = new File("BinMaster.xlsx");

            BinMasterReader reader = new BinMasterReader(binMaster);

            InvoiceProcessingService service =
                    new InvoiceProcessingService(reader);

            Invoice invoice = service.process(pdf);


        } catch (Exception e) {

            System.err.println("\n========== ERROR ==========\n");
            e.printStackTrace();

        }

    }


}