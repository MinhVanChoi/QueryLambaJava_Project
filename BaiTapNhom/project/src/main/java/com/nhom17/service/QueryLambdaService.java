package com.nhom17.service;

import com.nhom17.dao.*;
import com.nhom17.enums.*;
import com.nhom17.model.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class QueryLambdaService {

    private final ChiNhanhDAO chiNhanhDAO = new ChiNhanhDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();
    private final MonAnDAO monAnDAO = new MonAnDAO();
    private final TonKhoDAO tonKhoDAO = new TonKhoDAO();
    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final DonHangDAO donHangDAO = new DonHangDAO();
    private final ChiTietDonHangDAO chiTietDonHangDAO = new ChiTietDonHangDAO();
    private final ThanhToanDAO thanhToanDAO = new ThanhToanDAO();

    public List<NhanVien> q01_NhanVienChuaCoTaiKhoan() {
        Set<String> coTK = taiKhoanDAO.findAll().stream()
                .map(TaiKhoan::getMaNV)
                .collect(Collectors.toSet());
      
        return nhanVienDAO.findAll().stream()
                .filter(a -> !coTK.contains(a.getMaNV()))
                .toList();
    }

    public List<MonAn> q02_MonBurgerGia40kDen60k() {
        return monAnDAO.findAll().stream() 
                .filter(a -> "DM01".equals(a.getMaDM()))
                .filter(b -> b.getGiaBan().compareTo(BigDecimal.valueOf(40000)) >= 0
                          && b.getGiaBan().compareTo(BigDecimal.valueOf(60000)) <= 0)



                .sorted(Comparator.comparing(MonAn::getGiaBan).reversed())



                .toList();
    }

    public List<TonKho> q03_TonKhoNhoHon10() {
        return tonKhoDAO.findAll().stream()
                .filter(tk -> tk.getSoLuongTon() < 10)
                .sorted(Comparator.comparing(TonKho::getMaCN).thenComparing(TonKho::getMaMon))
                .toList();
    }

    public List<KhachHang> q04_KhachHangTPHCM() {
        return khachHangDAO.findAll().stream()
                .filter(kh -> kh.getDiaChi() != null &&
                        (kh.getDiaChi().contains("TP.HCM") || kh.getDiaChi().contains("TPHCM")))
                .toList();
    }

    public List<PhuongThucThanhToan> q05_PhuongThucThanhCong() {
        return thanhToanDAO.findAll().stream()
                .filter(tt -> tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                .map(ThanhToan::getPhuongThuc)
                .distinct()
                .toList();
    }

    public List<DonHang> q06_DonHangThang2() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .filter(dh -> dh.getNgayDat().getYear() == 2026 && dh.getNgayDat().getMonthValue() == 2)
                .toList();
    }

    public List<String> q07_DonHangKemThongTin() {
        return donHangDAO.findAll().stream()
        .map(dh -> {
            String tenNV = nhanVienDAO.findById(dh.getMaNV()).map(NhanVien::getHoTen).orElse("");
            String tenKH = khachHangDAO.findById(dh.getMaKH()).map(KhachHang::getHoTen).orElse("(Khach vang lai)");
            return dh.getMaDH() + " | " + tenKH + " | " + tenNV + " | " + dh.getTongTien();
        }).toList();
    }

    public List<ChiTietDonHang> q08_ChiTietDon1001() {
        return chiTietDonHangDAO.findAll().stream()
                .filter(ct -> ct.getMaDH().equals(1001L))
                .toList();
    }

    public Map<Long, Boolean> q09_DoiChieuTienHang() {
        Map<Long, BigDecimal> sumMap = chiTietDonHangDAO.findAll().stream()
                .collect(Collectors.groupingBy(ChiTietDonHang::getMaDH,
                        Collectors.reducing(BigDecimal.ZERO, ChiTietDonHang::getThanhTien, BigDecimal::add)));
        return donHangDAO.findAll().stream()
                .collect(Collectors.toMap(
                        DonHang::getMaDH,
                        dh -> dh.getTienHang().compareTo(sumMap.getOrDefault(dh.getMaDH(), BigDecimal.ZERO)) == 0
                ));
    }

    public BigDecimal q10_TongDoanhThu() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .map(DonHang::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Integer, BigDecimal> q11_DoanhThuTheoThang() {
        return donHangDAO.findAll().stream()

        
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH && dh.getNgayDat().getYear() == 2026)

                .collect(Collectors.groupingBy(
                        dh -> dh.getNgayDat().getMonthValue(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, DonHang::getTongTien, BigDecimal::add)
                ));
    }

    public List<Map.Entry<String, Integer>> q12_Top3MonBanChay() {
        Set<Long> doneOrders = donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .map(DonHang::getMaDH)
                .collect(Collectors.toSet());

        return chiTietDonHangDAO.findAll().stream()
                .filter(ct -> doneOrders.contains(ct.getMaDH()))
                .collect(Collectors.groupingBy(ChiTietDonHang::getMaMon, Collectors.summingInt(ChiTietDonHang::getSoLuong)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .toList();
    }

    public Map<String, BigDecimal> q13_DoanhThuTungKhachHang() {
        return khachHangDAO.findAll().stream().collect(Collectors.toMap(
                KhachHang::getHoTen,
                kh -> donHangDAO.findAll().stream()
                        .filter(dh -> kh.getMaKH().equals(dh.getMaKH()) && dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .map(DonHang::getTongTien)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        ));
    }

    public List<KhachHang> q14_KhachHangChuaTungDatHang() {
        Set<String> daMua = donHangDAO.findAll().stream()
                .map(DonHang::getMaKH)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return khachHangDAO.findAll().stream()
                .filter(kh -> !daMua.contains(kh.getMaKH()))
                .toList();
    }

    public List<MonAn> q15_MonChuaBanThanhCong() {
        Set<Long> doneOrders = donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .map(DonHang::getMaDH)
                .collect(Collectors.toSet());
        Set<String> daBan = chiTietDonHangDAO.findAll().stream()
                .filter(ct -> doneOrders.contains(ct.getMaDH()))
                .map(ChiTietDonHang::getMaMon)
                .collect(Collectors.toSet());
        return monAnDAO.findAll().stream()
                .filter(m -> !daBan.contains(m.getMaMon()))
                .toList();
    }

    public Map<String, BigDecimal> q16_DoanhThuTheoNhanVien() {
        return nhanVienDAO.findAll().stream().collect(Collectors.toMap(
                NhanVien::getHoTen,
                nv -> donHangDAO.findAll().stream()
                        .filter(dh -> nv.getMaNV().equals(dh.getMaNV()) && dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .map(DonHang::getTongTien)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        ));
    }

    public List<String> q17_KhachHangCoItNhat2Don() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH && dh.getMaKH() != null)
                .collect(Collectors.groupingBy(DonHang::getMaKH, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<ChiTietDonHang> q18_ChiTietGiaKhacHienTai() {
        return chiTietDonHangDAO.findAll().stream()
                .filter(ct -> monAnDAO.findById(ct.getMaMon())
                        .map(m -> ct.getDonGia().compareTo(m.getGiaBan()) != 0)
                        .orElse(false))
                .toList();
    }

    public List<TaiKhoan> q19_TaiKhoanBiKhoa() {
        return taiKhoanDAO.findAll().stream()
                .filter(tk -> tk.getTrangThai() == TrangThaiTaiKhoan.LOCKED)
                .toList();
    }

    public List<DonHang> q20_DonHangCaoHonTrungBinh() {
        double avg = donHangDAO.findAll().stream()
                .mapToDouble(dh -> dh.getTongTien().doubleValue())
                .average().orElse(0.0);
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTongTien().doubleValue() > avg)
                .sorted(Comparator.comparing(DonHang::getTongTien).reversed())
                .toList();
    }

    public List<TaiKhoan> q21_TaiKhoanChuaTungDangNhap() {
        return taiKhoanDAO.findAll().stream()
                .filter(tk -> tk.getLanDangNhapCuoi() == null)
                .toList();
    }

    public List<DonHang> q22_DonHangDungKhuyenMai() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getMaKM() != null && !dh.getMaKM().isBlank())
                .toList();
    }

    public Map<String, BigDecimal> q23_TongTienGiamTungKhuyenMai() {
        return khuyenMaiDAO.findAll().stream().collect(Collectors.toMap(
                KhuyenMai::getTenKM,
                km -> donHangDAO.findAll().stream()
                        .filter(dh -> km.getMaKM().equals(dh.getMaKM()))
                        .map(DonHang::getTienGiam)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        ));
    }

    public List<KhuyenMai> q24_KhuyenMaiChuaTungDung() {
        Set<String> daDung = donHangDAO.findAll().stream()
                .map(DonHang::getMaKM)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return khuyenMaiDAO.findAll().stream()
                .filter(km -> !daDung.contains(km.getMaKM()))
                .toList();
    }

    public Map<Boolean, BigDecimal> q25_SoSanhKhuyenMai() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .collect(Collectors.partitioningBy(
                        dh -> dh.getMaKM() != null && !dh.getMaKM().isBlank(),
                        Collectors.reducing(BigDecimal.ZERO, DonHang::getTongTien, BigDecimal::add)
                ));
    }

    public List<DonHang> q26_DonBiChanGiamToiDa() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getMaKM() != null)
                .filter(dh -> {
                    KhuyenMai km = khuyenMaiDAO.findById(dh.getMaKM()).orElse(null);
                    return km != null && km.getGiamToiDa() != null
                            && dh.getTienGiam().compareTo(km.getGiamToiDa()) == 0;
                })
                .toList();
    }

    public Map<PhuongThucThanhToan, BigDecimal> q27_TienThuTheoPhuongThuc() {
        return thanhToanDAO.findAll().stream()
                .filter(tt -> tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                .collect(Collectors.groupingBy(
                        ThanhToan::getPhuongThuc,
                        Collectors.reducing(BigDecimal.ZERO, ThanhToan::getSoTien, BigDecimal::add)
                ));
    }

    public Map<String, BigDecimal> q28_DoanhThuTheoChiNhanh() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .collect(Collectors.groupingBy(
                        DonHang::getMaCN,
                        Collectors.reducing(BigDecimal.ZERO, DonHang::getTongTien, BigDecimal::add)
                ));
    }

    public List<String> q29_KhachHangChiTieuTrenTrungBinh() {
        Map<String, BigDecimal> chiTieu = donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH && dh.getMaKH() != null)
                .collect(Collectors.groupingBy(DonHang::getMaKH,
                        Collectors.reducing(BigDecimal.ZERO, DonHang::getTongTien, BigDecimal::add)));
        double avg = chiTieu.values().stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0.0);
        return chiTieu.entrySet().stream()
                .filter(e -> e.getValue().doubleValue() > avg)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<Long> q30_DonThatBaiRoiThanhCong() {
        return thanhToanDAO.findAll().stream()
                .collect(Collectors.groupingBy(ThanhToan::getMaDH))
                .entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(t -> t.getTrangThai() == TrangThaiThanhToan.THAT_BAI)
                          && e.getValue().stream().anyMatch(t -> t.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN))
                .map(Map.Entry::getKey)
                .toList();
    }

    public void run(int num) {
        System.out.println("\n--- Cau " + num + " (Lambda) ---");
        switch (num) {
            case 1 -> q01_NhanVienChuaCoTaiKhoan().forEach(System.out::println);
            case 2 -> q02_MonBurgerGia40kDen60k().forEach(System.out::println);
            case 3 -> q03_TonKhoNhoHon10().forEach(System.out::println);
            case 4 -> q04_KhachHangTPHCM().forEach(System.out::println);
            case 5 -> q05_PhuongThucThanhCong().forEach(System.out::println);
            case 6 -> q06_DonHangThang2().forEach(System.out::println);
            case 7 -> q07_DonHangKemThongTin().forEach(System.out::println);
            case 8 -> q08_ChiTietDon1001().forEach(System.out::println);
            case 9 -> q09_DoiChieuTienHang().forEach((k, v) -> System.out.println("Don " + k + ": " + (v ? "KHOP" : "LECH")));
            case 10 -> System.out.println("Tong doanh thu: " + q10_TongDoanhThu());
            case 11 -> q11_DoanhThuTheoThang().forEach((k, v) -> System.out.println("Thang " + k + ": " + v));
            case 12 -> q12_Top3MonBanChay().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
            case 13 -> q13_DoanhThuTungKhachHang().forEach((k, v) -> System.out.println(k + ": " + v));
            case 14 -> q14_KhachHangChuaTungDatHang().forEach(System.out::println);
            case 15 -> q15_MonChuaBanThanhCong().forEach(System.out::println);
            case 16 -> q16_DoanhThuTheoNhanVien().forEach((k, v) -> System.out.println(k + ": " + v));
            case 17 -> q17_KhachHangCoItNhat2Don().forEach(System.out::println);
            case 18 -> q18_ChiTietGiaKhacHienTai().forEach(System.out::println);
            case 19 -> q19_TaiKhoanBiKhoa().forEach(System.out::println);
            case 20 -> q20_DonHangCaoHonTrungBinh().forEach(System.out::println);
            case 21 -> q21_TaiKhoanChuaTungDangNhap().forEach(System.out::println);
            case 22 -> q22_DonHangDungKhuyenMai().forEach(System.out::println);
            case 23 -> q23_TongTienGiamTungKhuyenMai().forEach((k, v) -> System.out.println(k + ": " + v));
            case 24 -> q24_KhuyenMaiChuaTungDung().forEach(System.out::println);
            case 25 -> q25_SoSanhKhuyenMai().forEach((k, v) -> System.out.println((k ? "Co KM: " : "Khong KM: ") + v));
            case 26 -> q26_DonBiChanGiamToiDa().forEach(System.out::println);
            case 27 -> q27_TienThuTheoPhuongThuc().forEach((k, v) -> System.out.println(k + ": " + v));
            case 28 -> q28_DoanhThuTheoChiNhanh().forEach((k, v) -> System.out.println(k + ": " + v));
            case 29 -> q29_KhachHangChiTieuTrenTrungBinh().forEach(System.out::println);
            case 30 -> q30_DonThatBaiRoiThanhCong().forEach(System.out::println);
            default -> System.out.println("Khong ton tai cau " + num);
        }
    }
}
