package com.nhom17.service;

import com.nhom17.dao.DanhMucDAO;
import com.nhom17.model.DanhMuc;

import java.util.List;
import java.util.Optional;

public class DanhMucService {

    private final DanhMucDAO danhMucDAO;

    public DanhMucService() {
        this.danhMucDAO = new DanhMucDAO();
    }

    public DanhMucService(DanhMucDAO danhMucDAO) {
        this.danhMucDAO = danhMucDAO;
    }

    public List<DanhMuc> getAll() {
        return danhMucDAO.findAll();
    }

    public Optional<DanhMuc> getById(String maDM) {
        return danhMucDAO.findById(maDM);
    }

    public boolean addDanhMuc(DanhMuc dm) {
        return danhMucDAO.insert(dm);
    }

    public boolean updateDanhMuc(DanhMuc dm) {
        return danhMucDAO.update(dm);
    }

    public boolean deleteDanhMuc(String maDM) {
        return danhMucDAO.delete(maDM);
    }
}
