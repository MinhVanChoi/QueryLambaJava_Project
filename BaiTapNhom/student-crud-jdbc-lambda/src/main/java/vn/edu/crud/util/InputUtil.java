package vn.edu.crud.util;

import java.util.Scanner;

public final class InputUtil {

    private InputUtil() {
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();

            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Vui long nhap so nguyen hop le.");
            }
        }
    }

    public static Integer readNullableInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();

            if (text.isBlank()) {
                return null;
            }

            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Vui long nhap so nguyen hoac de trong.");
            }
        }
    }
}
