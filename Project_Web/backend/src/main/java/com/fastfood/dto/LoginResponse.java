package com.fastfood.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String token;
    private String maTK;
    private String maNV;
    private String maKH;
    private String tenDangNhap;
    private String chucVu;
    private String maCN;
    private String hoTen;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String token, String maTK, String maNV, String maKH,
                         String tenDangNhap, String chucVu, String maCN, String hoTen, String role) {
        this.token = token;
        this.maTK = maTK;
        this.maNV = maNV;
        this.maKH = maKH;
        this.tenDangNhap = tenDangNhap;
        this.chucVu = chucVu;
        this.maCN = maCN;
        this.hoTen = hoTen;
        this.role = role;
    }
}
