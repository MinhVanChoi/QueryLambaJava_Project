package com.fastfood.repository;

import com.fastfood.entity.KhuyenMai;
import com.fastfood.enums.TrangThaiKhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, String> {
    List<KhuyenMai> findByTrangThai(TrangThaiKhuyenMai trangThai);
}
