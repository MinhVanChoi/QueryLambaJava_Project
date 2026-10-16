package com.fastfood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@Builder
public class KhachHang {

    @Id
    @Column(name = "maKH", length = 10, nullable = false)
    private String maKH;

    @Column(name = "hoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "soDienThoai", length = 20, nullable = false, unique = true)
    private String soDienThoai;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "diaChi", length = 255)
    private String diaChi;

    @Column(name = "ngayDangKy", nullable = false)
    private LocalDate ngayDangKy;

    public KhachHang() {}

    public KhachHang(String maKH, String hoTen, String soDienThoai, String email, String diaChi, LocalDate ngayDangKy) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.ngayDangKy = ngayDangKy;
    }
}
