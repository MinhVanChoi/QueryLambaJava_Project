package com.fastfood.entity;

import com.fastfood.enums.TrangThaiMon;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "mon_an")
@Getter
@Setter
@Builder
public class MonAn {

    @Id
    @Column(name = "maMon", length = 10, nullable = false)
    private String maMon;

    @Column(name = "tenMon", length = 100, nullable = false)
    private String tenMon;

    @Column(name = "giaBan", precision = 12, scale = 2, nullable = false)
    private BigDecimal giaBan;

    @Column(name = "moTa", length = 255)
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiMon trangThai;

    @Column(name = "maDM", length = 10, nullable = false)
    private String maDM;

    public MonAn() {}

    public MonAn(String maMon, String tenMon, BigDecimal giaBan, String moTa, TrangThaiMon trangThai, String maDM) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.maDM = maDM;
    }
}
