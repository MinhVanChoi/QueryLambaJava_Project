package com.nhom17.service;

import com.nhom17.dao.NhanVienDAO;
import com.nhom17.model.NhanVien;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NhanVienService {

    private final NhanVienDAO nhanVienDAO;

    public NhanVienService() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    public NhanVienService(NhanVienDAO nhanVienDAO) {
        this.nhanVienDAO = nhanVienDAO;
    }

    public List<NhanVien> getAll() {
        return nhanVienDAO.findAll();
    }

    public Optional<NhanVien> getById(String maNV) {
        return nhanVienDAO.findById(maNV);
    }

    public boolean addNhanVien(NhanVien nv) {
        return nhanVienDAO.insert(nv);
    }

    public boolean updateNhanVien(NhanVien nv) {
        return nhanVienDAO.update(nv);
    }

    public boolean deleteNhanVien(String maNV) {
        return nhanVienDAO.delete(maNV);
    }

    public List<NhanVien> searchByName(String keyword) {
        return nhanVienDAO.findAll().stream()
                .filter(nv -> nv.getHoTen().toLowerCase().contains(keyword.toLowerCase().trim()))
                .toList();
    }

    public List<NhanVien> filter(Predicate<NhanVien> predicate) {
        return nhanVienDAO.findAll().stream().filter(predicate).toList();
    }

    public Map<String, List<NhanVien>> groupByChiNhanh() {
        return nhanVienDAO.findAll().stream()
                .collect(Collectors.groupingBy(NhanVien::getMaCN, TreeMap::new, Collectors.toList()));
    }
}
