package com.fastfood.repository;

import com.fastfood.entity.DonHang;
import com.fastfood.enums.TrangThaiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    List<DonHang> findByTrangThai(TrangThaiDonHang trangThai);
    List<DonHang> findByMaCN(String maCN);
    List<DonHang> findByMaKH(String maKH);
    List<DonHang> findByMaNV(String maNV);
}
