package com.fastfood.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ton_kho", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"maMon", "maCN"})
})
@Getter
@Setter
@Builder
public class TonKho {

    @Id
    @Column(name = "maTon", length = 10, nullable = false)
    private String maTon;

    @Column(name = "maMon", length = 10, nullable = false)
    private String maMon;

    @Column(name = "maCN", length = 10, nullable = false)
    private String maCN;

    @Column(name = "soLuongTon", nullable = false)
    private Integer soLuongTon;

    @Column(name = "ngayCapNhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    public TonKho() {}

    public TonKho(String maTon, String maMon, String maCN, Integer soLuongTon, LocalDateTime ngayCapNhat) {
        this.maTon = maTon;
        this.maMon = maMon;
        this.maCN = maCN;
        this.soLuongTon = soLuongTon;
        this.ngayCapNhat = ngayCapNhat;
    }
}
