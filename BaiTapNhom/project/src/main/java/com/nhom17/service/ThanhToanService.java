package com.nhom17.service;

import com.nhom17.dao.ThanhToanDAO;
import com.nhom17.enums.PhuongThucThanhToan;
import com.nhom17.enums.TrangThaiThanhToan;
import com.nhom17.model.ThanhToan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ThanhToanService {

    private final ThanhToanDAO thanhToanDAO;

    public ThanhToanService() {
        this.thanhToanDAO = new ThanhToanDAO();
    }

    public ThanhToanService(ThanhToanDAO thanhToanDAO) {
        this.thanhToanDAO = thanhToanDAO;
    }

    public List<ThanhToan> getAll() {
        return thanhToanDAO.findAll();
    }

    public List<ThanhToan> getByMaDH(Long maDH) {
        return thanhToanDAO.findByMaDH(maDH);
    }

    public Optional<ThanhToan> getById(String maTT) {
        return thanhToanDAO.findById(maTT);
    }

    public boolean addThanhToan(ThanhToan tt) {
        return thanhToanDAO.insert(tt);
    }

    public boolean updateTrangThai(String maTT, TrangThaiThanhToan trangThai) {
        return thanhToanDAO.updateTrangThai(maTT, trangThai);
    }

    public boolean deleteThanhToan(String maTT) {
        return thanhToanDAO.delete(maTT);
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
