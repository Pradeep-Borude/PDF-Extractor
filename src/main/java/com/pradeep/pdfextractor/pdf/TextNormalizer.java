package com.pradeep.pdfextractor.pdf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextNormalizer {

    private TextNormalizer() {
    }

    public static String normalize(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        // -------------------------------
        // Standardize line endings
        // -------------------------------

        text = text.replace("\r\n", "\n");
        text = text.replace('\r', '\n');

        // -------------------------------
        // Remove tabs
        // -------------------------------

        text = text.replace('\t', ' ');

        // -------------------------------
        // Remove trailing spaces
        // -------------------------------

        text = text.replaceAll("[ ]+\n", "\n");

        // -------------------------------
        // Collapse multiple spaces
        // -------------------------------

        text = text.replaceAll("[ ]{2,}", " ");

        // -------------------------------
        // Collapse many blank lines
        // -------------------------------

        text = text.replaceAll("\n{3,}", "\n\n");

        // ---------------------------------------------------
        // Fix broken part numbers
        //
        // Example:
        //
        // 060-
        // 8040
        //
        // becomes
        //
        // 060-8040
        // ---------------------------------------------------

        text = text.replaceAll(
                "(\\d{2,6}-)\\s*\\n\\s*([A-Z0-9]+)",
                "$1$2");

        // ---------------------------------------------------
        // Join split words
        //
        // COMPA
        // NION
        //
        // becomes
        //
        // COMPANION
        // ---------------------------------------------------

        Pattern brokenWord = Pattern.compile(
                "([A-Z]{2,})\\n([A-Z]{2,})");

        Matcher matcher = brokenWord.matcher(text);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {

            matcher.appendReplacement(
                    sb,
                    matcher.group(1) + matcher.group(2));

        }

        matcher.appendTail(sb);

        text = sb.toString();

        // ---------------------------------------------------
        // Normalize field names
        // ---------------------------------------------------

        text = text.replaceAll("Tax\\s+Inv\\.\\s+No\\.", "Tax Inv. No.");
        text = text.replaceAll("Inv\\.\\s+Date", "Inv. Date");
        text = text.replaceAll("Vehicle\\s+No", "Vehicle No");
        text = text.replaceAll("Transporter\\s*:", "Transporter :");
        text = text.replaceAll("Customer\\s*:", "Customer :");

        // ---------------------------------------------------
        // Remove duplicate customer line
        //
        // MAHINDRA & MAHINDRA LTD-A
        // MAHINDRA & MAHINDRA LTD-A
        //
        // becomes
        //
        // MAHINDRA & MAHINDRA LTD-A
        // ---------------------------------------------------

        text = removeDuplicateCustomer(text);
        return text.trim();

    }

    private static String removeDuplicateCustomer(String text) {

        String[] lines = text.split("\n");

        StringBuilder builder = new StringBuilder();

        String previous = "";

        for (String line : lines) {

            String current = line.trim();

            if (!current.equals(previous)) {

                builder.append(current).append("\n");

            }

            previous = current;

        }

        return builder.toString();

    }

}
