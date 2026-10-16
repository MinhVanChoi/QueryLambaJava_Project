package com.fastfood.dto;

import com.fastfood.entity.ChiTietDonHang;
import com.fastfood.entity.DonHang;
import com.fastfood.entity.ThanhToan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponseDTO {
    private DonHang order;
    private String tenCN;
    private String tenNV;
    private String tenKH;
    private String tenKM;
    private List<ChiTietItemDTO> items;
    private List<ThanhToan> payments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietItemDTO {
        private Long maCTDH;
        private Long maDH;
        private String maMon;
        private String tenMon;
        private Integer soLuong;
        private java.math.BigDecimal donGia;
        private java.math.BigDecimal thanhTien;
    }
}
