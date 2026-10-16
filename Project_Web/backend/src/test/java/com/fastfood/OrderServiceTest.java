package com.fastfood;

import com.fastfood.dto.OrderCreateRequest;
import com.fastfood.dto.OrderDetailResponseDTO;
import com.fastfood.dto.OrderItemRequest;
import com.fastfood.enums.PhuongThucThanhToan;
import com.fastfood.enums.TrangThaiDonHang;
import com.fastfood.service.DatabaseInitService;
import com.fastfood.service.DonHangService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private DonHangService donHangService;

    @Autowired
    private DatabaseInitService databaseInitService;

    @BeforeEach
    public void setUp() {
        databaseInitService.reinitialize();
    }

    @Test
    public void testCreateOrderWithPromotionAndImmediatePayment() {
        // Create an order with promotion KM03 (10% off for order >= 100,000, max 20,000)
        // 2 x M001 (50,000 * 2 = 100,000) + 1 x M007 (30,000) = 130,000
        // Discount 10% = 13,000. Total = 117,000
        OrderCreateRequest req = OrderCreateRequest.builder()
                .maCN("CN01")
                .maNV("NV01")
                .maKH("KH001")
                .maKM("KM03")
                .payImmediately(true)
                .phuongThuc(PhuongThucThanhToan.TIEN_MAT)
                .items(List.of(
                        OrderItemRequest.builder().maMon("M001").soLuong(2).build(),
                        OrderItemRequest.builder().maMon("M007").soLuong(1).build()
                ))
                .build();

        OrderDetailResponseDTO created = donHangService.createOrder(req);
        assertNotNull(created);
        assertNotNull(created.getOrder());
        assertEquals(TrangThaiDonHang.DA_HOAN_THANH, created.getOrder().getTrangThai());
        assertEquals(new BigDecimal("130000.00"), created.getOrder().getTienHang());
        assertEquals(new BigDecimal("13000.00"), created.getOrder().getTienGiam());
        assertEquals(new BigDecimal("117000.00"), created.getOrder().getTongTien());
        assertEquals(2, created.getItems().size());
        assertFalse(created.getPayments().isEmpty());
    }
}
