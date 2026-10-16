package com.fastfood.entity;

import com.fastfood.enums.TrangThaiDonHang;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_hang")
@Getter
@Setter
@Builder
public class DonHang {

    @Id
    @Column(name = "maDH", nullable = false)
    private Long maDH;

    @Column(name = "ngayDat", nullable = false)
    private LocalDateTime ngayDat;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiDonHang trangThai;

    @Column(name = "tienHang", precision = 12, scale = 2, nullable = false)
    private BigDecimal tienHang;

    @Column(name = "tienGiam", precision = 12, scale = 2, nullable = false)
    private BigDecimal tienGiam;

    @Column(name = "tongTien", precision = 12, scale = 2, nullable = false)
    private BigDecimal tongTien;

    @Column(name = "maKH", length = 10)
    private String maKH;

    @Column(name = "maKM", length = 10)
    private String maKM;

    @Column(name = "maNV", length = 10, nullable = false)
    private String maNV;

    @Column(name = "maCN", length = 10, nullable = false)
    private String maCN;

    public DonHang() {}

    public DonHang(Long maDH, LocalDateTime ngayDat, TrangThaiDonHang trangThai,
                   BigDecimal tienHang, BigDecimal tienGiam, BigDecimal tongTien,
                   String maKH, String maKM, String maNV, String maCN) {
        this.maDH = maDH;
        this.ngayDat = ngayDat;
        this.trangThai = trangThai;
        this.tienHang = tienHang;
        this.tienGiam = tienGiam;
        this.tongTien = tongTien;
        this.maKH = maKH;
        this.maKM = maKM;
        this.maNV = maNV;
        this.maCN = maCN;
    }
}
