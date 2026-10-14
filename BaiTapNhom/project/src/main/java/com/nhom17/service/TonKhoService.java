package com.nhom17.service;

import com.nhom17.dao.TonKhoDAO;
import com.nhom17.model.TonKho;

import java.util.List;
import java.util.Optional;

public class TonKhoService {

    private final TonKhoDAO tonKhoDAO;

    public TonKhoService() {
        this.tonKhoDAO = new TonKhoDAO();
    }

    public TonKhoService(TonKhoDAO tonKhoDAO) {
        this.tonKhoDAO = tonKhoDAO;
    }

    public List<TonKho> getAll() {
        return tonKhoDAO.findAll();
    }

    public Optional<TonKho> getById(String maTon) {
        return tonKhoDAO.findById(maTon);
    }

    public Optional<TonKho> getByMonAndChiNhanh(String maMon, String maCN) {
        return tonKhoDAO.findByMonAndChiNhanh(maMon, maCN);
    }

    public boolean addTonKho(TonKho tk) {
        return tonKhoDAO.insert(tk);
    }

    public boolean updateSoLuong(String maTon, int soLuong) {
        return tonKhoDAO.updateSoLuong(maTon, soLuong);
    }

    public boolean deleteTonKho(String maTon) {
        return tonKhoDAO.delete(maTon);
    }

    public List<TonKho> filterLowStock(int threshold) {
        return tonKhoDAO.findAll().stream()
                .filter(tk -> tk.getSoLuongTon() < threshold)
                .toList();
    }
}
