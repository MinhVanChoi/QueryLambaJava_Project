package com.fastfood.entity;

import com.fastfood.enums.TrangThaiKhuyenMai;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "khuyen_mai")
@Getter
@Setter
@Builder
public class KhuyenMai {

    @Id
    @Column(name = "maKM", length = 10, nullable = false)
    private String maKM;

    @Column(name = "tenKM", length = 150, nullable = false)
    private String tenKM;

    @Column(name = "phanTramGiam", nullable = false)
    private Integer phanTramGiam;

    @Column(name = "donToiThieu", precision = 12, scale = 2, nullable = false)
    private BigDecimal donToiThieu;

    @Column(name = "giamToiDa", precision = 12, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "ngayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiKhuyenMai trangThai;

    public KhuyenMai() {}

    public KhuyenMai(String maKM, String tenKM, Integer phanTramGiam, BigDecimal donToiThieu,
                      BigDecimal giamToiDa, LocalDate ngayBatDau, LocalDate ngayKetThuc,
                      TrangThaiKhuyenMai trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.phanTramGiam = phanTramGiam;
        this.donToiThieu = donToiThieu;
        this.giamToiDa = giamToiDa;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }
}
