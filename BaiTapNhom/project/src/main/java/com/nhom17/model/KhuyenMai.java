package com.nhom17.model;

import com.nhom17.enums.TrangThaiKhuyenMai;
import com.nhom17.util.TablePrinter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private int phanTramGiam;
    private BigDecimal donToiThieu;
    private BigDecimal giamToiDa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private TrangThaiKhuyenMai trangThai;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKM, String tenKM, int phanTramGiam, BigDecimal donToiThieu, BigDecimal giamToiDa, LocalDate ngayBatDau, LocalDate ngayKetThuc, TrangThaiKhuyenMai trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.phanTramGiam = phanTramGiam;
        this.donToiThieu = donToiThieu;
        this.giamToiDa = giamToiDa;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public int getPhanTramGiam() {
        return phanTramGiam;
    }

    public void setPhanTramGiam(int phanTramGiam) {
        this.phanTramGiam = phanTramGiam;
    }

    public BigDecimal getDonToiThieu() {
        return donToiThieu;
    }

    public void setDonToiThieu(BigDecimal donToiThieu) {
        this.donToiThieu = donToiThieu;
    }

    public BigDecimal getGiamToiDa() {
        return giamToiDa;
    }

    public void setGiamToiDa(BigDecimal giamToiDa) {
        this.giamToiDa = giamToiDa;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public TrangThaiKhuyenMai getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiKhuyenMai trangThai) {
        this.trangThai = trangThai;
    }

    public boolean conHieuLuc(LocalDate ngay) {
        return !ngay.isBefore(ngayBatDau) && !ngay.isAfter(ngayKetThuc);
    }

    public BigDecimal tinhTienGiam(BigDecimal tienHang) {
        if (tienHang.compareTo(donToiThieu) < 0) return BigDecimal.ZERO;
        BigDecimal giam = tienHang.multiply(BigDecimal.valueOf(phanTramGiam)).divide(BigDecimal.valueOf(100));
        return (giamToiDa == null) ? giam : giam.min(giamToiDa);
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-25s | %-3d%% | %-12s | %-12s | %-10s | %-10s | %-12s",
                maKM,
                tenKM,
                phanTramGiam,
                TablePrinter.formatCurrency(donToiThieu),
                giamToiDa != null ? TablePrinter.formatCurrency(giamToiDa) : "Không giới hạn",
                ngayBatDau != null ? ngayBatDau.toString() : "N/A",
                ngayKetThuc != null ? ngayKetThuc.toString() : "N/A",
                trangThai != null ? trangThai.name() : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KhuyenMai khuyenMai)) return false;
        return Objects.equals(maKM, khuyenMai.maKM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKM);
    }
}
