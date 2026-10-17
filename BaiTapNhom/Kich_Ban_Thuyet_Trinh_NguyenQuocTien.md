# KỊCH BẢN QUAY VIDEO THUYẾT TRÌNH & DEMO ĐỒ ÁN OOP (FAST FOOD MIS)
**Sinh viên thực hiện:** Nguyễn Quốc Tiên  
**MSSV:** 22110244 | **Nhóm:** 17  
**Phần phụ trách:** 
1. **Slide 10:** Mô hình dữ liệu hướng đối tượng (Entities & Enums)
2. **Slide 11:** Kiến trúc phân tầng (Layered Architecture) — *Trọng tâm: Bản chất Tầng Service & 30 Bài toán nghiệp vụ*
3. **Demo Thực thi Code:** 10 Bài toán nghiệp vụ (Câu 11 đến Câu 20) bằng Java Stream API/Lambda và C# LINQ

---

## PHẦN 1: THUYẾT TRÌNH SLIDE 10 — MÔ HÌNH DỮ LIỆU HƯỚNG ĐỐI TƯỢNG (MODEL & ENUM)
* **Thời lượng dự kiến:** 1.5 – 2 phút
* **Màn hình hiển thị:** Canva Slide 10 kết hợp chuyển nhanh sang VS Code để chỉ file code.

### 1. Thao tác trên máy:
* Mở **Slide 10** trên trình duyệt / Canva.
* Mở sẵn VS Code, định vị vào thư mục:  
  `com/nhom17/model/` và `com/nhom17/enums/`.
* Chuẩn bị mở sẵn file `MonAn.java` và `TrangThaiDonHang.java`.

### 2. Lời thoại thuyết trình chi tiết:

> *"Kính thưa Thầy và các bạn, tiếp nối phần trình bày của bạn Minh Văn, em là **Nguyễn Quốc Tiên**, MSSV **22110244**. Sau đây em xin phép đại diện nhóm 17 trình bày về **Slide 10: Mô hình dữ liệu hướng đối tượng** và **Slide 11: Kiến trúc phân tầng của hệ thống Fast Food MIS**.*
>
> *(Chỉ vào sơ đồ trên Slide 10)*  
> *Để quản lý toàn diện một chuỗi cửa hàng thức ăn nhanh hiện đại, nhóm chúng em đã phân tích và ánh xạ thế giới thực thành **11 thực thể (Entities)** chính, bao gồm: `ChiNhanh`, `NhanVien`, `TaiKhoan`, `KhachHang`, `DanhMuc`, `MonAn`, `TonKho`, `KhuyenMai`, `DonHang`, `ChiTietDonHang` và `ThanhToan`.*
>
> *(Chuyển nhanh sang VS Code, mở file `MonAn.java`)*  
> *Trong thiết kế OOP, các Entity này tuân thủ chặt chẽ tính **Đóng gói (Encapsulation)**: toàn bộ thuộc tính đều để `private`, cung cấp đầy đủ `Getter/Setter`, Constructor có tham số và phương thức `toString()` để format hiển thị dữ liệu bảng.*
>
> *(Mở file `TrangThaiDonHang.java` trong thư mục `enums`)*  
> *Đặc biệt, để đảm bảo tính toàn vẹn dữ liệu, loại bỏ hoàn toàn các lỗi sai chính tả dạng chuỗi string tự do, nhóm đã xây dựng **7 kiểu dữ liệu liệt kê (Enums)**, ví dụ như: `TrangThaiDonHang` (gồm TẠO MỚI, ĐANG XỬ LÝ, ĐÃ HOÀN THÀNH, ĐÃ HỦY), `TrangThaiThanhToan`, `ChucVu`, `TrangThaiMon`, v.v. Việc kết hợp chặt chẽ giữa Model và Enum giúp hệ thống có tính gõ mạnh (Type-safe), ngăn chặn tối đa lỗi runtime trong quá trình vận hành."*

---

## PHẦN 2: THUYẾT TRÌNH SLIDE 11 — KIẾN TRÚC PHÂN TẦNG & BẢN CHẤT TẦNG SERVICE
* **Thời lượng dự kiến:** 2.5 – 3 phút
* **Màn hình hiển thị:** Slide 11 trên Canva.

### 1. Thao tác trên máy:
* Trỏ chuột lần lượt vào 4 tầng trên slide: **Presentation ➔ Service ➔ DAO ➔ Database**.
* Mở sẵn trong VS Code 2 file để đối chứng:
  - `com/nhom17/dao/DonHangDAO.java` (Tầng DAO thô)
  - `com/nhom17/service/QueryLambdaService.java` (Tầng Service thông minh)

### 2. Lời thoại thuyết trình chuyên sâu:

> *"Tiếp theo, em xin đi sâu vào **Slide 11: Kiến trúc phân tầng (Layered Architecture)** – cấu trúc xương sống định hình toàn bộ mã nguồn của dự án.*
>
> *Dự án của chúng em được phân rã thành 4 tầng riêng biệt:*
> 1. *Tầng giao diện người dùng (Presentation Layer - Main CLI / Controller)*
> 2. *Tầng xử lý nghiệp vụ (Business Logic Layer - Service)*
> 3. *Tầng truy xuất dữ liệu (Data Access Layer - DAO)*
> 4. *Tầng lưu trữ dữ liệu (Database Layer - MySQL)*
>
> *(Nhấn mạnh với giọng tự tin, rõ ràng)*  
> *Để Thầy và các bạn dễ hình dung sự phối hợp giữa các tầng, chúng ta có thể ví hệ thống như **quy trình vận hành của một nhà hàng thức ăn nhanh ngoài đời thực**:*
> * *Tầng **Main / CLI** giống như **Nhân viên thu ngân**: trực tiếp nhận yêu cầu gọi món từ khách hàng hoặc yêu cầu xuất báo cáo từ quản lý.*
> * *Tầng **DAO** giống như **Anh thủ kho**: chỉ có một nhiệm vụ duy nhất là vào kho Database lấy đúng thùng nguyên liệu thô mang ra. DAO không biết nấu ăn, không có logic tính toán thông minh, chỉ chạy lệnh SQL thô rồi đổ dữ liệu vào RAM thành các danh sách `List<Entity>`.*
> * *Và cốt lõi quan trọng nhất chính là **TẦNG SERVICE** — đóng vai trò như **BẾP TRƯỞNG kiêm PHÒNG KẾ TOÁN TRƯỞNG**.*
>
> *(Chuyển sang mở file `QueryLambdaService.java`)*  
> *Tại sao tầng Service lại là trái tim của hệ thống?*
> * *Thứ nhất: **Tách biệt nghiệp vụ khỏi CSDL (Decoupling)**. Tầng Service độc lập hoàn toàn với MySQL. Nếu tương lai chuỗi cửa hàng đổi sang dùng PostgreSQL, Oracle hay đọc từ file JSON, nhóm em **chỉ cần sửa tầng DAO**, còn toàn bộ 30 thuật toán xử lý Lambda ở tầng Service được **bảo toàn nguyên vẹn 100%**.*
> * *Thứ hai: **Xử lý toàn bộ trên bộ nhớ RAM bằng Java 17 Stream API & biểu thức Lambda**. Thay vì viết các vòng lặp for/while lồng nhau cồng kềnh, tầng Service tận dụng `filter`, `map`, `groupingBy`, `reduce` để lọc dữ liệu, gom nhóm và tính toán tức thời với hiệu năng cực cao.*
> * *Thứ ba: **Dễ dàng kiểm thử (Unit Testing)**. Chúng ta có thể kiểm tra logic tính doanh thu, xếp hạng khách hàng mà không bắt buộc phải kết nối CSDL thật.*
>
> *(Chuyển ý sang 30 bài toán nghiệp vụ)*  
> *Vậy **30 BÀI TOÁN NGHIỆP VỤ** mà nhóm giải quyết trong tầng Service là gì?*
> * *Trong môn Hệ thống thông tin quản lý (MIS), **'Nghiệp vụ'** là các bài toán điều hành thực tế mà người Giám đốc chuỗi cửa hàng cần lời giải đáp hàng ngày. Ví dụ: Đâu là 3 món bán chạy nhất để nhập thêm nguyên liệu? Khách hàng nào chi tiêu nhiều nhất để tặng thẻ VIP? Nhân viên nào chưa kích hoạt tài khoản? Doanh thu từng tháng biến động ra sao?*
> * *30 bài toán này được nhóm chia đều cho 3 thành viên: bạn Văn phụ trách 10 câu đầu (lọc cơ bản, tồn kho, menu); em phụ trách 10 câu tiếp theo từ **Câu 11 đến Câu 20**; và bạn Quí phụ trách 10 câu cuối (khuyến mãi nâng cao, đối soát thanh toán).*
> * *Sau đây, em xin phép trực tiếp demo và phân tích 10 bài toán nghiệp vụ từ **Câu 11 đến Câu 20** mà em trực tiếp hiện thực hóa trên mã nguồn!"*

---

## PHẦN 3: DEMO THỰC THI 10 BÀI TOÁN NGHIỆP VỤ (CÂU 11 – 20)
* **Thời lượng dự kiến:** 6 – 8 phút
* **Thao tác chuẩn bị:**
  - Mở Terminal trong thư mục: `D:\OOP\QueryLambaJava_Project\BaiTapNhom\project`
  - Chạy sẵn lệnh: `mvn compile exec:java`
  - Nhập phím `3` để vào menu: `=== MENU DEMO 30 CAU TRUY VAN (LAMBDA & LINQ) ===`
  - Chia đôi màn hình: Bên trái để file `QueryLambdaService.java` (từ dòng 104 đến 205), bên phải để cửa sổ Terminal chạy kết quả.

---

### CÂU 11: THỐNG KÊ DOANH THU THEO TỪNG THÁNG TRONG NĂM 2026
* **Ý nghĩa bài toán nghiệp vụ:** Giúp ban giám đốc theo dõi chu kỳ kinh doanh (tháng Tết, tháng hè) để điều chỉnh kế hoạch tài chính và mục tiêu bán hàng.
* **Vị trí code:** File `QueryLambdaService.java` — dòng 104 đến 112 (`q11_DoanhThuTheoThang`).
* **Giải thích code:**
  > *"Tại hàm `q11_DoanhThuTheoThang()`, em dùng `filter` để chỉ chọn các đơn có trạng thái `DA_HOAN_THANH` và năm đặt là 2026. Sau đó dùng `Collectors.groupingBy` theo `getMonthValue()`, sắp xếp theo `TreeMap` tăng dần, và tính tổng doanh thu bằng `Collectors.reducing` cộng dồn `BigDecimal`."*
* **Thao tác Terminal:** Nhập số `11` rồi Enter.
* **Kết quả Terminal:**
  ```text
  --- Cau 11 (Lambda) ---
  Thang 1: 175000
  Thang 2: 745000
  Thang 3: 205000
  ```
* **Lời bình:** *"Kết quả cho thấy doanh thu tháng 2 tăng vọt lên 745.000đ do trùng vào đợt Tết Nguyên Đán và các chương trình khuyến mãi đầu năm."*

---

### CÂU 12: TÌM TOP 3 MÓN ĂN BÁN CHẠY NHẤT TOÀN HỆ THỐNG
* **Ý nghĩa bài toán nghiệp vụ:** Xác định món ăn 'át chủ bài' (Best Seller) để bếp trung tâm luôn chuẩn bị sẵn nguyên liệu, tránh tình trạng hết hàng đột ngột.
* **Vị trí code:** Dòng 114 đến 127 (`q12_Top3MonBanChay`).
* **Giải thích code:**
  > *"Hàm này xử lý liên bảng trên RAM: Đầu tiên lọc lấy `Set` các mã đơn hàng đã hoàn tất (`doneOrders`). Sau đó duyệt danh sách chi tiết đơn, gom nhóm theo `MaMon`, tính tổng số lượng bán bằng `summingInt`, dùng `sorted` theo chiều giảm dần và lấy ra đúng `limit(3)` món."*
* **Thao tác Terminal:** Nhập số `12` rồi Enter.
* **Kết quả Terminal:**
  ```text
  --- Cau 12 (Lambda) ---
  M001: 8
  M002: 5
  M005: 4
  ```
* **Lời bình:** *"Mã món M001 (Burger Bò Phô Mai) đứng đầu với 8 phần bán ra, tiếp theo là M002 và M005."*

---

### CÂU 13: DOANH THU ĐÓNG GÓP TỪ TỪNG KHÁCH HÀNG
* **Ý nghĩa bài toán nghiệp vụ:** Đo lường giá trị vòng đời khách hàng (Customer Lifetime Value) để đánh giá mức độ gắn bó của từng khách.
* **Vị trí code:** Dòng 129 đến 137 (`q13_DoanhThuTungKhachHang`).
* **Giải thích code:**
  > *"Em duyệt qua toàn bộ khách hàng và dùng `Collectors.toMap`. Với mỗi khách, stream đơn hàng được kích hoạt để tính tổng `BigDecimal` của những đơn hoàn tất mang mã khách đó."*
* **Thao tác Terminal:** Nhập số `13` rồi Enter.
* **Kết quả Terminal:** Hiển thị tên khách và tổng tiền (ví dụ: *Nguyễn Văn An: 320.000đ, Lê Thị Mai: 450.000đ...*).

---

### CÂU 14: DANH SÁCH KHÁCH HÀNG CHƯA TỪNG ĐẶT HÀNG
* **Ý nghĩa bài toán nghiệp vụ:** Giúp bộ phận Marketing lọc ra tệp khách đã đăng ký tài khoản nhưng chưa mua đơn nào, từ đó gửi mã voucher chào mừng 30k để kích cầu.
* **Vị trí code:** Dòng 139 đến 147 (`q14_KhachHangChuaTungDatHang`).
* **Giải thích code:**
  > *"Em gom toàn bộ `MaKH` đã từng mua hàng vào một `Set<String> daMua` (loại bỏ null). Sau đó stream danh sách khách hàng và dùng vị từ `!daMua.contains(kh.getMaKH())` để lọc ra những khách chưa từng xuất hiện trong bất kỳ đơn hàng nào."*
* **Thao tác Terminal:** Nhập số `14` rồi Enter.
* **Kết quả Terminal:** Danh sách các khách hàng chưa có đơn (ví dụ: KH006, KH007).

---

### CÂU 15: MÓN ĂN CHƯA TỪNG BÁN ĐƯỢC PHẦN NÀO
* **Ý nghĩa bài toán nghiệp vụ:** Giúp bếp trưởng phát hiện món bị ế ẩm hoàn toàn để xem xét đổi công thức, giảm giá hoặc loại bỏ khỏi thực đơn nhằm tiết kiệm chi phí lưu kho.
* **Vị trí code:** Dòng 149 đến 161 (`q15_MonChuaBanThanhCong`).
* **Giải thích code:**
  > *"Tương tự như câu 14 nhưng kiểm tra chéo giữa bảng `DonHang` hoàn thành, `ChiTietDonHang` và `MonAn`. Em tạo tập hợp các món đã bán (`daBan`) và lọc ra những món trong thực đơn không nằm trong tập hợp này."*
* **Thao tác Terminal:** Nhập số `15` rồi Enter.
* **Kết quả Terminal:** Hiển thị thông tin món chưa bán được (ví dụ: Món Salad cá ngừ M012).

---

### CÂU 16: DOANH SỐ BÁN HÀNG CỦA TỪNG NHÂN VIÊN
* **Ý nghĩa bài toán nghiệp vụ:** Đánh giá hiệu suất làm việc (KPI) của nhân viên thu ngân để tính thưởng doanh số cuối tháng.
* **Vị trí code:** Dòng 163 đến 171 (`q16_DoanhThuTheoNhanVien`).
* **Giải thích code:**
  > *"Em duyệt danh sách nhân viên, gom tổng tiền từ các đơn hàng hoàn tất do nhân viên đó trực tiếp phục vụ bằng `reduce(BigDecimal.ZERO, BigDecimal::add)`."*
* **Thao tác Terminal:** Nhập số `16` rồi Enter.
* **Kết quả Terminal:** Hiển thị danh sách nhân viên cùng tổng doanh số tương ứng.

---

### CÂU 17: TÌM KHÁCH HÀNG THÂN THIẾT (CÓ TỪ 2 ĐƠN HOÀN TẤT TRỞ LÊN)
* **Ý nghĩa bài toán nghiệp vụ:** Xây dựng danh sách khách hàng thân thiết để cấp thẻ thành viên Silver/Gold và áp dụng chính sách tích điểm đổi quà.
* **Vị trí code:** Dòng 173 đến 181 (`q17_KhachHangCoItNhat2Don`).
* **Giải thích code:**
  > *"Em lọc đơn hoàn tất, nhóm theo `MaKH` và đếm số lượng đơn bằng `Collectors.counting()`. Sau đó lọc tiếp các `Entry` có số đơn `getValue() >= 2` và lấy danh sách mã khách hàng."*
* **Thao tác Terminal:** Nhập số `17` rồi Enter.
* **Kết quả Terminal:** Danh sách mã khách hàng thân thiết (ví dụ: KH001, KH003).

---

### CÂU 18: PHÁT HIỆN ĐƠN HÀNG CÓ ĐƠN GIÁ KHÁC GIÁ BÁN HIỆN TẠI (TRUY VẾT LỊCH SỬ GIÁ)
* **Ý nghĩa bài toán nghiệp vụ:** Đảm bảo tính toàn vẹn tài chính: Khi giá món ăn tăng hoặc giảm theo thời gian, các đơn hàng cũ vẫn phải lưu giữ đúng giá tại thời điểm khách mua mà không bị ghi đè bởi giá mới.
* **Vị trí code:** Dòng 183 đến 190 (`q18_ChiTietGiaKhacHienTai`).
* **Giải thích code:**
  > *"Em stream qua danh sách `ChiTietDonHang`, so sánh `DonGia` đã lưu trong chi tiết đơn với giá bán `GiaBan` hiện thời trong bảng `MonAn`. Nếu `compareTo != 0`, chứng tỏ món ăn này đã từng được điều chỉnh giá sau khi đơn hàng được tạo."*
* **Thao tác Terminal:** Nhập số `18` rồi Enter.
* **Kết quả Terminal:** In ra dòng chi tiết đơn hàng có sự chênh lệch giá để kiểm toán.

---

### CÂU 19: DANH SÁCH TÀI KHOẢN NGƯỜI DÙNG ĐANG BỊ KHÓA
* **Ý nghĩa bài toán nghiệp vụ:** Giúp bộ phận Quản trị hệ thống (IT Admin) kiểm soát bảo mật, rà soát các tài khoản nhân viên hoặc khách hàng bị khóa do nhập sai mật khẩu nhiều lần hoặc đã nghỉ việc.
* **Vị trí code:** Dòng 191 đến 195 (`q19_TaiKhoanBiKhoa`).
* **Giải thích code:**
  > *"Hàm lọc đơn giản nhưng cực kỳ quan trọng về bảo mật: `filter(tk -> tk.getTrangThai() == TrangThaiTaiKhoan.LOCKED)`."*
* **Thao tác Terminal:** Nhập số `19` rồi Enter.
* **Kết quả Terminal:** Danh sách tài khoản có trạng thái LOCKED.

---

### CÂU 20: TÌM CÁC ĐƠN HÀNG CÓ GIÁ TRỊ CAO HƠN MỨC TRUNG BÌNH TOÀN HỆ THỐNG
* **Ý nghĩa bài toán nghiệp vụ:** Xác định nhóm 'đơn hàng giá trị cao' (High-value orders) để cửa hàng ưu tiên đóng gói cẩn thận, tặng kèm quà tri ân và phân tích hành vi mua sắm của nhóm khách chịu chi.
* **Vị trí code:** Dòng 197 đến 205 (`q20_DonHangCaoHonTrungBinh`).
* **Giải thích code:**
  > *"Em chia làm 2 bước: Bước 1 tính giá trị trung bình của toàn bộ đơn hàng bằng `mapToDouble` và `.average().orElse(0.0)`. Bước 2 stream lại danh sách đơn hàng, lọc các đơn có tổng tiền lớn hơn mức trung bình này, và sắp xếp giảm dần theo giá tiền bằng `sorted(Comparator.comparing(...).reversed())`."*
* **Thao tác Terminal:** Nhập số `20` rồi Enter.
* **Kết quả Terminal:** Danh sách các đơn hàng giá trị cao nhất được sắp xếp từ cao xuống thấp.

---

### TIỆN ÍCH CHẠY HÀNG LOẠT 10 CÂU CỦA TIÊN (TÙY CHỌN 32):
* Để chứng minh tính liên hoàn của hệ thống, bạn gõ số `32` vào Terminal:
  > *"Đặc biệt, hệ thống đã tích hợp sẵn tùy chọn số **32: Chạy hàng loạt 10 câu của Nguyễn Quốc Tiên từ 11 đến 20**. Khi em nhấn 32, toàn bộ 10 bài toán nghiệp vụ này sẽ được thực thi tuần tự trong chưa đầy 1 giây, xuất kết quả đồng bộ và chuẩn xác."*

---

## PHẦN 4: LỜI KẾT & BÀN GIAO CHO THÀNH VIÊN TIẾP THEO

> *"Kính thưa Thầy và các bạn, vừa rồi em đã trình bày xong Mô hình dữ liệu ở Slide 10, Kiến trúc phân tầng và bản chất cốt lõi của Tầng Service ở Slide 11, cùng phần demo thực thi 10 bài toán nghiệp vụ từ Câu 11 đến Câu 20.*
>
> *Tiếp theo đây, xin mời bạn **Nguyễn Mai Thiện Quí** tiếp tục phần trình bày về Slide 12 và demo 10 bài toán nghiệp vụ chuyên sâu còn lại từ Câu 21 đến Câu 30. Em xin chân thành cảm ơn Thầy và các bạn đã lắng nghe!"*
