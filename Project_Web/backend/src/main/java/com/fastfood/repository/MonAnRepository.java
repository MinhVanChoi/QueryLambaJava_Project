package com.fastfood.repository;

import com.fastfood.entity.MonAn;
import com.fastfood.enums.TrangThaiMon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonAnRepository extends JpaRepository<MonAn, String> {
    List<MonAn> findByMaDM(String maDM);
    List<MonAn> findByTrangThai(TrangThaiMon trangThai);
}
