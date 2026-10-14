package vn.edu.crud.service;

import vn.edu.crud.dao.StudentDAO;
import vn.edu.crud.model.Student;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentService {

    private final StudentDAO studentDAO;

    /*
     * LAMBDA 1 - Predicate:
     * Quy tac validate tuoi duoc dong goi thanh mot ham.
     */
    private final Predicate<Integer> validAge =
            age -> age != null && age >= 16 && age <= 100;

    /*
     * LAMBDA 2 - Function:
     * Chuyen email thanh domain.
     */
    private final Function<Student, String> emailDomain =
            student -> {
                String email = student.getEmail();
                int at = email.indexOf('@');
                return at >= 0 ? email.substring(at + 1).toLowerCase() : "unknown";
            };

    /*
     * LAMBDA 3 - Consumer:
     * Hanh dong in mot Student.
     */
    private final Consumer<Student> printStudent =
            student -> System.out.println(student);

    /*
     * LAMBDA 4 - Map<String, BiConsumer<Student, String>>
     * Cho phep cap nhat tung truong bang lambda thay cho switch dai.
     */
    private final Map<String, BiConsumer<Student, String>> fieldUpdaters =
            Map.of(
                    "name", (student, value) -> student.setName(value),
                    "email", (student, value) -> student.setEmail(value),
                    "age", (student, value) -> student.setAge(Integer.parseInt(value)),
                    "classid", (student, value) -> student.setClassId(
                            value == null || value.isBlank() ? null : Integer.parseInt(value)
                    )
            );

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public boolean addStudent(Student student) {
        if (!validAge.test(student.getAge())) {
            System.out.println("Tuoi phai nam trong khoang 16-100.");
            return false;
        }

        if (student.getName() == null || student.getName().isBlank()) {
            System.out.println("Ho ten khong duoc de trong.");
            return false;
        }

        if (student.getEmail() == null || !student.getEmail().contains("@")) {
            System.out.println("Email khong hop le.");
            return false;
        }

        /*
         * anyMatch la terminal operation cua Stream.
         * Dung Lambda de kiem tra email da ton tai.
         */
        boolean duplicatedEmail = studentDAO.findAll()
                .stream()
                .anyMatch(s -> s.getEmail().equalsIgnoreCase(student.getEmail()));

        if (duplicatedEmail) {
            System.out.println("Email da ton tai.");
            return false;
        }

        return studentDAO.insert(student);
    }

    public List<Student> getAll() {
        return studentDAO.findAll();
    }

    /*
     * Bai 1: Tim sinh vien theo ten.
     * filter + contains + toList
     */
    public List<Student> searchByName(String keyword) {
        String normalized = keyword.toLowerCase().trim();

        return studentDAO.findAll()
                .stream()
                .filter(s -> s.getName().toLowerCase().contains(normalized))
                .toList();
    }

    /*
     * Bai 2: Tim sinh vien theo email.
     */
    public List<Student> searchByEmail(String keyword) {
        String normalized = keyword.toLowerCase().trim();

        return studentDAO.findAll()
                .stream()
                .filter(s -> s.getEmail().toLowerCase().contains(normalized))
                .toList();
    }

    /*
     * Bai 3: Kiem tra email trung.
     */
    public boolean emailExists(String email) {
        return studentDAO.findAll()
                .stream()
                .map(Student::getEmail)
                .anyMatch(e -> e.equalsIgnoreCase(email));
    }

    /*
     * Bai 4: Validate tuoi bang Predicate.
     */
    public boolean isValidAge(int age) {
        return validAge.test(age);
    }

    /*
     * Bai 5: Cap nhat chi mot field.
     *
     * Vi fieldUpdaters chua cac BiConsumer Lambda,
     * ta co the chon setter tai runtime.
     */
    public boolean updateSingleField(int id, String field, String newValue) {
        Optional<Student> optionalStudent = studentDAO.findById(id);

        if (optionalStudent.isEmpty()) {
            return false;
        }

        Student student = optionalStudent.get();
        String key = field.toLowerCase().replace("_", "").trim();

        BiConsumer<Student, String> updater = fieldUpdaters.get(key);

        if (updater == null) {
            System.out.println("Field chi ho tro: name, email, age, classId.");
            return false;
        }

        try {
            if ("age".equals(key)) {
                int age = Integer.parseInt(newValue);
                if (!validAge.test(age)) {
                    System.out.println("Tuoi phai nam trong khoang 16-100.");
                    return false;
                }
            }

            if ("email".equals(key)) {
                boolean duplicatedByAnotherStudent = studentDAO.findAll()
                        .stream()
                        .filter(s -> s.getId() != id)
                        .anyMatch(s -> s.getEmail().equalsIgnoreCase(newValue));

                if (duplicatedByAnotherStudent) {
                    System.out.println("Email da duoc sinh vien khac su dung.");
                    return false;
                }
            }

            updater.accept(student, newValue);
            return studentDAO.update(student);

        } catch (NumberFormatException e) {
            System.out.println("Gia tri so khong hop le.");
            return false;
        }
    }

    /*
     * Bai 6a: Sort theo ten bang Comparator Lambda.
     */
    public List<Student> sortByName() {
        return studentDAO.findAll()
                .stream()
                .sorted(Comparator.comparing(
                        Student::getName,
                        String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
    }

    /*
     * Bai 6b: Sort theo tuoi.
     */
    public List<Student> sortByAge() {
        return studentDAO.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Student::getAge))
                .toList();
    }

    /*
     * Sort ket hop: tuoi giam dan, neu bang tuoi thi ten tang dan.
     */
    public List<Student> sortByAgeDescThenName() {
        Comparator<Student> comparator =
                Comparator.comparingInt(Student::getAge)
                        .reversed()
                        .thenComparing(
                                Student::getName,
                                String.CASE_INSENSITIVE_ORDER
                        );

        return studentDAO.findAll()
                .stream()
                .sorted(comparator)
                .toList();
    }

    /*
     * Bai 7: Dem tong so sinh vien.
     */
    public long countStudents() {
        return studentDAO.findAll()
                .stream()
                .count();
    }

    /*
     * Bai 8: Tim sinh vien lon tuoi nhat.
     */
    public Optional<Student> findOldestStudent() {
        return studentDAO.findAll()
                .stream()
                .max(Comparator.comparingInt(Student::getAge));
    }

    /*
     * Bai bo sung: tim tat ca sinh vien cung tuoi lon nhat.
     */
    public List<Student> findAllOldestStudents() {
        List<Student> students = studentDAO.findAll();

        int maxAge = students.stream()
                .mapToInt(Student::getAge)
                .max()
                .orElse(Integer.MIN_VALUE);

        return students.stream()
                .filter(s -> s.getAge() == maxAge)
                .toList();
    }

    /*
     * Bai 9: Loc theo Predicate tuy bien.
     */
    public List<Student> filter(Predicate<Student> condition) {
        return studentDAO.findAll()
                .stream()
                .filter(condition)
                .toList();
    }

    public List<Student> filterByAgeRange(int min, int max) {
        Predicate<Student> inAgeRange =
                s -> s.getAge() >= min && s.getAge() <= max;

        return filter(inAgeRange);
    }

    /*
     * Bai 10a: Thong ke theo domain email.
     * groupingBy + counting.
     */
    public Map<String, Long> countByEmailDomain() {
        return studentDAO.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        emailDomain,
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /*
     * Bai 10b: Nhom sinh vien theo lop.
     */
    public Map<String, List<Student>> groupByClass() {
        return studentDAO.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        s -> Optional.ofNullable(s.getClassCode()).orElse("NO_CLASS"),
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    /*
     * Thong ke tuoi trung binh bang mapToInt.
     */
    public double averageAge() {
        return studentDAO.findAll()
                .stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    /*
     * Demo Consumer / method reference.
     */
    public void printStudents(List<Student> students) {
        students.forEach(printStudent);
    }
}
