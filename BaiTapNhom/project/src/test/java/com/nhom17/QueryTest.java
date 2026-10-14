package com.nhom17;

import com.nhom17.dao.QueryDAO;
import com.nhom17.database.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTest {

    private static QueryDAO queryDAO;

    @BeforeAll
    public static void setUp() {
        DatabaseInitializer.initialize();
        queryDAO = new QueryDAO();
    }

    @Test
    public void testAll30Queries() {
        assertNotNull(queryDAO.query01_NhanVienChuaCoTaiKhoan());
        assertEquals(1, queryDAO.query01_NhanVienChuaCoTaiKhoan().rows.size(), "Q1 NV06");

        assertNotNull(queryDAO.query02_MonBurgerGia40kDen60k());
        assertEquals(4, queryDAO.query02_MonBurgerGia40kDen60k().rows.size(), "Q2 4 burgers");

        assertNotNull(queryDAO.query03_TonKhoNhoHon10());
        assertEquals(19, queryDAO.query03_TonKhoNhoHon10().rows.size(), "Q3 19 ton kho < 10");

        assertNotNull(queryDAO.query04_KhachHangTPHCM());
        assertEquals(6, queryDAO.query04_KhachHangTPHCM().rows.size(), "Q4 6 khach HCM");

        assertNotNull(queryDAO.query05_PhuongThucThanhToanThanhCong());
        assertEquals(4, queryDAO.query05_PhuongThucThanhToanThanhCong().rows.size(), "Q5 4 phuong thuc");

        assertNotNull(queryDAO.query06_DonHangHoanThanhThang2());
        assertEquals(7, queryDAO.query06_DonHangHoanThanhThang2().rows.size(), "Q6 7 don thang 2");

        assertNotNull(queryDAO.query07_DonHangVaKhachHangNhanVien());
        assertEquals(28, queryDAO.query07_DonHangVaKhachHangNhanVien().rows.size(), "Q7 28 don");

        assertNotNull(queryDAO.query08_ChiTietDonHang1001());
        assertEquals(3, queryDAO.query08_ChiTietDonHang1001().rows.size(), "Q8 3 dong don 1001");

        assertNotNull(queryDAO.query09_DoiChieuTienHangVaTongTien());
        assertEquals(28, queryDAO.query09_DoiChieuTienHangVaTongTien().rows.size(), "Q9 28 don");

        assertNotNull(queryDAO.query10_TongDoanhThuDonHoanThanh());
        assertFalse(queryDAO.query10_TongDoanhThuDonHoanThanh().rows.isEmpty(), "Q10");

        assertNotNull(queryDAO.query11_DoanhThuTheoThang());
        assertEquals(6, queryDAO.query11_DoanhThuTheoThang().rows.size(), "Q11 6 thang");

        assertNotNull(queryDAO.query12_Top3MonBanChayNhat());
        assertEquals(3, queryDAO.query12_Top3MonBanChayNhat().rows.size(), "Q12 top 3");

        assertNotNull(queryDAO.query13_DoanhThuTungKhachHang());
        assertEquals(10, queryDAO.query13_DoanhThuTungKhachHang().rows.size(), "Q13 10 khach");

        assertNotNull(queryDAO.query14_KhachHangChuaTungDatHang());
        assertEquals(2, queryDAO.query14_KhachHangChuaTungDatHang().rows.size(), "Q14 KH009, KH010");

        assertNotNull(queryDAO.query15_MonChuaBanThanhCong());
        assertEquals(2, queryDAO.query15_MonChuaBanThanhCong().rows.size(), "Q15 M017, M018");

        assertNotNull(queryDAO.query16_ThongKeNhanVienDoanhThu());
        assertEquals(8, queryDAO.query16_ThongKeNhanVienDoanhThu().rows.size(), "Q16 8 nhan vien");

        assertNotNull(queryDAO.query17_KhachHangCoItNhat2DonHoanThanh());
        assertEquals(6, queryDAO.query17_KhachHangCoItNhat2DonHoanThanh().rows.size(), "Q17 6 khach");

        assertNotNull(queryDAO.query18_LichSuGiaMon());
        assertEquals(4, queryDAO.query18_LichSuGiaMon().rows.size(), "Q18 4 dong doi gia");

        assertNotNull(queryDAO.query19_TaiKhoanBiKhoa());
        assertEquals(1, queryDAO.query19_TaiKhoanBiKhoa().rows.size(), "Q19 TK05");

        assertNotNull(queryDAO.query20_DonHangCaoHonTrungBinh());
        assertEquals(13, queryDAO.query20_DonHangCaoHonTrungBinh().rows.size(), "Q20 13 don");

        assertNotNull(queryDAO.query21_TaiKhoanChuaTungDangNhap());
        assertEquals(1, queryDAO.query21_TaiKhoanChuaTungDangNhap().rows.size(), "Q21 TK07");

        assertNotNull(queryDAO.query22_DonHangDungKhuyenMai());
        assertEquals(11, queryDAO.query22_DonHangDungKhuyenMai().rows.size(), "Q22 11 don");

        assertNotNull(queryDAO.query23_TongTienGiamTungKhuyenMai());
        assertEquals(4, queryDAO.query23_TongTienGiamTungKhuyenMai().rows.size(), "Q23 4 KM");

        assertNotNull(queryDAO.query24_KhuyenMaiChuaTungDung());
        assertEquals(1, queryDAO.query24_KhuyenMaiChuaTungDung().rows.size(), "Q24 KM04");

        assertNotNull(queryDAO.query25_SoSanhCoVaKhongKhuyenMai());
        assertEquals(2, queryDAO.query25_SoSanhCoVaKhongKhuyenMai().rows.size(), "Q25 2 nhom");

        assertNotNull(queryDAO.query26_DonBiChanGiamToiDa());
        assertEquals(2, queryDAO.query26_DonBiChanGiamToiDa().rows.size(), "Q26 2 don");

        assertNotNull(queryDAO.query27_TienThuTheoPhuongThuc());
        assertEquals(4, queryDAO.query27_TienThuTheoPhuongThuc().rows.size(), "Q27 4 PT");

        assertNotNull(queryDAO.query28_DoanhThuTheoChiNhanh());
        assertEquals(3, queryDAO.query28_DoanhThuTheoChiNhanh().rows.size(), "Q28 3 CN ban hang");

        assertNotNull(queryDAO.query29_KhachHangChiTieuTrenTrungBinh());
        assertEquals(4, queryDAO.query29_KhachHangChiTieuTrenTrungBinh().rows.size(), "Q29 4 khach");

        assertNotNull(queryDAO.query30_DonThanhToanThatBaiRoiThanhCong());
        assertEquals(1, queryDAO.query30_DonThanhToanThatBaiRoiThanhCong().rows.size(), "Q30 don 1015");
    }
}
