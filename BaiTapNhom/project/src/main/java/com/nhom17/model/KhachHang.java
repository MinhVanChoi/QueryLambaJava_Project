package com.nhom17.model;

import java.time.LocalDate;
import java.util.Objects;

public class KhachHang {
    private String maKH;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private LocalDate ngayDangKy;

    public KhachHang() {
    }

    public KhachHang(String maKH, String hoTen, String soDienThoai, LocalDate ngayDangKy) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.ngayDangKy = ngayDangKy;
    }

    public KhachHang(String maKH, String hoTen, String soDienThoai, String email, String diaChi, LocalDate ngayDangKy) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.ngayDangKy = ngayDangKy;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDate getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(LocalDate ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-20s | %-12s | %-20s | %-25s | %-10s",
                maKH,
                hoTen,
                soDienThoai,
                email != null ? email : "N/A",
                diaChi != null ? diaChi : "N/A",
                ngayDangKy != null ? ngayDangKy.toString() : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KhachHang khachHang)) return false;
        return Objects.equals(maKH, khachHang.maKH);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKH);
    }
}
