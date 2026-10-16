package com.fastfood.repository;

import com.fastfood.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    Optional<TaiKhoan> findByMaNV(String maNV);
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
}
