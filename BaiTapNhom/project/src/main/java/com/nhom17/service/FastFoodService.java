package com.nhom17.service;

import com.nhom17.dao.*;
import com.nhom17.enums.*;
import com.nhom17.model.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FastFoodService {

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
    private final QueryDAO queryDAO = new QueryDAO();

    public List<ChiNhanh> getAllChiNhanh() { return chiNhanhDAO.findAll(); }
    public List<NhanVien> getAllNhanVien() { return nhanVienDAO.findAll(); }
    public List<TaiKhoan> getAllTaiKhoan() { return taiKhoanDAO.findAll(); }
    public List<KhachHang> getAllKhachHang() { return khachHangDAO.findAll(); }
    public List<DanhMuc> getAllDanhMuc() { return danhMucDAO.findAll(); }
    public List<MonAn> getAllMonAn() { return monAnDAO.findAll(); }
    public List<TonKho> getAllTonKho() { return tonKhoDAO.findAll(); }
    public List<KhuyenMai> getAllKhuyenMai() { return khuyenMaiDAO.findAll(); }
    public List<DonHang> getAllDonHang() { return donHangDAO.findAll(); }
    public List<ChiTietDonHang> getAllChiTietDonHang() { return chiTietDonHangDAO.findAll(); }
    public List<ThanhToan> getAllThanhToan() { return thanhToanDAO.findAll(); }
    public QueryDAO getQueryDAO() { return queryDAO; }

    public boolean addKhachHang(KhachHang kh) {
        if (kh.getSoDienThoai() != null && phoneExists(kh.getSoDienThoai())) {
            return false;
        }
        return khachHangDAO.insert(kh);
    }

    public boolean updateKhachHang(KhachHang kh) {
        return khachHangDAO.update(kh);
    }

    public boolean deleteKhachHang(String maKH) {
        return khachHangDAO.delete(maKH);
    }

    public boolean addMonAn(MonAn mon) {
        if (mon.getGiaBan() == null || mon.getGiaBan().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return monAnDAO.insert(mon);
    }

    public boolean updateMonAn(MonAn mon) {
        return monAnDAO.update(mon);
    }

    public boolean deleteMonAn(String maMon) {
        return monAnDAO.delete(maMon);
    }

    public boolean addNhanVien(NhanVien nv) {
        return nhanVienDAO.insert(nv);
    }

    public boolean addTaiKhoan(TaiKhoan tk) {
        if (usernameExists(tk.getTenDangNhap())) {
            return false;
        }
        return taiKhoanDAO.insert(tk);
    }

    public boolean lockTaiKhoan(String maTK) {
        return taiKhoanDAO.lockAccount(maTK);
    }

    public boolean updateTonKho(String maTon, int soLuong) {
        return tonKhoDAO.updateSoLuong(maTon, soLuong);
    }

    public boolean addDonHang(DonHang dh, List<ChiTietDonHang> chiTiets, List<ThanhToan> thanhToans) {
        boolean ok = donHangDAO.insert(dh);
        if (ok) {
            if (chiTiets != null) chiTiets.forEach(chiTietDonHangDAO::insert);
            if (thanhToans != null) thanhToans.forEach(thanhToanDAO::insert);
        }
        return ok;
    }

    public boolean updateTrangThaiDonHang(Long maDH, TrangThaiDonHang tt) {
        return donHangDAO.updateTrangThai(maDH, tt);
    }

    public List<MonAn> filterMonAn(Predicate<MonAn> predicate) {
        return monAnDAO.findAll().stream().filter(predicate).toList();
    }

    public List<KhachHang> filterKhachHang(Predicate<KhachHang> predicate) {
        return khachHangDAO.findAll().stream().filter(predicate).toList();
    }

    public List<NhanVien> searchNhanVienByName(String keyword) {
        return nhanVienDAO.findAll().stream()
                .filter(nv -> nv.getHoTen().toLowerCase().contains(keyword.toLowerCase().trim()))
                .toList();
    }

    public boolean usernameExists(String username) {
        return taiKhoanDAO.findAll().stream()
                .anyMatch(tk -> tk.getTenDangNhap().equalsIgnoreCase(username));
    }

    public boolean phoneExists(String phone) {
        return khachHangDAO.findAll().stream()
                .anyMatch(kh -> kh.getSoDienThoai().equalsIgnoreCase(phone));
    }

    public boolean updateMonAnField(String maMon, String field, String newValue) {
        return monAnDAO.findById(maMon).map(mon -> {
            Map<String, BiConsumer<MonAn, String>> updaters = Map.of(
                    "ten", MonAn::setTenMon,
                    "gia", (m, v) -> m.setGiaBan(new BigDecimal(v)),
                    "mota", MonAn::setMoTa,
                    "trangthai", (m, v) -> m.setTrangThai(TrangThaiMon.valueOf(v.toUpperCase())),
                    "danhmuc", MonAn::setMaDM
            );
            BiConsumer<MonAn, String> updater = updaters.get(field.toLowerCase().trim());
            if (updater != null) {
                try {
                    updater.accept(mon, newValue);
                    return monAnDAO.update(mon);
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }).orElse(false);
    }

    public List<MonAn> sortMonAnByPriceAsc() {
        return monAnDAO.findAll().stream()
                .sorted(Comparator.comparing(MonAn::getGiaBan))
                .toList();
    }

    public List<MonAn> sortMonAnByPriceDescThenName() {
        return monAnDAO.findAll().stream()
                .sorted(Comparator.comparing(MonAn::getGiaBan).reversed().thenComparing(MonAn::getTenMon))
                .toList();
    }

    public long countCompletedOrders() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .count();
    }

    public Optional<MonAn> findMostExpensiveFood() {
        return monAnDAO.findAll().stream()
                .max(Comparator.comparing(MonAn::getGiaBan));
    }

    public double calculateAverageOrderValue() {
        return donHangDAO.findAll().stream()
                .mapToDouble(dh -> dh.getTongTien().doubleValue())
                .average()
                .orElse(0.0);
    }

    public Map<String, List<MonAn>> groupMonAnByDanhMuc() {
        return monAnDAO.findAll().stream()
                .collect(Collectors.groupingBy(MonAn::getMaDM, TreeMap::new, Collectors.toList()));
    }

    public Map<String, List<NhanVien>> groupNhanVienByChiNhanh() {
        return nhanVienDAO.findAll().stream()
                .collect(Collectors.groupingBy(NhanVien::getMaCN, TreeMap::new, Collectors.toList()));
    }

    public BigDecimal calculateTotalCompletedRevenue() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .map(DonHang::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TrangThaiDonHang, Long> countOrdersByStatus() {
        return donHangDAO.findAll().stream()
                .collect(Collectors.groupingBy(DonHang::getTrangThai, Collectors.counting()));
    }

    public Map<PhuongThucThanhToan, BigDecimal> revenueByPaymentMethod() {
        return thanhToanDAO.findAll().stream()
                .filter(tt -> tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                .collect(Collectors.groupingBy(
                        ThanhToan::getPhuongThuc,
                        Collectors.reducing(BigDecimal.ZERO, ThanhToan::getSoTien, BigDecimal::add)
                ));
    }
}
