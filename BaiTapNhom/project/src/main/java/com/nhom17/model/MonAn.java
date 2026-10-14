package com.nhom17.model;

import com.nhom17.enums.TrangThaiMon;
import com.nhom17.util.TablePrinter;

import java.math.BigDecimal;
import java.util.Objects;

public class MonAn {
    private String maMon;
    private String tenMon;
    private BigDecimal giaBan;
    private String moTa;
    private TrangThaiMon trangThai;
    private String maDM;
    private String tenDM;

    public MonAn() {
    }

    public MonAn(String maMon, String tenMon, BigDecimal giaBan, TrangThaiMon trangThai, String maDM) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.trangThai = trangThai;
        this.maDM = maDM;
    }

    public MonAn(String maMon, String tenMon, BigDecimal giaBan, String moTa, TrangThaiMon trangThai, String maDM) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.maDM = maDM;
    }

    public MonAn(String maMon, String tenMon, BigDecimal giaBan, String moTa, TrangThaiMon trangThai, String maDM, String tenDM) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.maDM = maDM;
        this.tenDM = tenDM;
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

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public TrangThaiMon getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiMon trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaDM() {
        return maDM;
    }

    public void setMaDM(String maDM) {
        this.maDM = maDM;
    }

    public String getTenDM() {
        return tenDM;
    }

    public void setTenDM(String tenDM) {
        this.tenDM = tenDM;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-20s | %-12s | %-10s | %-6s | %-15s",
                maMon,
                tenMon,
                TablePrinter.formatCurrency(giaBan),
                trangThai != null ? trangThai.name() : "N/A",
                maDM != null ? maDM : "N/A",
                tenDM != null ? tenDM : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonAn monAn)) return false;
        return Objects.equals(maMon, monAn.maMon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maMon);
    }
}
