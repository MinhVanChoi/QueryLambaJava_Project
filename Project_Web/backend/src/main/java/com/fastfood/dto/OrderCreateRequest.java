package com.fastfood.dto;

import com.fastfood.enums.PhuongThucThanhToan;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
    
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotNull(message = "Branch code (maCN) is required")
    private String maCN;

    @NotNull(message = "Employee code (maNV) is required")
    private String maNV;

    private String maKH; // Nullable for guest/walk-in customer
    private String maKM; // Nullable for orders without promotion

    @NotEmpty(message = "Order must have at least one food item")
    @Valid
    private List<OrderItemRequest> items;

    private PhuongThucThanhToan phuongThuc; // Default: TIEN_MAT
    private Boolean payImmediately; // If true, process payment immediately as DA_THANH_TOAN
}
