package com.nhom17.util;

import java.text.DecimalFormat;
import java.util.List;

public final class TablePrinter {

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0 VND");

    private TablePrinter() {}

    public static String formatCurrency(Object amount) {
        if (amount == null) return "0 VND";
        if (amount instanceof Number n) {
            return CURRENCY_FORMAT.format(n);
        }
        try {
            return CURRENCY_FORMAT.format(Double.parseDouble(amount.toString()));
        } catch (Exception e) {
            return amount.toString();
        }
    }

    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return;

        System.out.println(String.join(" | ", headers));
        System.out.println("-".repeat(80));

        if (rows == null || rows.isEmpty()) {
            System.out.println("(Khong co du lieu)");
        } else {
            rows.forEach(row -> System.out.println(String.join(" | ", row)));
        }

        System.out.println("-".repeat(80));
        System.out.println("Tong cong: " + (rows != null ? rows.size() : 0) + " dong.");
    }
}
