package com.nhom17.service;

import com.nhom17.dao.KhuyenMaiDAO;
import com.nhom17.model.KhuyenMai;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class KhuyenMaiService {

    private final KhuyenMaiDAO khuyenMaiDAO;

    public KhuyenMaiService() {
        this.khuyenMaiDAO = new KhuyenMaiDAO();
    }

    public KhuyenMaiService(KhuyenMaiDAO khuyenMaiDAO) {
        this.khuyenMaiDAO = khuyenMaiDAO;
    }

    public List<KhuyenMai> getAll() {
        return khuyenMaiDAO.findAll();
    }

    public Optional<KhuyenMai> getById(String maKM) {
        return khuyenMaiDAO.findById(maKM);
    }

    public boolean addKhuyenMai(KhuyenMai km) {
        return khuyenMaiDAO.insert(km);
    }

    public boolean updateKhuyenMai(KhuyenMai km) {
        return khuyenMaiDAO.update(km);
    }

    public boolean deleteKhuyenMai(String maKM) {
        return khuyenMaiDAO.delete(maKM);
    }

    public List<KhuyenMai> filterActive(LocalDate date) {
        return khuyenMaiDAO.findAll().stream()
                .filter(km -> km.conHieuLuc(date))
                .toList();
    }
}
