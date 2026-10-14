package com.nhom17.model;

import java.util.Objects;

public class ChiNhanh {
    private String maCN;
    private String tenCN;
    private String diaChi;
    private String soDienThoai;

    public ChiNhanh() {
    }

    public ChiNhanh(String maCN, String tenCN, String diaChi, String soDienThoai) {
        this.maCN = maCN;
        this.tenCN = tenCN;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
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

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-22s | %-35s | %-12s",
                maCN,
                tenCN,
                diaChi,
                soDienThoai
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiNhanh chiNhanh)) return false;
        return Objects.equals(maCN, chiNhanh.maCN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCN);
    }
}
