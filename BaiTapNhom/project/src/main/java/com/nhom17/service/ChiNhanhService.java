package com.nhom17.service;

import com.nhom17.dao.ChiNhanhDAO;
import com.nhom17.model.ChiNhanh;

import java.util.List;
import java.util.Optional;

public class ChiNhanhService {

    private final ChiNhanhDAO chiNhanhDAO;

    public ChiNhanhService() {
        this.chiNhanhDAO = new ChiNhanhDAO();
    }

    public ChiNhanhService(ChiNhanhDAO chiNhanhDAO) {
        this.chiNhanhDAO = chiNhanhDAO;
    }

    public List<ChiNhanh> getAll() {
        return chiNhanhDAO.findAll();
    }

    public Optional<ChiNhanh> getById(String maCN) {
        return chiNhanhDAO.findById(maCN);
    }

    public boolean addChiNhanh(ChiNhanh cn) {
        return chiNhanhDAO.insert(cn);
    }

    public boolean updateChiNhanh(ChiNhanh cn) {
        return chiNhanhDAO.update(cn);
    }

    public boolean deleteChiNhanh(String maCN) {
        return chiNhanhDAO.delete(maCN);
    }
}
