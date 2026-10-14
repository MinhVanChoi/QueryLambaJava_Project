# Fast Food Management System (MIS) - JDBC + Java Lambda & Stream API

Hệ thống Quản lý Chuỗi Cửa hàng Thức ăn Nhanh (Management Information System - MIS) dành cho Bài tập lớn Nhóm 17.  
Được thiết kế theo kiến trúc phân tầng chuẩn mực, kết hợp MySQL JDBC và Java 17 Functional Programming (Lambda Expressions, Stream API).

---

## 1. Công nghệ sử dụng

- **Ngôn ngữ**: Java 17 (LTS)
- **Build tool**: Apache Maven
- **Cơ sở dữ liệu**: MySQL 8.x / MySQL Connector/J 9.7.0 (Kèm cơ chế Auto-Fallback sang H2 In-Memory Database khi chưa cấu hình MySQL)
- **Kiến trúc**: Layered Architecture (Model - DAO - Service - Util - CLI View)
- **Java Functional Programming**:
  - `Lambda Expressions` & `Method References` (`System.out::println`, `Student::getName`...)
  - `Stream API` (`filter`, `map`, `sorted`, `distinct`, `count`, `max`, `min`, `reduce`, `mapToDouble`, `average`, `toList`)
  - `Functional Interfaces`: `Predicate<T>`, `Function<T, R>`, `Consumer<T>`, `BiConsumer<T, U>`, `Comparator<T>`
  - `Collectors`: `Collectors.groupingBy`, `Collectors.counting`, `Collectors.reducing`, `Collectors.toList`
  - `Optional<T>` xử lý null-safety
- **Kiểm thử tự động**: JUnit 5 (Jupiter 5.10.2)

---

## 2. Mục tiêu dự án

Dự án kết hợp hoàn chỉnh hai phần cốt lõi:

### Phần 1: Quản trị CSDL & JDBC (MySQL 8)
- **Thiết kế Schema 3NF**: 11 bảng quan hệ chuẩn hóa cao với đầy đủ Primary Key, Foreign Key (Cascade/Set Null), Check constraints, Default values.
- **CRUD Operations**: Thêm, đọc, cập nhật, xóa bản ghi trên các bảng thực thể và bảng giao dịch (`ChiNhanh`, `DanhMuc`, `MonAn`, `KhachHang`, `NhanVien`, `TaiKhoan`, `TonKho`, `DonHang`, `ChiTietDonHang`, `ThanhToan`, `KhuyenMai`).
- **30 Câu truy vấn nghiệp vụ**: Chia đều cho 3 thành viên theo chuẩn Section 9 của báo cáo (Inner/Left/Right/Full/Cross JOIN, Subqueries IN/NOT IN/EXISTS, Aggregate Functions, Group By / Having, Transaction Audit, v.v.).
- **An toàn SQL**: Sử dụng 100% `PreparedStatement` chống SQL Injection.

### Phần 2: Java Lambda & Stream API
- **Filter**: Lọc món ăn theo khoảng giá, trạng thái, danh mục bằng `Predicate<T>`.
- **Search**: Tìm kiếm nhân viên, khách hàng, món ăn theo từ khóa họ tên, email, số điện thoại.
- **Validation & Duplicate Check**: Kiểm tra trùng email, trùng số điện thoại, trùng tên đăng nhập bằng `anyMatch`.
- **Dynamic Field Update**: Cập nhật động 1 trường của thực thể tại runtime thông qua `Map<String, BiConsumer<T, String>>` thay cho các khối `switch-case` dài.
- **Sorting**: Sắp xếp món ăn theo giá tăng dần (`Comparator.comparing`), sắp xếp kết hợp đa tiêu chí (giá giảm dần + tên tăng dần qua `.reversed().thenComparing()`).
- **Aggregation**:
  - Tính tổng số đơn hàng hoàn thành (`count`)
  - Tìm món ăn đắt nhất (`max`)
  - Tính giá trị đơn hàng trung bình (`mapToDouble` + `average`)
  - Tính tổng doanh thu thực tế (`reduce` với `BigDecimal`)
- **Grouping**:
  - Nhóm món ăn theo danh mục (`groupingBy`)
  - Nhóm nhân viên theo chi nhánh (`groupingBy`)
  - Thống kê số lượng đơn theo trạng thái (`groupingBy` + `counting`)
  - Thống kê doanh thu theo phương thức thanh toán (`groupingBy` + `reducing`)

---

## 3. Cấu trúc thư mục dự án

```text
project/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/nhom17/
    │   │   ├── Main.java                          # Menu CLI giao diện tương tác
    │   │   ├── dao/                               # Tầng Data Access Object (JDBC)
    │   │   │   ├── ChiNhanhDAO.java
    │   │   │   ├── ChiTietDonHangDAO.java
    │   │   │   ├── DanhMucDAO.java
    │   │   │   ├── DonHangDAO.java
    │   │   │   ├── KhachHangDAO.java
    │   │   │   ├── KhuyenMaiDAO.java
    │   │   │   ├── MonAnDAO.java
    │   │   │   ├── NhanVienDAO.java
    │   │   │   ├── QueryDAO.java                  # 30 câu truy vấn Section 9
    │   │   │   ├── TaiKhoanDAO.java
    │   │   │   ├── ThanhToanDAO.java
    │   │   │   └── TonKhoDAO.java
    │   │   ├── database/                          # Tầng khởi tạo và kết nối DB
    │   │   │   ├── DBConnection.java              # Quản lý Connection Pool & Fallback H2
    │   │   │   └── DatabaseInitializer.java       # Tự động tạo bảng DDL & Seed Data
    │   │   ├── enums/                             # Các Enum trạng thái nghiệp vụ
    │   │   │   ├── ChucVu.java
    │   │   │   ├── PhuongThucThanhToan.java
    │   │   │   ├── TrangThaiDonHang.java
    │   │   │   ├── TrangThaiKhuyenMai.java
    │   │   │   ├── TrangThaiMon.java
    │   │   │   ├── TrangThaiTaiKhoan.java
    │   │   │   └── TrangThaiThanhToan.java
    │   │   ├── model/                             # Các Entity POJO
    │   │   │   ├── ChiNhanh.java
    │   │   │   ├── ChiTietDonHang.java
    │   │   │   ├── DanhMuc.java
    │   │   │   ├── DonHang.java
    │   │   │   ├── KhachHang.java
    │   │   │   ├── KhuyenMai.java
    │   │   │   ├── MonAn.java
    │   │   │   ├── NhanVien.java
    │   │   │   ├── TaiKhoan.java
    │   │   │   ├── ThanhToan.java
    │   │   │   └── TonKho.java
    │   │   ├── service/                           # Tầng nghiệp vụ kết hợp Lambda/Stream
    │   │   │   ├── FastFoodService.java           # Service tổng hợp minh họa toàn diện
    │   │   │   ├── MonAnService.java
    │   │   │   ├── KhachHangService.java
    │   │   │   ├── NhanVienService.java
    │   │   │   ├── DonHangService.java
    │   │   │   ├── TaiKhoanService.java
    │   │   │   ├── ThanhToanService.java
    │   │   │   ├── TonKhoService.java
    │   │   │   ├── ChiNhanhService.java
    │   │   │   ├── DanhMucService.java
    │   │   │   ├── KhuyenMaiService.java
    │   │   │   ├── ChiTietDonHangService.java
    │   │   │   └── QueryService.java              # Service chạy 30 câu truy vấn
    │   │   └── util/                              # Tiện ích nhập liệu & in ấn
    │   │       ├── InputUtil.java                 # Xử lý nhập console an toàn
    │   │       └── TablePrinter.java              # In bảng kẻ khung ASCII chuyên nghiệp
    │   └── resources/
    │       └── database.properties                # Cấu hình chuỗi kết nối MySQL
    └── test/
        └── java/com/nhom17/                       # Tầng kiểm thử tự động
            ├── QueryTest.java                     # Test 30 câu truy vấn
            └── StreamApiTest.java                 # Test toàn bộ hàm Stream API / Lambda
```

---

## 4. Cấu hình CSDL & Kết nối

### Cách 1: Sử dụng MySQL Server (Khuyên dùng)
1. Mở file cấu hình:
   ```text
   src/main/resources/database.properties
   ```
2. Cấu hình user/password tương ứng với máy của bạn:
   ```properties
   db.url=jdbc:mysql://localhost:3306/fastfood_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8&createDatabaseIfNotExist=true
   db.user=root
   db.password=123456
   ```
3. Khi khởi chạy, chương trình sẽ **tự động khởi tạo toàn bộ 11 bảng và nạp sẵn dữ liệu mẫu đầy đủ** thông qua `DatabaseInitializer.initialize()`.

### Cách 2: Chạy trực tiếp (Chế độ tự động Fallback H2 In-Memory)
Nếu máy chưa bật MySQL, `DBConnection` sẽ tự động chuyển sang chế độ **H2 In-Memory Database (MySQL Mode)** để toàn bộ ứng dụng và các câu test vẫn chạy trơn tru 100%.

---

## 5. Hướng dẫn biên dịch và khởi chạy

### Chạy bằng Maven trên Terminal:
```bash
# Di chuyển vào thư mục project
cd project

# Biên dịch và chạy toàn bộ unit tests
mvn test

# Khởi chạy ứng dụng CLI trực tiếp
mvn exec:java
```

### Chạy trên IntelliJ IDEA hoặc VS Code:
1. Mở thư mục `project`.
2. Chọn `Load Maven Project` hoặc `Reload All Maven Projects`.
3. Đảm bảo Project SDK chọn **JDK 17** hoặc mới hơn.
4. Nhấp chuột phải vào file `com.nhom17.Main` -> Chọn **Run 'Main'**.

---

## 6. Menu chức năng chương trình

```text
=============================================================
          FAST FOOD CHAIN MIS - MENU CHINH
=============================================================
 1. Quan ly thuc the co ban (CRUD Khach hang, Mon an, Don hang...)
 2. Thuc thi 30 cau truy van (Section 9 - 3 Thanh vien)
 3. Thao tac nang cao voi Java Stream API / Lambda Functions
 4. Tao lai toan bo database & nap lai du lieu mau tu dau
 0. Thoat chuong trinh
=============================================================
```

### Chi tiết Menu Stream API & Lambda:
```text
 1. Loc mon an gia >= 50.000 va dang ban          [filter Lambda]
 2. Loc khach hang co email @gmail.com           [Predicate Lambda]
 3. Tim kiem nhan vien theo tu khoa ten          [filter Lambda]
 4. Kiem tra su ton tai cua ten dang nhap        [anyMatch]
 5. Sap xep mon an theo gia tang dan             [Comparator]
 6. Sap xep mon an: Gia giam dan + Ten tang dan  [Combined Comparator]
 7. Tim mon an co gia cao nhat                   [Stream max]
 8. Tinh gia tri don hang trung binh             [mapToDouble + average]
 9. Tong doanh thu don hoan thanh                [reduce]
10. Nhom mon an theo Danh Muc                    [groupingBy]
11. Nhom nhan vien theo Chi Nhanh                [groupingBy]
12. Thong ke so luong don theo trang thai        [groupingBy + counting]
13. Thong ke doanh thu theo phuong thuc TT       [groupingBy + reducing]
 0. Quay lai Menu Chinh
```

---

## 7. Các mẫu Lambda & Stream API tiêu biểu

### 1. Predicate Validation
```java
Predicate<BigDecimal> validPrice = price -> price != null && price.compareTo(BigDecimal.ZERO) > 0;
```

### 2. Stream Filter
```java
List<MonAn> result = monAnDAO.findAll().stream()
        .filter(m -> m.getGiaBan().compareTo(new BigDecimal("50000")) >= 0)
        .toList();
```

### 3. anyMatch (Kiểm tra trùng lặp)
```java
boolean exists = taiKhoanDAO.findAll().stream()
        .anyMatch(tk -> tk.getTenDangNhap().equalsIgnoreCase(username));
```

### 4. BiConsumer Dynamic Field Updater
```java
Map<String, BiConsumer<MonAn, String>> fieldUpdaters = Map.of(
        "ten", (m, v) -> m.setTenMon(v),
        "gia", (m, v) -> m.setGiaBan(new BigDecimal(v)),
        "mota", (m, v) -> m.setMoTa(v),
        "trangthai", (m, v) -> m.setTrangThai(TrangThaiMon.valueOf(v.toUpperCase())),
        "danhmuc", (m, v) -> m.setMaDM(v)
);
```

### 5. Multi-level Comparator (Sắp xếp kết hợp)
```java
Comparator<MonAn> comparator = Comparator.comparing(MonAn::getGiaBan)
        .reversed()
        .thenComparing(MonAn::getTenMon);

List<MonAn> sorted = monAnDAO.findAll().stream()
        .sorted(comparator)
        .toList();
```

### 6. Stream Reduce (Tính tổng tiền)
```java
BigDecimal totalRevenue = donHangDAO.findAll().stream()
        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
        .map(DonHang::getTongTien)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
```

### 7. Collectors.groupingBy & counting
```java
Map<TrangThaiDonHang, Long> countMap = donHangDAO.findAll().stream()
        .collect(Collectors.groupingBy(DonHang::getTrangThai, Collectors.counting()));
```

---

## 8. Luồng xử lý phân tầng (Layered Architecture Flow)

```text
       User (Console CLI)
              |
              v
     com.nhom17.Main (View & Menu)
              |
              v
   com.nhom17.service.* (Domain & FastFoodService)
              | \
              |  \ Java Lambda / Stream API
              |   -> Predicate / BiConsumer / Comparator / groupingBy / reduce
              v
     com.nhom17.dao.* (Data Access Objects)
              |
              v
   com.nhom17.database.DBConnection (JDBC PreparedStatement)
              |
              v
     MySQL 8 Database / H2 Fallback
```

- **Tầng DAO/JDBC**: Đảm nhận lưu trữ, truy xuất dữ liệu từ Database một cách tối ưu và an toàn.
- **Tầng Service/Lambda**: Đảm nhận nghiệp vụ, kiểm tra tính hợp lệ và xử lý Collection trong Java bằng Lambda Expressions và Stream API hiện đại.
- **Tầng Main**: Trình bày giao diện CLI tương tác, bảng dữ liệu ASCII và điều hướng người dùng.
