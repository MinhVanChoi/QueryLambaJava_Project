package com.nhom17.service;

import com.nhom17.dao.KhachHangDAO;
import com.nhom17.model.KhachHang;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KhachHangService {

    private final KhachHangDAO khachHangDAO;

    public KhachHangService() {
        this.khachHangDAO = new KhachHangDAO();
    }

    public KhachHangService(KhachHangDAO khachHangDAO) {
        this.khachHangDAO = khachHangDAO;
    }

    public List<KhachHang> getAll() {
        return khachHangDAO.findAll();
    }

    public Optional<KhachHang> getById(String maKH) {
        return khachHangDAO.findById(maKH);
    }

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

    public boolean phoneExists(String phone) {
        return khachHangDAO.findAll().stream()
                .anyMatch(kh -> kh.getSoDienThoai().equalsIgnoreCase(phone));
    }

    public List<KhachHang> filter(Predicate<KhachHang> predicate) {
        return khachHangDAO.findAll().stream().filter(predicate).toList();
    }

    public Map<String, Long> countByEmailDomain() {
        return khachHangDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        kh -> {
                            String email = kh.getEmail();
                            if (email == null) return "no_email";
                            int at = email.indexOf('@');
                            return at >= 0 ? email.substring(at + 1).toLowerCase() : "unknown";
                        },
                        TreeMap::new,
                        Collectors.counting()
                ));
    }
}
