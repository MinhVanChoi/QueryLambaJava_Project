package com.fastfood.repository;

import com.fastfood.entity.TonKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho, String> {
    List<TonKho> findByMaCN(String maCN);
    List<TonKho> findByMaMon(String maMon);
    Optional<TonKho> findByMaMonAndMaCN(String maMon, String maCN);
}
