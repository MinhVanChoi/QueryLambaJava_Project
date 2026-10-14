package vn.edu.crud;

import vn.edu.crud.dao.ClassDAO;
import vn.edu.crud.dao.StudentDAO;
import vn.edu.crud.database.DatabaseInitializer;
import vn.edu.crud.model.Student;
import vn.edu.crud.model.StudentClass;
import vn.edu.crud.service.StudentService;
import vn.edu.crud.util.InputUtil;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final StudentDAO STUDENT_DAO = new StudentDAO();
    private static final ClassDAO CLASS_DAO = new ClassDAO();
    private static final StudentService SERVICE = new StudentService(STUDENT_DAO);

    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        int choice;

        do {
            showMenu();
            choice = InputUtil.readInt(SCANNER, "Chon chuc nang: ");

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> showAllStudents();
                case 3 -> searchByName();
                case 4 -> searchByEmail();
                case 5 -> checkDuplicateEmail();
                case 6 -> updateSingleField();
                case 7 -> sortByName();
                case 8 -> sortByAge();
                case 9 -> sortByAgeDescThenName();
                case 10 -> countStudents();
                case 11 -> findOldestStudent();
                case 12 -> filterByAgeRange();
                case 13 -> filterWithCustomLambda();
                case 14 -> statisticsByEmailDomain();
                case 15 -> groupByClass();
                case 16 -> averageAge();
                case 17 -> deleteStudent();
                case 18 -> showClasses();
                case 0 -> System.out.println("Ket thuc chuong trinh.");
                default -> System.out.println("Lua chon khong hop le.");
            }

        } while (choice != 0);
    }

    private static void showMenu() {
        System.out.println("""

                =============================================================
                    STUDENT MANAGEMENT - JDBC + JAVA LAMBDA / STREAM API
                =============================================================
                 1. Them sinh vien (validate + duplicate email)
                 2. Hien thi tat ca sinh vien
                 3. Tim theo ten              [filter Lambda]
                 4. Tim theo email            [filter Lambda]
                 5. Kiem tra email trung      [anyMatch]
                 6. Cap nhat 1 field          [BiConsumer Lambda]
                 7. Sap xep theo ten          [Comparator]
                 8. Sap xep theo tuoi         [Comparator]
                 9. Tuoi giam dan + ten tang  [Comparator ket hop]
                10. Dem tong sinh vien        [count]
                11. Sinh vien lon tuoi nhat   [max]
                12. Loc theo khoang tuoi      [Predicate]
                13. Loc Lambda tuy bien
                14. Thong ke domain email     [groupingBy + counting]
                15. Nhom sinh vien theo lop   [groupingBy]
                16. Tuoi trung binh           [mapToInt + average]
                17. Xoa sinh vien             [JDBC DELETE]
                18. Danh sach lop
                 0. Thoat
                =============================================================
                """);
    }

    private static void addStudent() {
        System.out.println("\n--- THEM SINH VIEN ---");

        System.out.print("Ho ten: ");
        String name = SCANNER.nextLine().trim();

        System.out.print("Email: ");
        String email = SCANNER.nextLine().trim();

        int age = InputUtil.readInt(SCANNER, "Tuoi: ");

        showClasses();
        Integer classId = InputUtil.readNullableInt(
                SCANNER,
                "Class ID (de trong neu chua xep lop): "
        );

        Student student = new Student(name, email, age, classId);

        System.out.println(
                SERVICE.addStudent(student)
                        ? "Them sinh vien thanh cong."
                        : "Them sinh vien that bai."
        );
    }

    private static void showAllStudents() {
        printTable(SERVICE.getAll());
    }

    private static void searchByName() {
        System.out.print("Nhap tu khoa ten: ");
        printTable(SERVICE.searchByName(SCANNER.nextLine()));
    }

    private static void searchByEmail() {
        System.out.print("Nhap tu khoa email: ");
        printTable(SERVICE.searchByEmail(SCANNER.nextLine()));
    }

    private static void checkDuplicateEmail() {
        System.out.print("Nhap email can kiem tra: ");
        String email = SCANNER.nextLine().trim();

        System.out.println(
                SERVICE.emailExists(email)
                        ? "Email DA ton tai."
                        : "Email CHUA ton tai."
        );
    }

    private static void updateSingleField() {
        int id = InputUtil.readInt(SCANNER, "Nhap Student ID: ");

        System.out.print("Field [name/email/age/classId]: ");
        String field = SCANNER.nextLine().trim();

        if (field.equalsIgnoreCase("classId")) {
            showClasses();
        }

        System.out.print("Gia tri moi: ");
        String value = SCANNER.nextLine().trim();

        boolean success = SERVICE.updateSingleField(id, field, value);

        System.out.println(
                success
                        ? "Cap nhat thanh cong."
                        : "Cap nhat that bai."
        );
    }

    private static void sortByName() {
        printTable(SERVICE.sortByName());
    }

    private static void sortByAge() {
        printTable(SERVICE.sortByAge());
    }

    private static void sortByAgeDescThenName() {
        printTable(SERVICE.sortByAgeDescThenName());
    }

    private static void countStudents() {
        System.out.println(
                "Tong so sinh vien = " + SERVICE.countStudents()
        );
    }

    private static void findOldestStudent() {
        System.out.println("\n--- CAC SINH VIEN LON TUOI NHAT ---");
        printTable(SERVICE.findAllOldestStudents());
    }

    private static void filterByAgeRange() {
        int min = InputUtil.readInt(SCANNER, "Tu tuoi: ");
        int max = InputUtil.readInt(SCANNER, "Den tuoi: ");

        printTable(SERVICE.filterByAgeRange(min, max));
    }

    private static void filterWithCustomLambda() {
        System.out.println("""
                Vi du minh hoa Predicate<Student>:
                Loc sinh vien tuoi >= 20 va email thuoc gmail.com
                """);

        /*
         * Lambda duoc tao ngay tai noi goi service.
         * Day la diem quan trong cua bai:
         * service.filter() khong can biet quy tac cu the tu truoc.
         */
        Predicate<Student> condition =
                s -> s.getAge() >= 20
                        && s.getEmail().toLowerCase().endsWith("@gmail.com");

        printTable(SERVICE.filter(condition));
    }

    private static void statisticsByEmailDomain() {
        System.out.println("\n--- THONG KE THEO DOMAIN EMAIL ---");

        SERVICE.countByEmailDomain()
                .forEach((domain, count) ->
                        System.out.printf("%-20s : %d%n", domain, count)
                );
    }

    private static void groupByClass() {
        System.out.println("\n--- NHOM SINH VIEN THEO LOP ---");

        Map<String, List<Student>> groups = SERVICE.groupByClass();

        groups.forEach((classCode, students) -> {
            System.out.println("\n[" + classCode + "]");
            students.forEach(System.out::println);
        });
    }

    private static void averageAge() {
        System.out.printf(
                "Tuoi trung binh: %.2f%n",
                SERVICE.averageAge()
        );
    }

    private static void deleteStudent() {
        int id = InputUtil.readInt(SCANNER, "Nhap ID can xoa: ");

        STUDENT_DAO.findById(id).ifPresentOrElse(
                student -> {
                    System.out.println("Sinh vien se xoa:");
                    System.out.println(student);
                    System.out.print("Xac nhan y/n: ");

                    if (SCANNER.nextLine().equalsIgnoreCase("y")) {
                        System.out.println(
                                STUDENT_DAO.delete(id)
                                        ? "Xoa thanh cong."
                                        : "Xoa that bai."
                        );
                    }
                },
                () -> System.out.println("Khong tim thay sinh vien.")
        );
    }

    private static void showClasses() {
        System.out.println("\n--- DANH SACH LOP ---");
        CLASS_DAO.findAll().forEach(System.out::println);
    }

    private static void printTable(List<Student> students) {
        System.out.println();
        System.out.printf(
                "%-4s | %-24s | %-28s | %-3s | %-10s%n",
                "ID", "NAME", "EMAIL", "AGE", "CLASS"
        );
        System.out.println("-".repeat(82));

        if (students.isEmpty()) {
            System.out.println("(Khong co du lieu)");
            return;
        }

        /*
         * Method reference la dang viet gon cua Lambda:
         * student -> System.out.println(student)
         */
        students.forEach(System.out::println);
    }
}
