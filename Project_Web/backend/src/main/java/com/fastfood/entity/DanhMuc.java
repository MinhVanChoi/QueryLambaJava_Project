package com.fastfood.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "danh_muc")
@Getter
@Setter
public class DanhMuc {

    @Id
    @Column(name = "maDM", length = 10, nullable = false)
    private String maDM;

    @Column(name = "tenDM", length = 100, nullable = false, unique = true)
    private String tenDM;

    @Column(name = "moTa", length = 255)
    private String moTa;

    public DanhMuc() {}

    public DanhMuc(String maDM, String tenDM, String moTa) {
        this.maDM = maDM;
        this.tenDM = tenDM;
        this.moTa = moTa;
    }
}
