package com.nhom17.model;

import com.nhom17.enums.TrangThaiDonHang;
import com.nhom17.util.TablePrinter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class DonHang {
    private Long maDH;
    private LocalDateTime ngayDat;
    private TrangThaiDonHang trangThai;
    private BigDecimal tienHang;
    private BigDecimal tienGiam;
    private BigDecimal tongTien;
    private String maKH;
    private String tenKH;
    private String maKM;
    private String tenKM;
    private String maNV;
    private String tenNV;
    private String maCN;
    private String tenCN;

    public DonHang() {
    }

    public DonHang(Long maDH, LocalDateTime ngayDat, TrangThaiDonHang trangThai, BigDecimal tienHang, BigDecimal tienGiam, BigDecimal tongTien, String maKH, String maKM, String maNV, String maCN) {
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

    public DonHang(Long maDH, LocalDateTime ngayDat, TrangThaiDonHang trangThai, BigDecimal tienHang, BigDecimal tienGiam, BigDecimal tongTien, String maKH, String tenKH, String maKM, String tenKM, String maNV, String tenNV, String maCN, String tenCN) {
        this.maDH = maDH;
        this.ngayDat = ngayDat;
        this.trangThai = trangThai;
        this.tienHang = tienHang;
        this.tienGiam = tienGiam;
        this.tongTien = tongTien;
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.maCN = maCN;
        this.tenCN = tenCN;
    }

    public Long getMaDH() {
        return maDH;
    }

    public void setMaDH(Long maDH) {
        this.maDH = maDH;
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        this.ngayDat = ngayDat;
    }

    public TrangThaiDonHang getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiDonHang trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getTienHang() {
        return tienHang;
    }

    public void setTienHang(BigDecimal tienHang) {
        this.tienHang = tienHang;
    }

    public BigDecimal getTienGiam() {
        return tienGiam;
    }

    public void setTienGiam(BigDecimal tienGiam) {
        this.tienGiam = tienGiam;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
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

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getMaCN() {
        return maCN;
    }

    public void setMaCN(String maCN) {
        this.maCN = maCN;
    }

    public String getTenCN() {
        return tenCN;
    }

    public void setTenCN(String tenCN) {
        this.tenCN = tenCN;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6d | %-16s | %-14s | %-12s | %-10s | %-12s | %-15s | %-15s | %-6s",
                maDH,
                ngayDat != null ? ngayDat.toString().replace("T", " ") : "N/A",
                trangThai != null ? trangThai.name() : "N/A",
                TablePrinter.formatCurrency(tienHang),
                TablePrinter.formatCurrency(tienGiam),
                TablePrinter.formatCurrency(tongTien),
                tenKH != null ? tenKH : (maKH != null ? maKH : "(Vãng lai)"),
                tenNV != null ? tenNV : (maNV != null ? maNV : "N/A"),
                maCN != null ? maCN : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DonHang donHang)) return false;
        return Objects.equals(maDH, donHang.maDH);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDH);
    }
}
