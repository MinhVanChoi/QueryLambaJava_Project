using System.Globalization;
using System.Text;
using FastFoodMIS_CSharp.Database;
using FastFoodMIS_CSharp.Enums;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Services;
using FastFoodMIS_CSharp.Utils;
using MySqlConnector;

Console.InputEncoding = Encoding.UTF8;
Console.OutputEncoding = Encoding.UTF8;

var chiNhanhService = new ChiNhanhService();
var danhMucService = new DanhMucService();
var monAnService = new MonAnService();
var khachHangService = new KhachHangService();
var nhanVienService = new NhanVienService();
var taiKhoanService = new TaiKhoanService();
var tonKhoService = new TonKhoService();
var khuyenMaiService = new KhuyenMaiService();
var donHangService = new DonHangService();
var chiTietService = new ChiTietDonHangService();
var thanhToanService = new ThanhToanService();
var queryService = new QueryLinqService();

string[] tenCau =
{
    "Nhân viên chưa được cấp tài khoản",
    "Món Burger giá 40.000 - 60.000 (giảm dần)",
    "Món tồn kho dưới 10 tại từng chi nhánh",
    "Khách hàng tại TP.HCM",
    "Phương thức thanh toán từng thành công",
    "Đơn hàng hoàn thành tháng 02/2026",
    "Đơn hàng kèm tên khách hàng và nhân viên",
    "Chi tiết đơn hàng 1001",
    "Đối chiếu tiền hàng chi tiết với đơn hàng",
    "Tổng doanh thu đơn hoàn thành",
    "Doanh thu theo từng tháng năm 2026",
    "Top 3 món bán chạy nhất",
    "Tổng chi tiêu của từng khách hàng",
    "Khách hàng chưa từng đặt hàng",
    "Món ăn chưa từng bán thành công",
    "Số đơn và doanh thu theo nhân viên",
    "Khách hàng có ít nhất 2 đơn hoàn thành",
    "Chi tiết có đơn giá khác giá hiện tại",
    "Tài khoản đang bị khóa (LOCKED)",
    "Đơn hàng có tổng tiền trên mức trung bình",
    "Tài khoản chưa từng đăng nhập",
    "Đơn hàng có áp dụng khuyến mãi",
    "Tổng tiền giảm của từng khuyến mãi",
    "Khuyến mãi chưa từng được áp dụng",
    "So sánh doanh thu có/không khuyến mãi",
    "Đơn bị giới hạn bởi mức giảm tối đa",
    "Tiền thu theo phương thức thanh toán",
    "Doanh thu theo từng chi nhánh",
    "Khách hàng chi tiêu cao hơn trung bình",
    "Đơn thanh toán thất bại rồi thành công"
};

Console.WriteLine(new string('=', 70));
Console.WriteLine(" FAST FOOD MANAGEMENT SYSTEM (MIS) - NHÓM 17");
Console.WriteLine(" Thành viên: Nguyễn Mai Thiên Qui - Nguyễn Quốc Tiến - Choi Minh Văn");
Console.WriteLine(" PHIÊN BẢN C# LINQ - ĐỐI CHIẾU VỚI JAVA STREAM API");
Console.WriteLine(new string('=', 70));

try
{
    using var conn = DBConnection.GetConnection();
    using var cmd = new MySqlCommand("SELECT COUNT(*) FROM chi_nhanh", conn);
    Console.WriteLine($"[OK] Đã kết nối MySQL. Số chi nhánh: {Convert.ToInt64(cmd.ExecuteScalar())}");
}
catch (Exception ex)
{
    Console.WriteLine($"[LỖI] Không thể kết nối database: {ex.Message}");
    Console.WriteLine("Hãy kiểm tra DBConnection.cs và các biến môi trường FASTFOOD_DB_*. ");
    Console.ReadLine();
    return;
}

while (true)
{
    Console.WriteLine("\n=============================================================");
    Console.WriteLine("           FAST FOOD CHAIN MIS - MENU CHÍNH (C#)");
    Console.WriteLine("=============================================================");
    Console.WriteLine("1. Quản lý thực thể cơ bản (CRUD khách hàng, món ăn, đơn hàng...)");
    Console.WriteLine("2. Thực thi 30 câu truy vấn C# LINQ (3 thành viên)");
    Console.WriteLine("3. Thao tác nâng cao với C# LINQ ");
    Console.WriteLine("4. Tạo lại database và nạp dữ liệu mẫu (cần xác nhận an toàn)");
    Console.WriteLine("0. Thoát chương trình");
    Console.WriteLine("=============================================================");
    int luaChon = ReadInt("Chọn chức năng [0-4]: ");
    if (luaChon == 0) break;
    try
    {
        switch (luaChon)
        {
            case 1: MenuCrud(); break;
            case 2: Menu30Linq(); break;
            case 3: MenuLinqNangCao(); break;
            case 4: MenuResetDatabase(); break;
            default: Console.WriteLine("(!) Lựa chọn không hợp lệ."); break;
        }
    }
    catch (Exception ex)
    {
        Console.WriteLine($"[LỖI] {ex.Message}");
    }
}
Console.WriteLine("Cảm ơn bạn đã sử dụng Fast Food MIS - phiên bản C# LINQ!");

void MenuCrud()
{
    while (true)
    {
        Console.WriteLine("\n---------------- QUẢN LÝ THỰC THỂ (CRUD) ----------------");
        Console.WriteLine(" 1. Danh sách Chi nhánh");
        Console.WriteLine(" 2. Danh sách Danh mục");
        Console.WriteLine(" 3. Danh sách Món ăn");
        Console.WriteLine(" 4. Thêm Món ăn mới");
        Console.WriteLine(" 5. Cập nhật một trường Món ăn");
        Console.WriteLine(" 6. Xóa Món ăn");
        Console.WriteLine(" 7. Danh sách Khách hàng");
        Console.WriteLine(" 8. Thêm Khách hàng (kiểm tra trùng SĐT)");
        Console.WriteLine(" 9. Danh sách Nhân viên");
        Console.WriteLine("10. Thêm Nhân viên mới");
        Console.WriteLine("11. Danh sách Tài khoản");
        Console.WriteLine("12. Khóa Tài khoản (LOCKED)");
        Console.WriteLine("13. Danh sách Tồn kho theo chi nhánh");
        Console.WriteLine("14. Cập nhật số lượng Tồn kho");
        Console.WriteLine("15. Danh sách Đơn hàng & Thanh toán");
        Console.WriteLine(" 0. Quay lại menu chính");
        int chon = ReadInt("Chọn chức năng [0-15]: ");
        if (chon == 0) return;
        try
        {
            switch (chon)
            {
                case 1: Print("DANH SÁCH CHI NHÁNH", chiNhanhService.GetAll()); break;
                case 2: Print("DANH SÁCH DANH MỤC", danhMucService.GetAll()); break;
                case 3: Print("DANH SÁCH MÓN ĂN", monAnService.GetAll()); break;
                case 4: ThemMonAn(); break;
                case 5: CapNhatMonAn(); break;
                case 6: XoaMonAn(); break;
                case 7: Print("DANH SÁCH KHÁCH HÀNG", khachHangService.GetAll()); break;
                case 8: ThemKhachHang(); break;
                case 9: Print("DANH SÁCH NHÂN VIÊN", nhanVienService.GetAll()); break;
                case 10: ThemNhanVien(); break;
                case 11: Print("DANH SÁCH TÀI KHOẢN", taiKhoanService.GetAll()); break;
                case 12: KhoaTaiKhoan(); break;
                case 13: Print("DANH SÁCH TỒN KHO", tonKhoService.GetAll()); break;
                case 14: CapNhatTonKho(); break;
                case 15:
                    Print("DANH SÁCH ĐƠN HÀNG", donHangService.GetAll());
                    Print("DANH SÁCH THANH TOÁN", thanhToanService.GetAll());
                    break;
                default: Console.WriteLine("(!) Chọn từ 0 đến 15."); break;
            }
        }
        catch (Exception ex) { Console.WriteLine($"[LỖI] {ex.Message}"); }
    }
}

void ThemMonAn()
{
    Console.WriteLine("\n--- THÊM MÓN ĂN MỚI ---");
    string ma = ReadRequired("Mã món (VD M019): ");
    string ten = ReadRequired("Tên món: ");
    decimal gia = ReadDecimal("Giá bán (>0): ", true);
    string? moTa = ReadOptional("Mô tả (Enter nếu không có): ");
    TrangThaiMon trangThai = ReadEnum<TrangThaiMon>("Trạng thái");
    Print("DANH MỤC HIỆN CÓ", danhMucService.GetAll());
    string maDm = ReadRequired("Mã danh mục (VD DM01): ");
    var mon = new MonAn(ma, ten, gia, moTa, trangThai, maDm);
    bool ok = monAnService.AddMonAn(mon);
    AfterWrite(ok, "Thêm món ăn");
}

void CapNhatMonAn()
{
    Console.WriteLine("\n--- CẬP NHẬT 1 TRƯỜNG MÓN ĂN ---");
    string ma = ReadRequired("Mã món: ");
    Console.WriteLine("Các trường: ten, gia, mota, trangthai, danhmuc");
    string truong = ReadRequired("Chọn trường: ");
    string giaTri = ReadRequired("Giá trị mới (trạng thái: DANG_BAN/NGUNG_BAN): ");
    bool ok = monAnService.UpdateSingleField(ma, truong, giaTri);
    AfterWrite(ok, "Cập nhật món ăn");
}

void XoaMonAn()
{
    string ma = ReadRequired("Mã món cần xóa: ");
    if (!Confirm($"Xóa món {ma}?")) return;
    bool ok = monAnService.DeleteMonAn(ma);
    AfterWrite(ok, "Xóa món ăn");
}

void ThemKhachHang()
{
    Console.WriteLine("\n--- THÊM KHÁCH HÀNG ---");
    string ma = ReadRequired("Mã khách hàng (VD KH011): ");
    string ten = ReadRequired("Họ tên: ");
    string sdt = ReadRequired("Số điện thoại: ");
    if (khachHangService.PhoneExists(sdt))
    {
        Console.WriteLine("(!) Số điện thoại đã tồn tại. Hủy thêm mới.");
        return;
    }
    string? email = ReadOptional("Email: ");
    string? diaChi = ReadOptional("Địa chỉ: ");
    var kh = new KhachHang(ma, ten, sdt, email, diaChi, DateOnly.FromDateTime(DateTime.Today));
    AfterWrite(khachHangService.AddKhachHang(kh), "Thêm khách hàng");
}

void ThemNhanVien()
{
    Console.WriteLine("\n--- THÊM NHÂN VIÊN ---");
    string ma = ReadRequired("Mã nhân viên (VD NV09): ");
    string ten = ReadRequired("Họ tên: ");
    string sdt = ReadRequired("Số điện thoại: ");
    ChucVu chucVu = ReadEnum<ChucVu>("Chức vụ");
    Print("DANH SÁCH CHI NHÁNH", chiNhanhService.GetAll());
    string maCn = ReadRequired("Mã chi nhánh (VD CN01): ");
    AfterWrite(nhanVienService.AddNhanVien(new NhanVien(ma, ten, sdt, chucVu, maCn)), "Thêm nhân viên");
}

void KhoaTaiKhoan()
{
    string ma = ReadRequired("Mã tài khoản cần khóa (VD TK05): ");
    if (!Confirm($"Khóa tài khoản {ma}?")) return;
    AfterWrite(taiKhoanService.LockTaiKhoan(ma), "Khóa tài khoản");
}

void CapNhatTonKho()
{
    Print("TỒN KHO", tonKhoService.GetAll());
    string ma = ReadRequired("Mã tồn kho (VD T001): ");
    int soLuong = ReadInt("Số lượng tồn mới (>= 0): ");
    if (soLuong < 0) { Console.WriteLine("(!) Số lượng không được âm."); return; }
    AfterWrite(tonKhoService.UpdateSoLuong(ma, soLuong), "Cập nhật tồn kho");
}

void AfterWrite(bool ok, string action)
{
    Console.WriteLine(ok ? $"[OK] {action} thành công." : $"[THÔNG BÁO] {action} không thành công.");
    if (ok) queryService.Refresh();
}

void Menu30Linq()
{
    while (true)
    {
        Console.WriteLine("\n================== 30 CÂU TRUY VẤN C# LINQ ==================");
        Console.WriteLine("--- CHOI MINH VĂN: CÂU 1 - 10 ---");
        PrintQueryTitles(1, 10);
        Console.WriteLine("--- NGUYỄN QUỐC TIẾN: CÂU 11 - 20 ---");
        PrintQueryTitles(11, 20);
        Console.WriteLine("--- NGUYỄN MAI THIÊN QUI: CÂU 21 - 30 ---");
        PrintQueryTitles(21, 30);
        Console.WriteLine("31. Chạy 10 câu của Choi Minh Văn (1 - 10)");
        Console.WriteLine("32. Chạy 10 câu của Nguyễn Quốc Tiến (11 - 20)");
        Console.WriteLine("33. Chạy 10 câu của Nguyễn Mai Thiên Qui (21 - 30)");
        Console.WriteLine("99. CHẠY TOÀN BỘ 30 CÂU C# LINQ");
        Console.WriteLine(" 0. Quay lại menu chính");
        int chon = ReadInt("Chọn câu / nhóm [0-33, 99]: ");
        if (chon == 0) return;
        queryService.Refresh();
        if (chon is >= 1 and <= 30) RunLinqRange(chon, chon);
        else if (chon == 31) RunLinqRange(1, 10);
        else if (chon == 32) RunLinqRange(11, 20);
        else if (chon == 33) RunLinqRange(21, 30);
        else if (chon == 99) RunLinqRange(1, 30);
        else Console.WriteLine("(!) Lựa chọn không hợp lệ.");
    }
}

void PrintQueryTitles(int start, int end)
{
    for (int i = start; i <= end; i++)
        Console.WriteLine($"{i,2}. {tenCau[i - 1]}");
}

void RunLinqRange(int start, int end)
{
    int ok = 0;
    int fail = 0;
    for (int i = start; i <= end; i++)
    {
        try
        {
            Console.WriteLine($"\n--- CÂU {i:00}: {tenCau[i - 1]} ---");
            ResultPrinter.Print(queryService.Run(i));
            ok++;
        }
        catch (Exception ex)
        {
            fail++;
            Console.WriteLine($"[LỖI] Câu {i:00}: {ex.Message}");
        }
    }
    Console.WriteLine($"\nTổng kết: thành công {ok}/{end - start + 1}, lỗi {fail}.");
}

void MenuLinqNangCao()
{
    while (true)
    {
        Console.WriteLine("\n============= C# LINQ / LAMBDA EXPRESSIONS DEMO =============");
        Console.WriteLine(" 1. Lọc món giá >= 50.000 và đang bán         [Where]");
        Console.WriteLine(" 2. Khách hàng có email @gmail.com           [Where]");
        Console.WriteLine(" 3. Tìm nhân viên theo từ khóa               [Contains]");
        Console.WriteLine(" 4. Kiểm tra tên đăng nhập tồn tại           [Any]");
        Console.WriteLine(" 5. Sắp xếp món theo giá tăng dần            [OrderBy]");
        Console.WriteLine(" 6. Giá giảm dần, tên tăng dần               [ThenBy]");
        Console.WriteLine(" 7. Món có giá cao nhất                      [MaxBy]");
        Console.WriteLine(" 8. Giá trị đơn hàng trung bình             [Average]");
        Console.WriteLine(" 9. Tổng doanh thu đơn hoàn thành           [Sum]");
        Console.WriteLine("10. Nhóm món ăn theo danh mục               [GroupBy]");
        Console.WriteLine("11. Nhóm nhân viên theo chi nhánh           [GroupBy]");
        Console.WriteLine("12. Đếm đơn hàng theo trạng thái            [GroupBy + Count]");
        Console.WriteLine("13. Doanh thu theo phương thức thanh toán   [GroupBy + Sum]");
        Console.WriteLine(" 0. Quay lại menu chính");
        int chon = ReadInt("Chọn chức năng [0-13]: ");
        if (chon == 0) return;
        try
        {
            switch (chon)
            {
                case 1:
                    Print("MÓN GIÁ >= 50.000, ĐANG BÁN", monAnService.Filter(
                        m => m.GiaBan >= 50000m && m.TrangThai == TrangThaiMon.DANG_BAN));
                    break;
                case 2:
                    Print("KHÁCH HÀNG GMAIL", khachHangService.Filter(
                        k => k.Email?.EndsWith("@gmail.com", StringComparison.OrdinalIgnoreCase) == true));
                    break;
                case 3:
                    Print("TÌM NHÂN VIÊN", nhanVienService.SearchByName(ReadRequired("Từ khóa tên: ")));
                    break;
                case 4:
                    string username = ReadRequired("Tên đăng nhập: ");
                    Console.WriteLine(taiKhoanService.UsernameExists(username)
                        ? "Tên đăng nhập ĐÃ TỒN TẠI." : "Tên đăng nhập CHƯA TỒN TẠI.");
                    break;
                case 5: Print("GIÁ TĂNG DẦN", monAnService.SortByPriceAsc()); break;
                case 6: Print("GIÁ GIẢM DẦN, TÊN TĂNG DẦN", monAnService.SortByPriceDescThenName()); break;
                case 7:
                    MonAn? max = monAnService.FindMostExpensive();
                    Console.WriteLine(max is null ? "(Không có món ăn)"
                        : $"Món giá cao nhất: {max.TenMon} ({max.MaMon}): {max.GiaBan:N0} VNĐ");
                    break;
                case 8: Console.WriteLine($"Giá trị đơn trung bình: {donHangService.CalculateAverageOrderValue():N0} VNĐ"); break;
                case 9: Console.WriteLine($"Doanh thu hoàn thành: {donHangService.CalculateTotalCompletedRevenue():N0} VNĐ"); break;
                case 10:
                    foreach (var group in monAnService.GroupByDanhMuc().OrderBy(g => g.Key))
                    {
                        Console.WriteLine($"\nDanh mục {group.Key}: {group.Value.Count} món");
                        foreach (var mon in group.Value) Console.WriteLine($"  {mon.MaMon} | {mon.TenMon} | {mon.GiaBan:N0} VNĐ");
                    }
                    break;
                case 11:
                    foreach (var group in nhanVienService.GroupByChiNhanh().OrderBy(g => g.Key))
                    {
                        Console.WriteLine($"\nChi nhánh {group.Key}: {group.Value.Count} nhân viên");
                        foreach (var nv in group.Value) Console.WriteLine($"  {nv.MaNV} | {nv.HoTen} | {nv.ChucVu}");
                    }
                    break;
                case 12:
                    foreach (var group in donHangService.CountOrdersByStatus().OrderBy(g => g.Key.ToString()))
                        Console.WriteLine($"{group.Key,-20}: {group.Value} đơn");
                    break;
                case 13:
                    foreach (var group in thanhToanService.RevenueByPaymentMethod().OrderBy(g => g.Key.ToString()))
                        Console.WriteLine($"{group.Key,-20}: {group.Value:N0} VNĐ");
                    break;
                default: Console.WriteLine("(!) Chọn từ 0 đến 13."); break;
            }
        }
        catch (Exception ex) { Console.WriteLine($"[LỖI] {ex.Message}"); }
    }
}

void MenuResetDatabase()
{
    Console.WriteLine("\n--- CẢNH BÁO: TẠO LẠI TOÀN BỘ DATABASE ---");
    Console.WriteLine("Java và C# dùng CHUNG MySQL. Thao tác sẽ xóa dữ liệu của CẢ HAI bản.");
    Console.WriteLine("Chỉ thực hiện sau khi sao lưu database và chắc chắn muốn khôi phục dữ liệu mẫu.");
    if (!string.Equals(Environment.GetEnvironmentVariable("FASTFOOD_ALLOW_RESET"), "YES", StringComparison.Ordinal))
    {
        Console.WriteLine("[AN TOÀN] Chức năng tạo lại dữ liệu đang bị khóa.");
        Console.WriteLine("Nếu thật sự cần, sao lưu MySQL và đặt biến FASTFOOD_ALLOW_RESET=YES, sau đó mở lại Visual Studio.");
        return;
    }
    using var conn = DBConnection.GetConnection();
    using var cmd = new MySqlCommand("SELECT DATABASE()", conn);
    string database = Convert.ToString(cmd.ExecuteScalar()) ?? "(không xác định)";
    Console.WriteLine($"Database hiện tại: {database}");
    Console.Write($"Nhập chính xác 'RESET {database}' để xác nhận XÓA TẤT CẢ dữ liệu: ");
    if (Console.ReadLine()?.Trim() != $"RESET {database}")
    {
        Console.WriteLine("Đã hủy, không thay đổi database.");
        return;
    }
    Console.WriteLine("Đang tạo lại 11 bảng và nạp dữ liệu mẫu...");
    DatabaseInitializer.Reinitialize();
    queryService.Refresh();
    Console.WriteLine("[OK] Đã tạo lại 11 bảng và nạp dữ liệu mẫu tương ứng bản Java.");
}

void Print(string label, object data)
{
    Console.WriteLine($"\n--- {label} ---");
    ResultPrinter.Print(data);
}

int ReadInt(string prompt)
{
    Console.Write(prompt);
    return int.TryParse(Console.ReadLine()?.Trim(), out var value) ? value : -1;
}

string ReadRequired(string prompt)
{
    Console.Write(prompt);
    string result = Console.ReadLine()?.Trim() ?? "";
    if (result.Length == 0) throw new ArgumentException("Không được để trống thông tin bắt buộc.");
    return result;
}

string? ReadOptional(string prompt)
{
    Console.Write(prompt);
    string result = Console.ReadLine()?.Trim() ?? "";
    return result.Length == 0 ? null : result;
}

decimal ReadDecimal(string prompt, bool positive = false)
{
    string raw = ReadRequired(prompt);
    if (!decimal.TryParse(raw, NumberStyles.Number, CultureInfo.InvariantCulture, out decimal value)
        && !decimal.TryParse(raw, NumberStyles.Number, CultureInfo.CurrentCulture, out value))
        throw new ArgumentException("Giá trị tiền phải là số (VD: 50000).");
    if (positive && value <= 0) throw new ArgumentException("Giá bán phải lớn hơn 0.");
    return value;
}

T ReadEnum<T>(string title) where T : struct, Enum
{
    Console.WriteLine($"{title}: {string.Join(", ", Enum.GetNames<T>())}");
    string raw = ReadRequired("Nhập trạng thái/chức vụ: ");
    if (!Enum.TryParse<T>(raw, true, out var value) || !Enum.IsDefined(value))
        throw new ArgumentException($"Giá trị không hợp lệ: {raw}");
    return value;
}

bool Confirm(string text)
{
    Console.Write($"{text} (y/N): ");
    return string.Equals(Console.ReadLine()?.Trim(), "y", StringComparison.OrdinalIgnoreCase);
}
