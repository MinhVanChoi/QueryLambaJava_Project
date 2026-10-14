package com.nhom17.service;

import com.nhom17.dao.MonAnDAO;
import com.nhom17.enums.TrangThaiMon;
import com.nhom17.model.MonAn;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MonAnService {

    private final MonAnDAO monAnDAO;

    public MonAnService() {
        this.monAnDAO = new MonAnDAO();
    }

    public MonAnService(MonAnDAO monAnDAO) {
        this.monAnDAO = monAnDAO;
    }

    public List<MonAn> getAll() {
        return monAnDAO.findAll();
    }

    public Optional<MonAn> getById(String maMon) {
        return monAnDAO.findById(maMon);
    }

    public boolean addMonAn(MonAn mon) {
        if (mon.getGiaBan() == null || mon.getGiaBan().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return monAnDAO.insert(mon);
    }

    public boolean updateMonAn(MonAn mon) {
        return monAnDAO.update(mon);
    }

    public boolean deleteMonAn(String maMon) {
        return monAnDAO.delete(maMon);
    }

    public List<MonAn> filter(Predicate<MonAn> predicate) {
        return monAnDAO.findAll().stream().filter(predicate).toList();
    }

    public boolean updateSingleField(String maMon, String field, String newValue) {
        return monAnDAO.findById(maMon).map(mon -> {
            Map<String, BiConsumer<MonAn, String>> updaters = Map.of(
                    "ten", MonAn::setTenMon,
                    "gia", (m, v) -> m.setGiaBan(new BigDecimal(v)),
                    "mota", MonAn::setMoTa,
                    "trangthai", (m, v) -> m.setTrangThai(TrangThaiMon.valueOf(v.toUpperCase())),
                    "danhmuc", MonAn::setMaDM
            );
            BiConsumer<MonAn, String> updater = updaters.get(field.toLowerCase().trim());
            if (updater != null) {
                try {
                    updater.accept(mon, newValue);
                    return monAnDAO.update(mon);
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }).orElse(false);
    }

    public List<MonAn> sortByPriceAsc() {
        return monAnDAO.findAll().stream()
                .sorted(Comparator.comparing(MonAn::getGiaBan))
                .toList();
    }

    public List<MonAn> sortByPriceDescThenName() {
        return monAnDAO.findAll().stream()
                .sorted(Comparator.comparing(MonAn::getGiaBan).reversed().thenComparing(MonAn::getTenMon))
                .toList();
    }

    public Optional<MonAn> findMostExpensive() {
        return monAnDAO.findAll().stream()
                .max(Comparator.comparing(MonAn::getGiaBan));
    }

    public Map<String, List<MonAn>> groupByDanhMuc() {
        return monAnDAO.findAll().stream()
                .collect(Collectors.groupingBy(MonAn::getMaDM, TreeMap::new, Collectors.toList()));
    }
}
