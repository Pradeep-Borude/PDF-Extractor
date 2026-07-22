package com.pradeep.pdfextractor.parser;

import com.pradeep.pdfextractor.model.Invoice;

public final class InvoiceParser {

    private InvoiceParser() {
    }

    /**
     * Parses normalized invoice text into an Invoice object.
     *
     * @param normalizedText Text returned from TextNormalizer
     * @return Parsed Invoice
     */
    public static Invoice parse(String normalizedText) {

        if (normalizedText == null || normalizedText.isBlank()) {
            throw new IllegalArgumentException("Invoice text is empty.");
        }

        Invoice invoice = new Invoice();

        HeaderParser.parse(normalizedText, invoice);

        CustomerParser.parse(normalizedText, invoice);

        ItemParser.parse(normalizedText, invoice);

        PackingParser.parse(normalizedText, invoice);

        WeightParser.parse(normalizedText, invoice);

        return invoice;
    }

}