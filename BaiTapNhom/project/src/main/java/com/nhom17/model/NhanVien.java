package com.nhom17.model;

import com.nhom17.enums.ChucVu;

import java.util.Objects;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String soDienThoai;
    private ChucVu chucVu;
    private String maCN;
    private String tenCN;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String soDienThoai, ChucVu chucVu, String maCN) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.chucVu = chucVu;
        this.maCN = maCN;
    }

    public NhanVien(String maNV, String hoTen, String soDienThoai, ChucVu chucVu, String maCN, String tenCN) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.chucVu = chucVu;
        this.maCN = maCN;
        this.tenCN = tenCN;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
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

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        this.chucVu = chucVu;
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
                "%-6s | %-20s | %-12s | %-12s | %-6s | %-20s",
                maNV,
                hoTen,
                soDienThoai,
                chucVu != null ? chucVu.name() : "N/A",
                maCN != null ? maCN : "N/A",
                tenCN != null ? tenCN : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhanVien nhanVien)) return false;
        return Objects.equals(maNV, nhanVien.maNV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }
}
