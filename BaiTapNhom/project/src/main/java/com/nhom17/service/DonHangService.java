package com.nhom17.service;

import com.nhom17.dao.ChiTietDonHangDAO;
import com.nhom17.dao.DonHangDAO;
import com.nhom17.dao.ThanhToanDAO;
import com.nhom17.enums.TrangThaiDonHang;
import com.nhom17.model.ChiTietDonHang;
import com.nhom17.model.DonHang;
import com.nhom17.model.ThanhToan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DonHangService {

    private final DonHangDAO donHangDAO;
    private final ChiTietDonHangDAO chiTietDonHangDAO;
    private final ThanhToanDAO thanhToanDAO;

    public DonHangService() {
        this.donHangDAO = new DonHangDAO();
        this.chiTietDonHangDAO = new ChiTietDonHangDAO();
        this.thanhToanDAO = new ThanhToanDAO();
    }

    public DonHangService(DonHangDAO donHangDAO, ChiTietDonHangDAO chiTietDonHangDAO, ThanhToanDAO thanhToanDAO) {
        this.donHangDAO = donHangDAO;
        this.chiTietDonHangDAO = chiTietDonHangDAO;
        this.thanhToanDAO = thanhToanDAO;
    }

    public List<DonHang> getAll() {
        return donHangDAO.findAll();
    }

    public Optional<DonHang> getById(Long maDH) {
        return donHangDAO.findById(maDH);
    }

    public boolean addDonHang(DonHang dh, List<ChiTietDonHang> chiTiets, List<ThanhToan> thanhToans) {
        boolean ok = donHangDAO.insert(dh);
        if (ok) {
            if (chiTiets != null) chiTiets.forEach(chiTietDonHangDAO::insert);
            if (thanhToans != null) thanhToans.forEach(thanhToanDAO::insert);
        }
        return ok;
    }

    public boolean updateTrangThai(Long maDH, TrangThaiDonHang trangThai) {
        return donHangDAO.updateTrangThai(maDH, trangThai);
    }

    public boolean deleteDonHang(Long maDH) {
        return donHangDAO.delete(maDH);
    }

    public long countCompletedOrders() {
        return donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .count();
    }

    public double calculateAverageOrderValue() {
        return donHangDAO.findAll().stream()
                .mapToDouble(dh -> dh.getTongTien().doubleValue())
                .average()
                .orElse(0.0);
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
}
