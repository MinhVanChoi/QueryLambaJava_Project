package com.fastfood.entity;

import com.fastfood.enums.PhuongThucThanhToan;
import com.fastfood.enums.TrangThaiThanhToan;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
@Getter
@Setter
@Builder
public class ThanhToan {

    @Id
    @Column(name = "maTT", length = 10, nullable = false)
    private String maTT;

    @Column(name = "maDH", nullable = false)
    private Long maDH;

    @Column(name = "soTien", precision = 12, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuongThuc", length = 20, nullable = false)
    private PhuongThucThanhToan phuongThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 20, nullable = false)
    private TrangThaiThanhToan trangThai;

    @Column(name = "ngayThanhToan")
    private LocalDateTime ngayThanhToan;

    public ThanhToan() {}

    public ThanhToan(String maTT, Long maDH, BigDecimal soTien, PhuongThucThanhToan phuongThuc,
                     TrangThaiThanhToan trangThai, LocalDateTime ngayThanhToan) {
        this.maTT = maTT;
        this.maDH = maDH;
        this.soTien = soTien;
        this.phuongThuc = phuongThuc;
        this.trangThai = trangThai;
        this.ngayThanhToan = ngayThanhToan;
    }
}
