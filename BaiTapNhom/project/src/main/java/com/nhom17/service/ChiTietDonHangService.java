package com.nhom17.service;

import com.nhom17.dao.ChiTietDonHangDAO;
import com.nhom17.model.ChiTietDonHang;

import java.util.List;
import java.util.Optional;

public class ChiTietDonHangService {

    private final ChiTietDonHangDAO chiTietDonHangDAO;

    public ChiTietDonHangService() {
        this.chiTietDonHangDAO = new ChiTietDonHangDAO();
    }

    public ChiTietDonHangService(ChiTietDonHangDAO chiTietDonHangDAO) {
        this.chiTietDonHangDAO = chiTietDonHangDAO;
    }

    public List<ChiTietDonHang> getAll() {
        return chiTietDonHangDAO.findAll();
    }

    public List<ChiTietDonHang> getByMaDH(Long maDH) {
        return chiTietDonHangDAO.findByMaDH(maDH);
    }

    public Optional<ChiTietDonHang> getById(Long maCTDH) {
        return chiTietDonHangDAO.findById(maCTDH);
    }

    public boolean addChiTiet(ChiTietDonHang ct) {
        return chiTietDonHangDAO.insert(ct);
    }

    public boolean deleteChiTiet(Long maCTDH) {
        return chiTietDonHangDAO.delete(maCTDH);
    }
}
