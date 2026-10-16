package com.fastfood.repository;

import com.fastfood.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, String> {
    Optional<DanhMuc> findByTenDM(String tenDM);
}
