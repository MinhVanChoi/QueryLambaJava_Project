package com.nhom17.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class InputUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private InputUtil() {}

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("(!) Vui long nhap mot so nguyen hop le.");
            }
        }
    }

    public static Integer readNullableInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("(!) Vui long nhap mot so nguyen hoac de trong.");
            }
        }
    }

    public static Long readLong(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("(!) Vui long nhap mot so nguyen (Long) hop le.");
            }
        }
    }

    public static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return new BigDecimal(input);
            } catch (Exception e) {
                System.out.println("(!) Vui long nhap mot so tien hop le (VD: 50000).");
            }
        }
    }

    public static BigDecimal readNullableBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(input);
            } catch (Exception e) {
                System.out.println("(!) Vui long nhap mot so tien hop le hoac de trong.");
            }
        }
    }

    public static String readString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("(!) Khong duoc de trong.");
        }
    }

    public static String readNullableString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }

    public static LocalDate readLocalDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("(!) Dinh dang ngay khong hop le. Vi du: 2026-06-30");
            }
        }
    }

    public static LocalDateTime readLocalDateTime(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd HH:mm): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDateTime.parse(input, DATETIME_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("(!) Dinh dang thoi gian khong hop le. Vi du: 2026-06-30 08:00");
            }
        }
    }

    public static <E extends Enum<E>> E readEnum(Scanner scanner, String prompt, Class<E> enumClass) {
        E[] constants = enumClass.getEnumConstants();
        System.out.println(prompt);
        for (int i = 0; i < constants.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, constants[i].name());
        }
        while (true) {
            int choice = readInt(scanner, "Chon [1-" + constants.length + "]: ");
            if (choice >= 1 && choice <= constants.length) {
                return constants[choice - 1];
            }
            System.out.println("(!) Lua chon khong hop le.");
        }
    }
}
