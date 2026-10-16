package com.fastfood.entity;

import com.fastfood.enums.ChucVu;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@Builder
public class NhanVien {

    @Id
    @Column(name = "maNV", length = 10, nullable = false)
    private String maNV;

    @Column(name = "hoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "soDienThoai", length = 20, nullable = false, unique = true)
    private String soDienThoai;

    @Enumerated(EnumType.STRING)
    @Column(name = "chucVu", length = 20, nullable = false)
    private ChucVu chucVu;

    @Column(name = "maCN", length = 10, nullable = false)
    private String maCN;

    public NhanVien() {}

    public NhanVien(String maNV, String hoTen, String soDienThoai, ChucVu chucVu, String maCN) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.chucVu = chucVu;
        this.maCN = maCN;
    }
}
