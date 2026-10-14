# Student CRUD JDBC + Java Lambda

Project thực hành Java dành cho IntelliJ IDEA, Maven và MySQL 8.

## 1. Công nghệ

- Java 17
- IntelliJ IDEA
- Maven
- MySQL 8
- JDBC
- MySQL Connector/J 9.7.0
- Java Lambda Expressions
- Stream API
- Functional Interfaces: Predicate, Function, Consumer, BiConsumer
- Optional
- Comparator
- Collectors.groupingBy / counting

## 2. Mục tiêu

Project kết hợp hai phần:

### JDBC / MySQL
- CREATE
- READ
- UPDATE
- DELETE
- PreparedStatement
- ResultSet
- JOIN students + classes
- Foreign Key

### Lambda / Stream API
- Tìm theo tên/email
- Kiểm tra email trùng
- Validate tuổi 16-100 bằng Predicate
- Cập nhật một field bằng BiConsumer
- Sort theo tên/tuổi bằng Comparator
- Count
- Max
- Filter
- Grouping
- Average
- Method reference

## 3. Cấu trúc

```text
student-crud-jdbc-lambda/
├── pom.xml
├── README.md
├── sql/
│   └── schema.sql
└── src/main/
    ├── java/vn/edu/crud/
    │   ├── Main.java
    │   ├── dao/
    │   │   ├── ClassDAO.java
    │   │   └── StudentDAO.java
    │   ├── database/
    │   │   └── DBConnection.java
    │   ├── model/
    │   │   ├── Student.java
    │   │   └── StudentClass.java
    │   ├── service/
    │   │   └── StudentService.java
    │   └── util/
    │       └── InputUtil.java
    └── resources/
        └── database.properties
```

## 4. Chuẩn bị MySQL

Mở MySQL Workbench và chạy:

```text
sql/schema.sql
```

Script sẽ tạo:

- Database `student_db`
- Table `classes`
- Table `students`
- Foreign key `students.class_id -> classes.id`
- Dữ liệu mẫu

## 5. Cấu hình tài khoản MySQL

Mở:

```text
src/main/resources/database.properties
```

Ví dụ:

```properties
db.url=jdbc:mysql://localhost:3306/student_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8
db.user=root
db.password=123456
```

Hãy đổi `db.password` theo máy của bạn.

## 6. Mở bằng IntelliJ IDEA

1. File -> Open
2. Chọn thư mục `student-crud-jdbc-lambda`
3. IntelliJ nhận dạng `pom.xml`
4. Chọn `Load Maven Project` nếu IntelliJ hỏi
5. Đảm bảo Project SDK = JDK 17 hoặc mới hơn
6. Chạy `vn.edu.crud.Main`

Hoặc Terminal:

```bash
mvn clean compile
mvn exec:java
```

## 7. Menu

```text
 1. Thêm sinh viên
 2. Hiển thị tất cả
 3. Tìm theo tên
 4. Tìm theo email
 5. Kiểm tra email trùng
 6. Cập nhật một field
 7. Sort theo tên
 8. Sort theo tuổi
 9. Tuổi giảm + tên tăng
10. Đếm sinh viên
11. Sinh viên lớn tuổi nhất
12. Lọc khoảng tuổi
13. Predicate Lambda tùy biến
14. Thống kê domain email
15. Nhóm sinh viên theo lớp
16. Tuổi trung bình
17. Xóa sinh viên
18. Danh sách lớp
 0. Thoát
```

## 8. Các Lambda quan trọng

### Predicate

```java
Predicate<Integer> validAge =
        age -> age >= 16 && age <= 100;
```

### Filter

```java
students.stream()
        .filter(s -> s.getAge() >= 20)
        .toList();
```

### anyMatch

```java
students.stream()
        .anyMatch(s -> s.getEmail().equalsIgnoreCase(email));
```

### Comparator

```java
students.stream()
        .sorted(Comparator.comparingInt(Student::getAge))
        .toList();
```

### Function

```java
Function<Student, String> emailDomain =
        s -> s.getEmail().substring(
                s.getEmail().indexOf('@') + 1
        );
```

### Consumer / method reference

```java
students.forEach(System.out::println);
```

### BiConsumer

```java
BiConsumer<Student, String> updateName =
        (student, value) -> student.setName(value);
```

### groupingBy

```java
students.stream()
        .collect(Collectors.groupingBy(
                Student::getClassCode
        ));
```

## 9. Gợi ý bài tập cho sinh viên

1. Viết Predicate lọc sinh viên 18-21 tuổi.
2. Lọc sinh viên dùng Gmail.
3. Lọc sinh viên thuộc lớp EC2026A.
4. Sort tên Z -> A.
5. Sort classCode rồi đến name.
6. Tìm sinh viên nhỏ tuổi nhất.
7. Tính tuổi trung bình theo từng lớp.
8. Đếm sinh viên theo từng lớp.
9. Tạo Map<domain, List<Student>>.
10. Tạo chức năng export danh sách đã lọc ra CSV.

## 10. Luồng xử lý

```text
User
  |
  v
Main
  |
  v
StudentService
  | \
  |  \ Lambda / Stream API
  |   -> filter / map / sorted / count / max / groupingBy
  |
  v
StudentDAO
  |
  v
JDBC
  |
  v
MySQL 8
```

Điểm quan trọng: SQL/JDBC chịu trách nhiệm lưu trữ và truy xuất dữ liệu; Lambda/Stream chịu trách nhiệm xử lý collection trong Java.
