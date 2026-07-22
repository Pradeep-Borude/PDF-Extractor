package com.pradeep.pdfextractor.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pradeep.pdfextractor.model.Invoice;

public final class PackingParser {

    private PackingParser() {
    }

    /*
     * Finds quantity and bins from every packing layout.
     */
    private static final Pattern QTY_PATTERN = Pattern.compile(
            "(\\d+)\\*(\\d+)\\s+NOS",
            Pattern.CASE_INSENSITIVE);

    /*
     * Detect corrugated box.
     */
    private static final Pattern BOX_PATTERN = Pattern.compile(
            "CORRUGATED\\s+BOX",
            Pattern.CASE_INSENSITIVE);

    public static void parse(String text, Invoice invoice) {

        parsePackingValues(text, invoice);

        /*
         * Corrugated box has NO bin code.
         * Keep ItemParser part number.
         */
        if (BOX_PATTERN.matcher(text).find()) {
            return;
        }

        String partNo = extractBinCode(text);

        if (partNo != null) {
            invoice.setPartNo(partNo);
        }
    }

    private static void parsePackingValues(String text, Invoice invoice) {

        Matcher m = QTY_PATTERN.matcher(text);

        if (m.find()) {

            invoice.setPartsPerBin(
                    Integer.parseInt(m.group(1)));

            invoice.setTotalBins(
                    Integer.parseInt(m.group(2)));
        }
    }

    /**
     * Extract first packing code.
     *
     * Supported layouts:
     *
     * BIN 060-00000109#600X400X80
     *
     * BIN 060-
     * 00002590#500X300X150
     *
     * PLASTIC BIN#060-
     * 20*25 NOS
     * 8040#400X300X100MM
     *
     * PLASTIC BIN#M&M#060-
     * 2*125 NOS
     * 2010#500X300X100
     */
    private static String extractBinCode(String text) {

        /*
         * Remove M&M marker.
         */
        text = text.replace("M&M#", "");

        /*
         * Remove quantity column so the split code becomes adjacent.
         *
         * Example:
         *
         * 060-
         * 20*25 NOS
         * 8040#
         *
         * =>
         *
         * 060-
         * 8040#
         */
        text = text.replaceAll(
                "\\d+\\*\\d+\\s+NOS",
                "");

        /*
         * Join broken codes.
         *
         * 060-
         * 8040#
         *
         * =>
         *
         * 060-8040#
         */
        text = text.replaceAll(
                "(\\d{2,6}-)\\s*\\n\\s*([A-Z0-9]+#)",
                "$1$2");

        /*
         * Join metal layout.
         *
         * BIN 060-
         * 00002590#
         *
         * =>
         *
         * BIN 060-00002590#
         */
        text = text.replaceAll(
                "(\\d{2,6}-)\\s*\\n\\s*(\\d+#)",
                "$1$2");

        /*
         * Find first code before '#'
         */
        Matcher matcher = Pattern.compile(
                "(?:BIN|PLASTIC\\s+BIN)\\s*#?([A-Z0-9\\-]+)#",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
                .matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }
}