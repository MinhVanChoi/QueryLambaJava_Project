using MySqlConnector;
using FastFoodMIS_CSharp.Database;

namespace FastFoodMIS_CSharp.DAO;

public sealed record QueryResult(string Title, string Sql, List<string> Headers, List<string[]> Rows)
{
    public void Print()
    {
        Console.WriteLine($"\n--- {Title} ---");
        Console.WriteLine(string.Join(" | ", Headers));
        foreach (var row in Rows) Console.WriteLine(string.Join(" | ", row));
        if (Rows.Count == 0) Console.WriteLine("(Không có dữ liệu)");
        Console.WriteLine($"Tổng: {Rows.Count} dòng.");
    }
}

public class QueryDAO
{
    public static readonly string[] Titles =
    {
        "Cau 1: Nhan vien chua duoc cap tai khoan",
        "Cau 2: Mon Burger gia tu 40k den 60k",
        "Cau 3: Mon ton kho < 10 tai tung chi nhanh",
        "Cau 4: Khach hang tai TP.HCM",
        "Cau 5: Phuong thuc thanh toan tung thanh cong",
        "Cau 6: Don hang hoan thanh trong thang 02/2026",
        "Cau 7: Don hang kem ten khach hang va nhan vien",
        "Cau 8: Chi tiet don hang 1001",
        "Cau 9: Doi chieu tien hang chi tiet voi tien hang cua don",
        "Cau 10: Tong doanh thu cac don da hoan thanh",
        "Cau 11: Doanh thu theo tung thang trong nam 2026",
        "Cau 12: Top 3 mon ban chay nhat",
        "Cau 13: Tong chi tieu tung khach hang",
        "Cau 14: Khach hang chua tung dat hang",
        "Cau 15: Mon an chua tung ban thanh cong",
        "Cau 16: Thong ke so don va doanh thu theo nhan vien",
        "Cau 17: Khach hang co it nhat 2 don hoan thanh",
        "Cau 18: Chi tiet co don gia khac gia hien tai",
        "Cau 19: Tai khoan dang bi khoa (LOCKED)",
        "Cau 20: Don hang co tong tien > muc trung binh",
        "Cau 21: Tai khoan chua tung dang nhap",
        "Cau 22: Don hang co ap dung khuyen mai",
        "Cau 23: Tong tien giam cua tung khuyen mai",
        "Cau 24: Khuyen mai chua tung duoc ap dung",
        "Cau 25: So sanh don co va khong co khuyen mai",
        "Cau 26: Don co tien giam bi chan boi muc tran toi da",
        "Cau 27: Tong tien thu theo phuong thuc thanh toan",
        "Cau 28: Doanh thu theo tung chi nhanh",
        "Cau 29: Khach hang chi tieu cao hon trung binh",
        "Cau 30: Don thanh toan that bai roi sau do thanh cong",
    };

    public string GetSql(int number) => number switch
    {
        1 => Sql01,
        2 => Sql02,
        3 => Sql03,
        4 => Sql04,
        5 => Sql05,
        6 => Sql06,
        7 => Sql07,
        8 => Sql08,
        9 => Sql09,
        10 => Sql10,
        11 => Sql11,
        12 => Sql12,
        13 => Sql13,
        14 => Sql14,
        15 => Sql15,
        16 => Sql16,
        17 => Sql17,
        18 => Sql18,
        19 => Sql19,
        20 => Sql20,
        21 => Sql21,
        22 => Sql22,
        23 => Sql23,
        24 => Sql24,
        25 => Sql25,
        26 => Sql26,
        27 => Sql27,
        28 => Sql28,
        29 => Sql29,
        30 => Sql30,
        _ => throw new ArgumentOutOfRangeException(nameof(number), "Chọn câu 1-30")
    };

    public QueryResult Run(int number)
    {
        if (number < 1 || number > 30) throw new ArgumentOutOfRangeException(nameof(number));
        string sql = GetSql(number);
        using var conn = DBConnection.GetConnection();
        using var cmd = new MySqlCommand(sql, conn);
        using var reader = cmd.ExecuteReader();
        var headers = Enumerable.Range(0, reader.FieldCount).Select(reader.GetName).ToList();
        var rows = new List<string[]>();
        while (reader.Read())
        {
            rows.Add(Enumerable.Range(0, reader.FieldCount)
                .Select(i => reader.IsDBNull(i) ? "NULL" : Convert.ToString(reader.GetValue(i)) ?? "NULL")
                .ToArray());
        }
        return new QueryResult(Titles[number - 1], sql, headers, rows);
    }

    public QueryResult Query01_NhanVienChuaCoTaiKhoan() => Run(1);
    public QueryResult Query02_MonBurgerGia40kDen60k() => Run(2);
    public QueryResult Query03_TonKhoNhoHon10() => Run(3);
    public QueryResult Query04_KhachHangTPHCM() => Run(4);
    public QueryResult Query05_PhuongThucThanhToanThanhCong() => Run(5);
    public QueryResult Query06_DonHangHoanThanhThang2() => Run(6);
    public QueryResult Query07_DonHangVaKhachHangNhanVien() => Run(7);
    public QueryResult Query08_ChiTietDonHang1001() => Run(8);
    public QueryResult Query09_DoiChieuTienHangVaTongTien() => Run(9);
    public QueryResult Query10_TongDoanhThuDonHoanThanh() => Run(10);
    public QueryResult Query11_DoanhThuTheoThang() => Run(11);
    public QueryResult Query12_Top3MonBanChayNhat() => Run(12);
    public QueryResult Query13_DoanhThuTungKhachHang() => Run(13);
    public QueryResult Query14_KhachHangChuaTungDatHang() => Run(14);
    public QueryResult Query15_MonChuaBanThanhCong() => Run(15);
    public QueryResult Query16_ThongKeNhanVienDoanhThu() => Run(16);
    public QueryResult Query17_KhachHangCoItNhat2DonHoanThanh() => Run(17);
    public QueryResult Query18_LichSuGiaMon() => Run(18);
    public QueryResult Query19_TaiKhoanBiKhoa() => Run(19);
    public QueryResult Query20_DonHangCaoHonTrungBinh() => Run(20);
    public QueryResult Query21_TaiKhoanChuaTungDangNhap() => Run(21);
    public QueryResult Query22_DonHangDungKhuyenMai() => Run(22);
    public QueryResult Query23_TongTienGiamTungKhuyenMai() => Run(23);
    public QueryResult Query24_KhuyenMaiChuaTungDung() => Run(24);
    public QueryResult Query25_SoSanhCoVaKhongKhuyenMai() => Run(25);
    public QueryResult Query26_DonBiChanGiamToiDa() => Run(26);
    public QueryResult Query27_TienThuTheoPhuongThuc() => Run(27);
    public QueryResult Query28_DoanhThuTheoChiNhanh() => Run(28);
    public QueryResult Query29_KhachHangChiTieuTrenTrungBinh() => Run(29);
    public QueryResult Query30_DonThanhToanThatBaiRoiThanhCong() => Run(30);

    private const string Sql01 = @"SELECT nv.maNV, nv.hoTen, nv.soDienThoai, nv.chucVu, cn.tenCN
            FROM nhan_vien nv
            JOIN chi_nhanh cn ON nv.maCN = cn.maCN
            LEFT JOIN tai_khoan tk ON nv.maNV = tk.maNV
            WHERE tk.maTK IS NULL
            ORDER BY nv.maNV;";

    private const string Sql02 = @"SELECT m.maMon, m.tenMon, m.giaBan, d.tenDM, m.trangThai
            FROM mon_an m
            JOIN danh_muc d ON m.maDM = d.maDM
            WHERE d.tenDM = 'Burger' AND m.giaBan BETWEEN 40000 AND 60000
            ORDER BY m.giaBan DESC;";

    private const string Sql03 = @"SELECT tk.maTon, cn.tenCN, m.maMon, m.tenMon, tk.soLuongTon
            FROM ton_kho tk
            JOIN chi_nhanh cn ON tk.maCN = cn.maCN
            JOIN mon_an m ON tk.maMon = m.maMon
            WHERE tk.soLuongTon < 10
            ORDER BY cn.maCN, m.maMon;";

    private const string Sql04 = @"SELECT maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy
            FROM khach_hang
            WHERE diaChi LIKE '%TP.HCM%' OR diaChi LIKE '%TPHCM%'
            ORDER BY maKH;";

    private const string Sql05 = @"SELECT DISTINCT phuongThuc, 'Da tung thanh cong' AS ghiChu
            FROM thanh_toan
            WHERE trangThai = 'DA_THANH_TOAN'
            ORDER BY phuongThuc;";

    private const string Sql06 = @"SELECT maDH, ngayDat, tienHang, tienGiam, tongTien, trangThai
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH'
              AND ngayDat >= '2026-02-01 00:00:00'
              AND ngayDat <= '2026-02-28 23:59:59'
            ORDER BY maDH;";

    private const string Sql07 = @"SELECT dh.maDH, dh.ngayDat,
                   COALESCE(kh.hoTen, '(Khach vang lai)') AS tenKhach,
                   nv.hoTen AS tenNhanVien,
                   cn.tenCN,
                   dh.tongTien
            FROM don_hang dh
            INNER JOIN nhan_vien nv ON dh.maNV = nv.maNV
            INNER JOIN chi_nhanh cn ON dh.maCN = cn.maCN
            LEFT JOIN khach_hang kh ON dh.maKH = kh.maKH
            ORDER BY dh.maDH;";

    private const string Sql08 = @"SELECT ct.maCTDH, ct.maDH, m.tenMon, ct.soLuong, ct.donGia, ct.thanhTien
            FROM chi_tiet_don_hang ct
            JOIN mon_an m ON ct.maMon = m.maMon
            WHERE ct.maDH = 1001
            ORDER BY ct.maCTDH;";

    private const string Sql09 = @"SELECT dh.maDH,
                   SUM(ct.thanhTien) AS tinhTienHang,
                   dh.tienHang,
                   CASE WHEN SUM(ct.thanhTien) = dh.tienHang THEN 'KHOP' ELSE 'LECH' END AS doiChieu,
                   dh.tienGiam,
                   dh.tongTien
            FROM don_hang dh
            JOIN chi_tiet_don_hang ct ON dh.maDH = ct.maDH
            GROUP BY dh.maDH, dh.tienHang, dh.tienGiam, dh.tongTien
            ORDER BY dh.maDH;";

    private const string Sql10 = @"SELECT COUNT(*) AS soDon,
                   SUM(tienHang) AS tongTienHang,
                   SUM(tienGiam) AS tongTienGiam,
                   SUM(tongTien) AS tongDoanhThu
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH';";

    private const string Sql11 = @"SELECT CONCAT('Thang ', MONTH(ngayDat), '/', YEAR(ngayDat)) AS thoiGian,
                   COUNT(*) AS soDon,
                   SUM(tongTien) AS doanhThu
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH'
            GROUP BY CONCAT('Thang ', MONTH(ngayDat), '/', YEAR(ngayDat)), YEAR(ngayDat), MONTH(ngayDat)
            ORDER BY YEAR(ngayDat), MONTH(ngayDat);";

    private const string Sql12 = @"SELECT m.maMon, m.tenMon, SUM(ct.soLuong) AS tongSL, m.giaBan
            FROM chi_tiet_don_hang ct
            JOIN don_hang dh ON ct.maDH = dh.maDH
            JOIN mon_an m ON ct.maMon = m.maMon
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY m.maMon, m.tenMon, m.giaBan
            ORDER BY tongSL DESC
            LIMIT 3;";

    private const string Sql13 = @"SELECT kh.maKH, kh.hoTen,
                   COUNT(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.maDH END) AS soDon,
                   COALESCE(SUM(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.tongTien ELSE 0 END), 0) AS tongChiTieu
            FROM khach_hang kh
            LEFT JOIN don_hang dh ON kh.maKH = dh.maKH
            GROUP BY kh.maKH, kh.hoTen
            ORDER BY tongChiTieu DESC, kh.maKH;";

    private const string Sql14 = @"SELECT kh.maKH, kh.hoTen, kh.soDienThoai, kh.email, kh.diaChi, kh.ngayDangKy
            FROM khach_hang kh
            LEFT JOIN don_hang dh ON kh.maKH = dh.maKH
            WHERE dh.maDH IS NULL
            ORDER BY kh.maKH;";

    private const string Sql15 = @"SELECT m.maMon, m.tenMon, m.giaBan, m.trangThai, m.maDM
            FROM mon_an m
            WHERE m.maMon NOT IN (
                SELECT DISTINCT ct.maMon
                FROM chi_tiet_don_hang ct
                JOIN don_hang dh ON ct.maDH = dh.maDH
                WHERE dh.trangThai = 'DA_HOAN_THANH'
            )
            ORDER BY m.maMon;";

    private const string Sql16 = @"SELECT nv.maNV, nv.hoTen, nv.chucVu,
                   COUNT(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.maDH END) AS soDon,
                   COALESCE(SUM(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.tongTien ELSE 0 END), 0) AS tongDoanhThu
            FROM nhan_vien nv
            LEFT JOIN don_hang dh ON nv.maNV = dh.maNV
            GROUP BY nv.maNV, nv.hoTen, nv.chucVu
            ORDER BY tongDoanhThu DESC, nv.maNV;";

    private const string Sql17 = @"SELECT kh.maKH, kh.hoTen,
                   COUNT(dh.maDH) AS soDon,
                   SUM(dh.tongTien) AS tongChiTieu
            FROM khach_hang kh
            JOIN don_hang dh ON kh.maKH = dh.maKH
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY kh.maKH, kh.hoTen
            HAVING COUNT(dh.maDH) >= 2
            ORDER BY soDon DESC, tongChiTieu DESC;";

    private const string Sql18 = @"SELECT ct.maCTDH, ct.maDH, m.maMon, m.tenMon, ct.donGia, m.giaBan, (m.giaBan - ct.donGia) AS chenhLech
            FROM chi_tiet_don_hang ct
            JOIN mon_an m ON ct.maMon = m.maMon
            WHERE ct.donGia <> m.giaBan
            ORDER BY ct.maDH, ct.maCTDH;";

    private const string Sql19 = @"SELECT tk.maTK, tk.tenDangNhap, nv.hoTen, nv.chucVu, cn.tenCN, tk.trangThai
            FROM tai_khoan tk
            JOIN nhan_vien nv ON tk.maNV = nv.maNV
            JOIN chi_nhanh cn ON nv.maCN = cn.maCN
            WHERE tk.trangThai = 'LOCKED'
            ORDER BY tk.maTK;";

    private const string Sql20 = @"SELECT maDH, ngayDat, tongTien, trangThai, maCN
            FROM don_hang
            WHERE tongTien > (SELECT AVG(tongTien) FROM don_hang)
            ORDER BY tongTien DESC;";

    private const string Sql21 = @"SELECT tk.maTK, tk.tenDangNhap, nv.hoTen, nv.chucVu, cn.tenCN, tk.ngayTao
            FROM tai_khoan tk
            JOIN nhan_vien nv ON tk.maNV = nv.maNV
            JOIN chi_nhanh cn ON nv.maCN = cn.maCN
            WHERE tk.lanDangNhapCuoi IS NULL
            ORDER BY tk.maTK;";

    private const string Sql22 = @"SELECT dh.maDH, km.tenKM, dh.tienHang, dh.tienGiam, dh.tongTien, dh.trangThai
            FROM don_hang dh
            JOIN khuyen_mai km ON dh.maKM = km.maKM
            ORDER BY dh.maDH;";

    private const string Sql23 = @"SELECT km.maKM, km.tenKM,
                   COUNT(dh.maDH) AS soDon,
                   COALESCE(SUM(dh.tienGiam), 0) AS tongGiam
            FROM khuyen_mai km
            LEFT JOIN don_hang dh ON km.maKM = dh.maKM
            GROUP BY km.maKM, km.tenKM
            ORDER BY tongGiam DESC, km.maKM;";

    private const string Sql24 = @"SELECT km.maKM, km.tenKM, CONCAT(km.phanTramGiam, '%'), km.donToiThieu, km.trangThai
            FROM khuyen_mai km
            LEFT JOIN don_hang dh ON km.maKM = dh.maKM
            WHERE dh.maDH IS NULL
            ORDER BY km.maKM;";

    private const string Sql25 = @"SELECT CASE WHEN maKM IS NOT NULL THEN 'Co dung Khuyen mai' ELSE 'Khong dung Khuyen mai' END AS loaiDon,
                   COUNT(*) AS soDon,
                   SUM(tienHang) AS tongTienHang,
                   SUM(tienGiam) AS tongTienGiam,
                   SUM(tongTien) AS doanhThu
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH'
            GROUP BY CASE WHEN maKM IS NOT NULL THEN 'Co dung Khuyen mai' ELSE 'Khong dung Khuyen mai' END
            ORDER BY doanhThu DESC;";

    private const string Sql26 = @"SELECT dh.maDH, km.maKM, dh.tienHang,
                   CONCAT(km.phanTramGiam, '%'),
                   (dh.tienHang * km.phanTramGiam / 100) AS giamTheoPT,
                   km.giamToiDa,
                   dh.tienGiam
            FROM don_hang dh
            JOIN khuyen_mai km ON dh.maKM = km.maKM
            WHERE km.giamToiDa IS NOT NULL
              AND dh.tienGiam = km.giamToiDa
              AND (dh.tienHang * km.phanTramGiam / 100) > km.giamToiDa
            ORDER BY dh.maDH;";

    private const string Sql27 = @"SELECT phuongThuc,
                   COUNT(*) AS soGiaoDich,
                   SUM(soTien) AS tongTienThu
            FROM thanh_toan
            WHERE trangThai = 'DA_THANH_TOAN'
            GROUP BY phuongThuc
            ORDER BY tongTienThu DESC;";

    private const string Sql28 = @"SELECT cn.maCN, cn.tenCN,
                   COUNT(dh.maDH) AS soDon,
                   SUM(dh.tongTien) AS doanhThu
            FROM chi_nhanh cn
            JOIN don_hang dh ON cn.maCN = dh.maCN
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY cn.maCN, cn.tenCN
            ORDER BY doanhThu DESC;";

    private const string Sql29 = @"SELECT kh.maKH, kh.hoTen, SUM(dh.tongTien) AS tongChiTieu
            FROM khach_hang kh
            JOIN don_hang dh ON kh.maKH = dh.maKH
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY kh.maKH, kh.hoTen
            HAVING SUM(dh.tongTien) > (
                SELECT AVG(t.chiTieuKhach)
                FROM (
                    SELECT SUM(dh2.tongTien) AS chiTieuKhach
                    FROM don_hang dh2
                    WHERE dh2.trangThai = 'DA_HOAN_THANH' AND dh2.maKH IS NOT NULL
                    GROUP BY dh2.maKH
                ) t
            )
            ORDER BY tongChiTieu DESC;";

    private const string Sql30 = @"SELECT DISTINCT dh.maDH, dh.ngayDat, dh.tongTien, dh.trangThai, 'Co GD THAT_BAI va DA_THANH_TOAN' AS ghiChu
            FROM don_hang dh
            JOIN thanh_toan t1 ON dh.maDH = t1.maDH AND t1.trangThai = 'THAT_BAI'
            JOIN thanh_toan t2 ON dh.maDH = t2.maDH AND t2.trangThai = 'DA_THANH_TOAN'
            ORDER BY dh.maDH;";

}
