package com.nhom17.model;

import com.nhom17.enums.PhuongThucThanhToan;
import com.nhom17.enums.TrangThaiThanhToan;
import com.nhom17.util.TablePrinter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ThanhToan {
    private String maTT;
    private Long maDH;
    private BigDecimal soTien;
    private PhuongThucThanhToan phuongThuc;
    private TrangThaiThanhToan trangThai;
    private LocalDateTime ngayThanhToan;

    public ThanhToan() {
    }

    public ThanhToan(String maTT, Long maDH, BigDecimal soTien, PhuongThucThanhToan phuongThuc, TrangThaiThanhToan trangThai, LocalDateTime ngayThanhToan) {
        this.maTT = maTT;
        this.maDH = maDH;
        this.soTien = soTien;
        this.phuongThuc = phuongThuc;
        this.trangThai = trangThai;
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getMaTT() {
        return maTT;
    }

    public void setMaTT(String maTT) {
        this.maTT = maTT;
    }

    public Long getMaDH() {
        return maDH;
    }

    public void setMaDH(Long maDH) {
        this.maDH = maDH;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public PhuongThucThanhToan getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(PhuongThucThanhToan phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public TrangThaiThanhToan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiThanhToan trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    @Override
    public String toString() {
        return String.format(
                "%-6s | %-6d | %-12s | %-14s | %-14s | %-16s",
                maTT,
                maDH,
                TablePrinter.formatCurrency(soTien),
                phuongThuc != null ? phuongThuc.name() : "N/A",
                trangThai != null ? trangThai.name() : "N/A",
                ngayThanhToan != null ? ngayThanhToan.toString().replace("T", " ") : "(Chờ TT)"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThanhToan thanhToan)) return false;
        return Objects.equals(maTT, thanhToan.maTT);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTT);
    }
}
