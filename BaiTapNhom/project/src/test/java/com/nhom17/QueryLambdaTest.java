package com.nhom17;

import com.nhom17.database.DatabaseInitializer;
import com.nhom17.service.QueryLambdaService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryLambdaTest {

    private static QueryLambdaService service;

    @BeforeAll
    public static void setUp() {
        DatabaseInitializer.initialize();
        service = new QueryLambdaService();
    }

    @Test
    public void testLambdaQueries() {
        assertEquals(1, service.q01_NhanVienChuaCoTaiKhoan().size());
        assertEquals(4, service.q02_MonBurgerGia40kDen60k().size());
        assertEquals(19, service.q03_TonKhoNhoHon10().size());
        assertEquals(6, service.q04_KhachHangTPHCM().size());
        assertEquals(4, service.q05_PhuongThucThanhCong().size());
        assertEquals(7, service.q06_DonHangThang2().size());
        assertEquals(2, service.q14_KhachHangChuaTungDatHang().size());
        assertEquals(2, service.q15_MonChuaBanThanhCong().size());
        assertEquals(1, service.q19_TaiKhoanBiKhoa().size());
        assertEquals(13, service.q20_DonHangCaoHonTrungBinh().size());
        assertEquals(1, service.q21_TaiKhoanChuaTungDangNhap().size());
        assertEquals(11, service.q22_DonHangDungKhuyenMai().size());
        assertEquals(1, service.q24_KhuyenMaiChuaTungDung().size());
        assertEquals(2, service.q26_DonBiChanGiamToiDa().size());
        assertEquals(1, service.q30_DonThatBaiRoiThanhCong().size());
    }
}
