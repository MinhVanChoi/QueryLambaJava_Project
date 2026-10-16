package com.fastfood.repository;

import com.fastfood.entity.ThanhToan;
import com.fastfood.enums.PhuongThucThanhToan;
import com.fastfood.enums.TrangThaiThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, String> {
    List<ThanhToan> findByMaDH(Long maDH);
    List<ThanhToan> findByTrangThai(TrangThaiThanhToan trangThai);
    List<ThanhToan> findByPhuongThuc(PhuongThucThanhToan phuongThuc);
}
