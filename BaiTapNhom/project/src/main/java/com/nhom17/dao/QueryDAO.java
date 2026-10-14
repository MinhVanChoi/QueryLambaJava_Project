package com.nhom17.dao;

import com.nhom17.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryDAO {

    public static class QueryResult {
        public String title;
        public List<String> headers = new ArrayList<>();
        public List<String[]> rows = new ArrayList<>();

        public QueryResult(String title) {
            this.title = title;
        }

        public void print() {
            System.out.println("\n--- " + title + " ---");
            System.out.println(String.join(" | ", headers));
            System.out.println("-".repeat(80));
            if (rows.isEmpty()) {
                System.out.println("(Khong co du lieu)");
            } else {
                rows.forEach(r -> System.out.println(String.join(" | ", r)));
            }
            System.out.println("-".repeat(80));
            System.out.println("Tong cong: " + rows.size() + " dong.");
        }
    }

    private QueryResult execute(String title, String sql) {
        QueryResult qr = new QueryResult(title);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                qr.headers.add(meta.getColumnLabel(i));
            }

            while (rs.next()) {
                String[] row = new String[cols];
                for (int i = 1; i <= cols; i++) {
                    Object val = rs.getObject(i);
                    row[i - 1] = (val != null) ? val.toString() : "NULL";
                }
                qr.rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return qr;
    }

    public QueryResult query01_NhanVienChuaCoTaiKhoan() {
        return execute("Cau 1: Nhan vien chua duoc cap tai khoan", """
            SELECT nv.maNV, nv.hoTen, nv.soDienThoai, nv.chucVu, cn.tenCN
            FROM nhan_vien nv
            JOIN chi_nhanh cn ON nv.maCN = cn.maCN
            LEFT JOIN tai_khoan tk ON nv.maNV = tk.maNV
            WHERE tk.maTK IS NULL
            ORDER BY nv.maNV;
        """);
    }

    public QueryResult query02_MonBurgerGia40kDen60k() {
        return execute("Cau 2: Mon Burger gia tu 40k den 60k", """
            SELECT m.maMon, m.tenMon, m.giaBan, d.tenDM, m.trangThai
            FROM mon_an m
            JOIN danh_muc d ON m.maDM = d.maDM
            WHERE d.tenDM = 'Burger' AND m.giaBan BETWEEN 40000 AND 60000
            ORDER BY m.giaBan DESC;
        """);
    }

    public QueryResult query03_TonKhoNhoHon10() {
        return execute("Cau 3: Mon ton kho < 10 tai tung chi nhanh", """
            SELECT tk.maTon, cn.tenCN, m.maMon, m.tenMon, tk.soLuongTon
            FROM ton_kho tk
            JOIN chi_nhanh cn ON tk.maCN = cn.maCN
            JOIN mon_an m ON tk.maMon = m.maMon
            WHERE tk.soLuongTon < 10
            ORDER BY cn.maCN, m.maMon;
        """);
    }

    public QueryResult query04_KhachHangTPHCM() {
        return execute("Cau 4: Khach hang tai TP.HCM", """
            SELECT maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy
            FROM khach_hang
            WHERE diaChi LIKE '%TP.HCM%' OR diaChi LIKE '%TPHCM%'
            ORDER BY maKH;
        """);
    }

    public QueryResult query05_PhuongThucThanhToanThanhCong() {
        return execute("Cau 5: Phuong thuc thanh toan tung thanh cong", """
            SELECT DISTINCT phuongThuc, 'Da tung thanh cong' AS ghiChu
            FROM thanh_toan
            WHERE trangThai = 'DA_THANH_TOAN'
            ORDER BY phuongThuc;
        """);
    }

    public QueryResult query06_DonHangHoanThanhThang2() {
        return execute("Cau 6: Don hang hoan thanh trong thang 02/2026", """
            SELECT maDH, ngayDat, tienHang, tienGiam, tongTien, trangThai
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH'
              AND ngayDat >= '2026-02-01 00:00:00'
              AND ngayDat <= '2026-02-28 23:59:59'
            ORDER BY maDH;
        """);
    }

    public QueryResult query07_DonHangVaKhachHangNhanVien() {
        return execute("Cau 7: Don hang kem ten khach hang va nhan vien", """
            SELECT dh.maDH, dh.ngayDat,
                   COALESCE(kh.hoTen, '(Khach vang lai)') AS tenKhach,
                   nv.hoTen AS tenNhanVien,
                   cn.tenCN,
                   dh.tongTien
            FROM don_hang dh
            INNER JOIN nhan_vien nv ON dh.maNV = nv.maNV
            INNER JOIN chi_nhanh cn ON dh.maCN = cn.maCN
            LEFT JOIN khach_hang kh ON dh.maKH = kh.maKH
            ORDER BY dh.maDH;
        """);
    }

    public QueryResult query08_ChiTietDonHang1001() {
        return execute("Cau 8: Chi tiet don hang 1001", """
            SELECT ct.maCTDH, ct.maDH, m.tenMon, ct.soLuong, ct.donGia, ct.thanhTien
            FROM chi_tiet_don_hang ct
            JOIN mon_an m ON ct.maMon = m.maMon
            WHERE ct.maDH = 1001
            ORDER BY ct.maCTDH;
        """);
    }

    public QueryResult query09_DoiChieuTienHangVaTongTien() {
        return execute("Cau 9: Doi chieu tien hang chi tiet voi tien hang cua don", """
            SELECT dh.maDH,
                   SUM(ct.thanhTien) AS tinhTienHang,
                   dh.tienHang,
                   CASE WHEN SUM(ct.thanhTien) = dh.tienHang THEN 'KHOP' ELSE 'LECH' END AS doiChieu,
                   dh.tienGiam,
                   dh.tongTien
            FROM don_hang dh
            JOIN chi_tiet_don_hang ct ON dh.maDH = ct.maDH
            GROUP BY dh.maDH, dh.tienHang, dh.tienGiam, dh.tongTien
            ORDER BY dh.maDH;
        """);
    }

    public QueryResult query10_TongDoanhThuDonHoanThanh() {
        return execute("Cau 10: Tong doanh thu cac don da hoan thanh", """
            SELECT COUNT(*) AS soDon,
                   SUM(tienHang) AS tongTienHang,
                   SUM(tienGiam) AS tongTienGiam,
                   SUM(tongTien) AS tongDoanhThu
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH';
        """);
    }

    public QueryResult query11_DoanhThuTheoThang() {
        return execute("Cau 11: Doanh thu theo tung thang trong nam 2026", """
            SELECT CONCAT('Thang ', MONTH(ngayDat), '/', YEAR(ngayDat)) AS thoiGian,
                   COUNT(*) AS soDon,
                   SUM(tongTien) AS doanhThu
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH'
            GROUP BY CONCAT('Thang ', MONTH(ngayDat), '/', YEAR(ngayDat)), YEAR(ngayDat), MONTH(ngayDat)
            ORDER BY YEAR(ngayDat), MONTH(ngayDat);
        """);
    }

    public QueryResult query12_Top3MonBanChayNhat() {
        return execute("Cau 12: Top 3 mon ban chay nhat", """
            SELECT m.maMon, m.tenMon, SUM(ct.soLuong) AS tongSL, m.giaBan
            FROM chi_tiet_don_hang ct
            JOIN don_hang dh ON ct.maDH = dh.maDH
            JOIN mon_an m ON ct.maMon = m.maMon
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY m.maMon, m.tenMon, m.giaBan
            ORDER BY tongSL DESC
            LIMIT 3;
        """);
    }

    public QueryResult query13_DoanhThuTungKhachHang() {
        return execute("Cau 13: Tong chi tieu tung khach hang", """
            SELECT kh.maKH, kh.hoTen,
                   COUNT(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.maDH END) AS soDon,
                   COALESCE(SUM(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.tongTien ELSE 0 END), 0) AS tongChiTieu
            FROM khach_hang kh
            LEFT JOIN don_hang dh ON kh.maKH = dh.maKH
            GROUP BY kh.maKH, kh.hoTen
            ORDER BY tongChiTieu DESC, kh.maKH;
        """);
    }

    public QueryResult query14_KhachHangChuaTungDatHang() {
        return execute("Cau 14: Khach hang chua tung dat hang", """
            SELECT kh.maKH, kh.hoTen, kh.soDienThoai, kh.email, kh.diaChi, kh.ngayDangKy
            FROM khach_hang kh
            LEFT JOIN don_hang dh ON kh.maKH = dh.maKH
            WHERE dh.maDH IS NULL
            ORDER BY kh.maKH;
        """);
    }

    public QueryResult query15_MonChuaBanThanhCong() {
        return execute("Cau 15: Mon an chua tung ban thanh cong", """
            SELECT m.maMon, m.tenMon, m.giaBan, m.trangThai, m.maDM
            FROM mon_an m
            WHERE m.maMon NOT IN (
                SELECT DISTINCT ct.maMon
                FROM chi_tiet_don_hang ct
                JOIN don_hang dh ON ct.maDH = dh.maDH
                WHERE dh.trangThai = 'DA_HOAN_THANH'
            )
            ORDER BY m.maMon;
        """);
    }

    public QueryResult query16_ThongKeNhanVienDoanhThu() {
        return execute("Cau 16: Thong ke so don va doanh thu theo nhan vien", """
            SELECT nv.maNV, nv.hoTen, nv.chucVu,
                   COUNT(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.maDH END) AS soDon,
                   COALESCE(SUM(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.tongTien ELSE 0 END), 0) AS tongDoanhThu
            FROM nhan_vien nv
            LEFT JOIN don_hang dh ON nv.maNV = dh.maNV
            GROUP BY nv.maNV, nv.hoTen, nv.chucVu
            ORDER BY tongDoanhThu DESC, nv.maNV;
        """);
    }

    public QueryResult query17_KhachHangCoItNhat2DonHoanThanh() {
        return execute("Cau 17: Khach hang co it nhat 2 don hoan thanh", """
            SELECT kh.maKH, kh.hoTen,
                   COUNT(dh.maDH) AS soDon,
                   SUM(dh.tongTien) AS tongChiTieu
            FROM khach_hang kh
            JOIN don_hang dh ON kh.maKH = dh.maKH
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY kh.maKH, kh.hoTen
            HAVING COUNT(dh.maDH) >= 2
            ORDER BY soDon DESC, tongChiTieu DESC;
        """);
    }

    public QueryResult query18_LichSuGiaMon() {
        return execute("Cau 18: Chi tiet co don gia khac gia hien tai", """
            SELECT ct.maCTDH, ct.maDH, m.maMon, m.tenMon, ct.donGia, m.giaBan, (m.giaBan - ct.donGia) AS chenhLech
            FROM chi_tiet_don_hang ct
            JOIN mon_an m ON ct.maMon = m.maMon
            WHERE ct.donGia <> m.giaBan
            ORDER BY ct.maDH, ct.maCTDH;
        """);
    }

    public QueryResult query19_TaiKhoanBiKhoa() {
        return execute("Cau 19: Tai khoan dang bi khoa (LOCKED)", """
            SELECT tk.maTK, tk.tenDangNhap, nv.hoTen, nv.chucVu, cn.tenCN, tk.trangThai
            FROM tai_khoan tk
            JOIN nhan_vien nv ON tk.maNV = nv.maNV
            JOIN chi_nhanh cn ON nv.maCN = cn.maCN
            WHERE tk.trangThai = 'LOCKED'
            ORDER BY tk.maTK;
        """);
    }

    public QueryResult query20_DonHangCaoHonTrungBinh() {
        return execute("Cau 20: Don hang co tong tien > muc trung binh", """
            SELECT maDH, ngayDat, tongTien, trangThai, maCN
            FROM don_hang
            WHERE tongTien > (SELECT AVG(tongTien) FROM don_hang)
            ORDER BY tongTien DESC;
        """);
    }

    public QueryResult query21_TaiKhoanChuaTungDangNhap() {
        return execute("Cau 21: Tai khoan chua tung dang nhap", """
            SELECT tk.maTK, tk.tenDangNhap, nv.hoTen, nv.chucVu, cn.tenCN, tk.ngayTao
            FROM tai_khoan tk
            JOIN nhan_vien nv ON tk.maNV = nv.maNV
            JOIN chi_nhanh cn ON nv.maCN = cn.maCN
            WHERE tk.lanDangNhapCuoi IS NULL
            ORDER BY tk.maTK;
        """);
    }

    public QueryResult query22_DonHangDungKhuyenMai() {
        return execute("Cau 22: Don hang co ap dung khuyen mai", """
            SELECT dh.maDH, km.tenKM, dh.tienHang, dh.tienGiam, dh.tongTien, dh.trangThai
            FROM don_hang dh
            JOIN khuyen_mai km ON dh.maKM = km.maKM
            ORDER BY dh.maDH;
        """);
    }

    public QueryResult query23_TongTienGiamTungKhuyenMai() {
        return execute("Cau 23: Tong tien giam cua tung khuyen mai", """
            SELECT km.maKM, km.tenKM,
                   COUNT(dh.maDH) AS soDon,
                   COALESCE(SUM(dh.tienGiam), 0) AS tongGiam
            FROM khuyen_mai km
            LEFT JOIN don_hang dh ON km.maKM = dh.maKM
            GROUP BY km.maKM, km.tenKM
            ORDER BY tongGiam DESC, km.maKM;
        """);
    }

    public QueryResult query24_KhuyenMaiChuaTungDung() {
        return execute("Cau 24: Khuyen mai chua tung duoc ap dung", """
            SELECT km.maKM, km.tenKM, CONCAT(km.phanTramGiam, '%'), km.donToiThieu, km.trangThai
            FROM khuyen_mai km
            LEFT JOIN don_hang dh ON km.maKM = dh.maKM
            WHERE dh.maDH IS NULL
            ORDER BY km.maKM;
        """);
    }

    public QueryResult query25_SoSanhCoVaKhongKhuyenMai() {
        return execute("Cau 25: So sanh don co va khong co khuyen mai", """
            SELECT CASE WHEN maKM IS NOT NULL THEN 'Co dung Khuyen mai' ELSE 'Khong dung Khuyen mai' END AS loaiDon,
                   COUNT(*) AS soDon,
                   SUM(tienHang) AS tongTienHang,
                   SUM(tienGiam) AS tongTienGiam,
                   SUM(tongTien) AS doanhThu
            FROM don_hang
            WHERE trangThai = 'DA_HOAN_THANH'
            GROUP BY CASE WHEN maKM IS NOT NULL THEN 'Co dung Khuyen mai' ELSE 'Khong dung Khuyen mai' END
            ORDER BY doanhThu DESC;
        """);
    }

    public QueryResult query26_DonBiChanGiamToiDa() {
        return execute("Cau 26: Don co tien giam bi chan boi muc tran toi da", """
            SELECT dh.maDH, km.maKM, dh.tienHang,
                   CONCAT(km.phanTramGiam, '%'),
                   (dh.tienHang * km.phanTramGiam / 100) AS giamTheoPT,
                   km.giamToiDa,
                   dh.tienGiam
            FROM don_hang dh
            JOIN khuyen_mai km ON dh.maKM = km.maKM
            WHERE km.giamToiDa IS NOT NULL
              AND dh.tienGiam = km.giamToiDa
              AND (dh.tienHang * km.phanTramGiam / 100) > km.giamToiDa
            ORDER BY dh.maDH;
        """);
    }

    public QueryResult query27_TienThuTheoPhuongThuc() {
        return execute("Cau 27: Tong tien thu theo phuong thuc thanh toan", """
            SELECT phuongThuc,
                   COUNT(*) AS soGiaoDich,
                   SUM(soTien) AS tongTienThu
            FROM thanh_toan
            WHERE trangThai = 'DA_THANH_TOAN'
            GROUP BY phuongThuc
            ORDER BY tongTienThu DESC;
        """);
    }

    public QueryResult query28_DoanhThuTheoChiNhanh() {
        return execute("Cau 28: Doanh thu theo tung chi nhanh", """
            SELECT cn.maCN, cn.tenCN,
                   COUNT(dh.maDH) AS soDon,
                   SUM(dh.tongTien) AS doanhThu
            FROM chi_nhanh cn
            JOIN don_hang dh ON cn.maCN = dh.maCN
            WHERE dh.trangThai = 'DA_HOAN_THANH'
            GROUP BY cn.maCN, cn.tenCN
            ORDER BY doanhThu DESC;
        """);
    }

    public QueryResult query29_KhachHangChiTieuTrenTrungBinh() {
        return execute("Cau 29: Khach hang chi tieu cao hon trung binh", """
            SELECT kh.maKH, kh.hoTen, SUM(dh.tongTien) AS tongChiTieu
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
            ORDER BY tongChiTieu DESC;
        """);
    }

    public QueryResult query30_DonThanhToanThatBaiRoiThanhCong() {
        return execute("Cau 30: Don thanh toan that bai roi sau do thanh cong", """
            SELECT DISTINCT dh.maDH, dh.ngayDat, dh.tongTien, dh.trangThai, 'Co GD THAT_BAI va DA_THANH_TOAN' AS ghiChu
            FROM don_hang dh
            JOIN thanh_toan t1 ON dh.maDH = t1.maDH AND t1.trangThai = 'THAT_BAI'
            JOIN thanh_toan t2 ON dh.maDH = t2.maDH AND t2.trangThai = 'DA_THANH_TOAN'
            ORDER BY dh.maDH;
        """);
    }
}
