package com.nhom17.model;

import com.nhom17.util.TablePrinter;

import java.math.BigDecimal;
import java.util.Objects;

public class ChiTietDonHang {
    private Long maCTDH;
    private Long maDH;
    private String maMon;
    private String tenMon;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(Long maCTDH, Long maDH, String maMon, int soLuong, BigDecimal donGia) {
        this.maCTDH = maCTDH;
        this.maDH = maDH;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    public ChiTietDonHang(Long maCTDH, Long maDH, String maMon, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        this.maCTDH = maCTDH;
        this.maDH = maDH;
        this.maMon = maMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public ChiTietDonHang(Long maCTDH, Long maDH, String maMon, String tenMon, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        this.maCTDH = maCTDH;
        this.maDH = maDH;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public Long getMaCTDH() {
        return maCTDH;
    }

    public void setMaCTDH(Long maCTDH) {
        this.maCTDH = maCTDH;
    }

    public Long getMaDH() {
        return maDH;
    }

    public void setMaDH(Long maDH) {
        this.maDH = maDH;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        if (this.donGia != null) {
            this.thanhTien = this.donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
        if (this.donGia != null) {
            this.thanhTien = this.donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    public BigDecimal getThanhTien() {
        return (thanhTien != null) ? thanhTien : (donGia != null ? donGia.multiply(BigDecimal.valueOf(soLuong)) : BigDecimal.ZERO);
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6d | %-6d | %-6s | %-18s | %-3d | %-12s | %-12s",
                maCTDH,
                maDH,
                maMon,
                tenMon != null ? tenMon : "N/A",
                soLuong,
                TablePrinter.formatCurrency(donGia),
                TablePrinter.formatCurrency(getThanhTien())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietDonHang that)) return false;
        return Objects.equals(maCTDH, that.maCTDH);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTDH);
    }
}
