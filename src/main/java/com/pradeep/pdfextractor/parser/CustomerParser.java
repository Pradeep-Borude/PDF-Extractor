package com.pradeep.pdfextractor.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pradeep.pdfextractor.model.Invoice;

public final class CustomerParser {

    private CustomerParser() {
    }

    /*
     * Billed To : MAHINDRA & MAHINDRA LTD-A Shipped To : MAHINDRA & MAHINDRA LTD-A
     *
     * BUG in the old code: it looked for the line "TAX INVOICE" and
     * then walked UPWARD to find the nearest non-blank line above it,
     * assuming that line was the customer name.
     *
     * That assumption does not hold with the real, position-sorted
     * PDFBox text. "TAX INVOICE" is actually printed near the TOP of
     * the page, right under the page-type label:
     *
     *   Triplicate for Supplier
     *   TAX INVOICE                <-- old code anchors here
     *   Page 1 of 1
     *   : Transporter : PATIL TRANSPORT COMPANY
     *   ...
     *
     * so the old code was picking up "Triplicate for Supplier" (the
     * line directly above "TAX INVOICE") as the customer name, which
     * is completely wrong. The real Billed To / Shipped To line is
     * much further down the page and has nothing to do with where
     * "TAX INVOICE" appears.
     *
     * FIX: extract the customer name directly from the "Billed To :"
     * field, stopping at "Shipped To" (Billed To and Shipped To are
     * always the same customer in these invoices and sit on one
     * line after PDFBox sorts by position).
     */

    private static final Pattern CUSTOMER_PATTERN =
            Pattern.compile(
                    "Billed\\s*To\\s*:?\\s*(.+?)\\s*Shipped\\s*To",
                    Pattern.CASE_INSENSITIVE);

    public static void parse(String text, Invoice invoice) {

        if (text == null || invoice == null) {
            return;
        }

        Matcher matcher = CUSTOMER_PATTERN.matcher(text);

        if (matcher.find()) {
            invoice.setCustomer(clean(matcher.group(1)));
        }

    }

    private static String clean(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace('\n', ' ')
                .replaceAll("\\s{2,}", " ")
                .trim();

    }

}