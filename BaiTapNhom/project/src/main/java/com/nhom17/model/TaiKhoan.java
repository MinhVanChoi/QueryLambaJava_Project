package com.nhom17.model;

import com.nhom17.enums.TrangThaiTaiKhoan;

import java.time.LocalDateTime;
import java.util.Objects;

public class TaiKhoan {
    private String maTK;
    private String maNV;
    private String hoTenNV;
    private String tenDangNhap;
    private String matKhauHash;
    private TrangThaiTaiKhoan trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime lanDangNhapCuoi;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTK, String maNV, String tenDangNhap, String matKhauHash, TrangThaiTaiKhoan trangThai, LocalDateTime ngayTao, LocalDateTime lanDangNhapCuoi) {
        this.maTK = maTK;
        this.maNV = maNV;
        this.tenDangNhap = tenDangNhap;
        this.matKhauHash = matKhauHash;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }

    public TaiKhoan(String maTK, String maNV, String hoTenNV, String tenDangNhap, String matKhauHash, TrangThaiTaiKhoan trangThai, LocalDateTime ngayTao, LocalDateTime lanDangNhapCuoi) {
        this.maTK = maTK;
        this.maNV = maNV;
        this.hoTenNV = hoTenNV;
        this.tenDangNhap = tenDangNhap;
        this.matKhauHash = matKhauHash;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }

    public String getMaTK() {
        return maTK;
    }

    public void setMaTK(String maTK) {
        this.maTK = maTK;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTenNV() {
        return hoTenNV;
    }

    public void setHoTenNV(String hoTenNV) {
        this.hoTenNV = hoTenNV;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauHash() {
        return matKhauHash;
    }

    public void setMatKhauHash(String matKhauHash) {
        this.matKhauHash = matKhauHash;
    }

    public TrangThaiTaiKhoan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiTaiKhoan trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getLanDangNhapCuoi() {
        return lanDangNhapCuoi;
    }

    public void setLanDangNhapCuoi(LocalDateTime lanDangNhapCuoi) {
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-6s | %-15s | %-8s | %-16s | %-16s",
                maTK,
                maNV,
                tenDangNhap,
                trangThai != null ? trangThai.name() : "N/A",
                ngayTao != null ? ngayTao.toString().replace("T", " ") : "N/A",
                lanDangNhapCuoi != null ? lanDangNhapCuoi.toString().replace("T", " ") : "(Chưa đăng nhập)"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaiKhoan taiKhoan)) return false;
        return Objects.equals(maTK, taiKhoan.maTK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTK);
    }
}
