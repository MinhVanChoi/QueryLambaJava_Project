package com.fastfood.entity;

import com.fastfood.enums.TrangThaiTaiKhoan;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tai_khoan")
@Getter
@Setter
@Builder
public class TaiKhoan {

    @Id
    @Column(name = "maTK", length = 10, nullable = false)
    private String maTK;

    @Column(name = "maNV", length = 10, nullable = false, unique = true)
    private String maNV;

    @Column(name = "tenDangNhap", length = 50, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "matKhauHash", length = 255, nullable = false)
    private String matKhauHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiTaiKhoan trangThai;

    @Column(name = "ngayTao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "lanDangNhapCuoi")
    private LocalDateTime lanDangNhapCuoi;

    public TaiKhoan() {}

    public TaiKhoan(String maTK, String maNV, String tenDangNhap, String matKhauHash,
                    TrangThaiTaiKhoan trangThai, LocalDateTime ngayTao, LocalDateTime lanDangNhapCuoi) {
        this.maTK = maTK;
        this.maNV = maNV;
        this.tenDangNhap = tenDangNhap;
        this.matKhauHash = matKhauHash;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }
}
