package com.pradeep.pdfextractor.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pradeep.pdfextractor.model.Invoice;

public final class ItemParser {

    private ItemParser() {
    }

    /*
     * Matches the only item present in the invoice.
     *
     * Example:
     *
     * 1 0503BA0310N04 - S/F END YOKE-SPLINE BROACHING
     * 87089900
     * 272
     * NOS
     * 187.93
     * 51,116.96
     */

    private static final Pattern ITEM_PATTERN = Pattern.compile(
            "\\b1\\s+"
          + "([A-Z0-9\\-]+)\\s+"
          + "-\\s+"
          + "(.+?)\\s+"
          + "(\\d{8})\\s+"
          + "(\\d+)\\s+"
          + "NOS\\s+"
          + "([\\d,.]+)\\s+"
          + "([\\d,.]+)",
            Pattern.DOTALL);

    public static void parse(String text, Invoice invoice) {

        Matcher matcher = ITEM_PATTERN.matcher(text);

        if (!matcher.find()) {
            return;
        }

        invoice.setPartNo(clean(matcher.group(1)));

        invoice.setQuantity(
                Integer.parseInt(
                        matcher.group(4).replace(",", "")
                )
        );

        invoice.setRate(
                Double.parseDouble(
                        matcher.group(5).replace(",", "")
                )
        );

        invoice.setAmount(
                Double.parseDouble(
                        matcher.group(6).replace(",", "")
                )
        );

    }

    private static String clean(String value) {

        return value
                .replace('\n', ' ')
                .replaceAll("\\s{2,}", " ")
                .trim();

    }

}
