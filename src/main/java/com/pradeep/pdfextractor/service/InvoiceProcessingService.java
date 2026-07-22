package com.pradeep.pdfextractor.service;

import java.io.File;
import java.io.IOException;

import com.pradeep.pdfextractor.excel.BinMasterReader;
import com.pradeep.pdfextractor.model.Invoice;
import com.pradeep.pdfextractor.parser.InvoiceParser;
import com.pradeep.pdfextractor.pdf.PdfReader;
import com.pradeep.pdfextractor.pdf.TextNormalizer;

public class InvoiceProcessingService {

    private final BinMasterReader binMasterReader;

    public InvoiceProcessingService(BinMasterReader binMasterReader) {
        this.binMasterReader = binMasterReader;
    }

    /**
     * Processes one invoice PDF and returns a fully populated Invoice object.
     */
    public Invoice process(File pdfFile) throws IOException {

        // Step 1 : Read PDF
        String text = PdfReader.read(pdfFile);

        // Step 2 : Normalize
        text = TextNormalizer.normalize(text);

        // Step 3 : Parse
        Invoice invoice = InvoiceParser.parse(text);

        // Step 4 : Lookup Bin Weight
        double binWeight = binMasterReader.getBinWeight(invoice.getPartNo());

        invoice.setBinWeight(binWeight);

        // Step 5 : Calculate Total Bin Weight
        invoice.setTotalBinWeight(
                binWeight * invoice.getTotalBins());

        // Step 6 : Calculate Weight Per Part
        if (invoice.getQuantity() > 0) {

            invoice.setWeightPerPart(
                    invoice.getTotalWeight() / invoice.getQuantity());

        } else {

            invoice.setWeightPerPart(0);

        }

        // Step 7 : Calculate Net Weight
        invoice.setNetWeight(
                invoice.getTotalWeight()
                        + invoice.getTotalBinWeight());

        return invoice;

    }

}