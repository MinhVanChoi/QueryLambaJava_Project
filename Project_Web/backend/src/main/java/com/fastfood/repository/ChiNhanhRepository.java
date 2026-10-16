package com.fastfood.repository;

import com.fastfood.entity.ChiNhanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChiNhanhRepository extends JpaRepository<ChiNhanh, String> {
    Optional<ChiNhanh> findBySoDienThoai(String soDienThoai);
}
