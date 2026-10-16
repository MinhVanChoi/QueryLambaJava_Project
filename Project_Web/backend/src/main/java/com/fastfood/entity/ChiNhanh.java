package com.fastfood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Entity
@Table(name = "chi_nhanh")
@Getter
@Setter
@Builder
public class ChiNhanh {

    @Id
    @Column(name = "maCN", length = 10, nullable = false)
    private String maCN;

    @Column(name = "tenCN", length = 100, nullable = false)
    private String tenCN;

    @Column(name = "diaChi", length = 255, nullable = false)
    private String diaChi;

    @Column(name = "soDienThoai", length = 20, nullable = false, unique = true)
    private String soDienThoai;

    public ChiNhanh() {}

    public ChiNhanh(String maCN, String tenCN, String diaChi, String soDienThoai) {
        this.maCN = maCN;
        this.tenCN = tenCN;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
    }
}
