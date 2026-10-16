package com.fastfood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_don_hang", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"maDH", "maMon"})
})
@Getter
@Setter
@Builder
public class ChiTietDonHang {

    @Id
    @Column(name = "maCTDH", nullable = false)
    private Long maCTDH;

    @Column(name = "maDH", nullable = false)
    private Long maDH;

    @Column(name = "maMon", length = 10, nullable = false)
    private String maMon;

    @Column(name = "soLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "donGia", precision = 12, scale = 2, nullable = false)
    private BigDecimal donGia;

    @Column(name = "thanhTien", precision = 12, scale = 2, nullable = false)
    private BigDecimal thanhTien;

    public ChiTietDonHang() {}

    public ChiTietDonHang(Long maCTDH, Long maDH, String maMon, Integer soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        this.maCTDH = maCTDH;
        this.maDH = maDH;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }
}
