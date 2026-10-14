package com.nhom17.model;

import java.util.Objects;

public class DanhMuc {
    private String maDM;
    private String tenDM;
    private String moTa;

    public DanhMuc() {
    }

    public DanhMuc(String maDM, String tenDM) {
        this.maDM = maDM;
        this.tenDM = tenDM;
    }

    public DanhMuc(String maDM, String tenDM, String moTa) {
        this.maDM = maDM;
        this.tenDM = tenDM;
        this.moTa = moTa;
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-18s | %-30s",
                maDM,
                tenDM,
                moTa != null ? moTa : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DanhMuc danhMuc)) return false;
        return Objects.equals(maDM, danhMuc.maDM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDM);
    }
}
