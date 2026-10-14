package com.nhom17.service;

import com.nhom17.dao.TaiKhoanDAO;
import com.nhom17.model.TaiKhoan;

import java.util.List;
import java.util.Optional;

public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanService() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public TaiKhoanService(TaiKhoanDAO taiKhoanDAO) {
        this.taiKhoanDAO = taiKhoanDAO;
    }

    public List<TaiKhoan> getAll() {
        return taiKhoanDAO.findAll();
    }

    public Optional<TaiKhoan> getById(String maTK) {
        return taiKhoanDAO.findById(maTK);
    }

    public Optional<TaiKhoan> getByUsername(String username) {
        return taiKhoanDAO.findByUsername(username);
    }

    public boolean addTaiKhoan(TaiKhoan tk) {
        if (usernameExists(tk.getTenDangNhap())) {
            return false;
        }
        return taiKhoanDAO.insert(tk);
    }

    public boolean updateTaiKhoan(TaiKhoan tk) {
        return taiKhoanDAO.update(tk);
    }

    public boolean lockTaiKhoan(String maTK) {
        return taiKhoanDAO.lockAccount(maTK);
    }

    public boolean deleteTaiKhoan(String maTK) {
        return taiKhoanDAO.delete(maTK);
    }

    public boolean usernameExists(String username) {
        return taiKhoanDAO.findAll().stream()
                .anyMatch(tk -> tk.getTenDangNhap().equalsIgnoreCase(username));
    }
}
