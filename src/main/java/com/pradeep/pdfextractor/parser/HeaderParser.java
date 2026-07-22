package com.pradeep.pdfextractor.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pradeep.pdfextractor.model.Invoice;

public final class HeaderParser {

    private HeaderParser() {
    }

    /*
     * Tax Inv. No.
     * :
     * 2627-03364
     */

    private static final Pattern INVOICE_PATTERN =
            Pattern.compile(
                    "Tax\\s*Inv\\.\\s*No\\.?\\s*:?\\s*([A-Za-z0-9\\-/]+)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /*
     * Inv. Date
     * :
     * 14.07.2026
     */

    private static final Pattern DATE_PATTERN =
            Pattern.compile(
                    "Inv\\.\\s*Date\\s*:?\\s*(\\d{2}\\.\\d{2}\\.\\d{4})",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /*
     * Vehicle No
     * :
     * MH20EL9029
     */

    private static final Pattern VEHICLE_PATTERN =
            Pattern.compile(
                    "Vehicle\\s*No\\s*:?\\s*([A-Z]{2}\\d{2}[A-Z]{1,3}\\d{1,4})",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


    private static final Pattern TRANSPORTER_PATTERN =
            Pattern.compile(
                    "(?<!for\\s{1,5})Transporter\\s*:\\s*(.+?)"
                            + "(?=\\s*(?:Tax\\s*Inv|L\\.?\\s*R\\.?\\s*No"
                            + "|DC\\s*No|Vehicle\\s*No|Vendor\\s*Code"
                            + "|Place\\s*of\\s*Supply|Payment\\s*Term"
                            + "|Due\\s*Date|Cust\\s*Ord|Ack\\s*No"
                            + "|IRN\\s*No|Billed\\s*To|\\r?\\n|$))",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static void parse(String text, Invoice invoice) {

        if (text == null || invoice == null) {
            return;
        }

        invoice.setInvoiceNo(find(INVOICE_PATTERN, text));
        invoice.setInvoiceDate(find(DATE_PATTERN, text));
        invoice.setVehicleNo(find(VEHICLE_PATTERN, text));
        invoice.setTransporter(clean(find(TRANSPORTER_PATTERN, text)));

    }

    private static String find(Pattern pattern, String text) {

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
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