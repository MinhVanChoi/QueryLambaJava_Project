package com.nhom17.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class TonKho {
    private String maTon;
    private String maMon;
    private String tenMon;
    private String maCN;
    private String tenCN;
    private int soLuongTon;
    private LocalDateTime ngayCapNhat;

    public TonKho() {
    }

    public TonKho(String maTon, String maCN, String maMon, int soLuongTon, LocalDateTime ngayCapNhat) {
        this.maTon = maTon;
        this.maCN = maCN;
        this.maMon = maMon;
        this.soLuongTon = soLuongTon;
        this.ngayCapNhat = ngayCapNhat;
    }

    public TonKho(String maTon, String maCN, String tenCN, String maMon, String tenMon, int soLuongTon, LocalDateTime ngayCapNhat) {
        this.maTon = maTon;
        this.maCN = maCN;
        this.tenCN = tenCN;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soLuongTon = soLuongTon;
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getMaTon() {
        return maTon;
    }

    public void setMaTon(String maTon) {
        this.maTon = maTon;
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

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-6s | %-18s | %-6s | %-18s | %-4d | %-16s",
                maTon,
                maCN != null ? maCN : "N/A",
                tenCN != null ? tenCN : "N/A",
                maMon != null ? maMon : "N/A",
                tenMon != null ? tenMon : "N/A",
                soLuongTon,
                ngayCapNhat != null ? ngayCapNhat.toString().replace("T", " ") : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TonKho tonKho)) return false;
        return Objects.equals(maTon, tonKho.maTon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTon);
    }
}
