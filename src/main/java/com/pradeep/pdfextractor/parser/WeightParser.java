package com.pradeep.pdfextractor.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pradeep.pdfextractor.model.Invoice;

public final class WeightParser {

    /*
     * Matches:
     *
     * Total Weight : 432 KGS
     * Total Weight : 198.56 KGS
     * Total Weight : 235 KGS
     */

    private static final Pattern TOTAL_WEIGHT_PATTERN =
            Pattern.compile(
                    "Total\\s*Weight\\s*:?\\s*([\\d,.]+)\\s*-?\\s*KGS",
                    Pattern.CASE_INSENSITIVE);

    private WeightParser() {
    }

    public static void parse(String text, Invoice invoice) {

        if (text == null || invoice == null) {
            return;
        }

        Matcher matcher = TOTAL_WEIGHT_PATTERN.matcher(text);

        if (!matcher.find()) {
            return;
        }

        String value = matcher.group(1).replace(",", "");

        invoice.setTotalWeight(Double.parseDouble(value));

    }

}