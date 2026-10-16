package com.fastfood;

import com.fastfood.entity.MonAn;
import com.fastfood.entity.NhanVien;
import com.fastfood.service.DatabaseInitService;
import com.fastfood.service.FastFoodService;
import com.fastfood.service.MonAnService;
import com.fastfood.service.NhanVienService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StreamApiMethodsTest {

    @Autowired
    private FastFoodService fastFoodService;

    @Autowired
    private MonAnService monAnService;

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private DatabaseInitService databaseInitService;

    @BeforeEach
    public void setUp() {
        databaseInitService.reinitialize();
    }

    @Test
    public void testFunctionalStreamApiMethods() {
        // 1. Filter MonAn by Category
        List<MonAn> burgers = monAnService.filter(m -> "DM01".equals(m.getMaDM()));
        assertFalse(burgers.isEmpty());
        assertEquals(4, burgers.size());

        // 2. Search NhanVien by Name keyword
        List<NhanVien> found = nhanVienService.searchByName("An");
        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(nv -> nv.getHoTen().contains("An")));

        // 3. Sort food by price Ascending
        List<MonAn> sortedAsc = monAnService.sortPriceAsc();
        assertTrue(sortedAsc.get(0).getGiaBan().compareTo(sortedAsc.get(sortedAsc.size() - 1).getGiaBan()) <= 0);

        // 4. Find most expensive food item
        Optional<MonAn> mostExpensive = monAnService.findMostExpensive();
        assertTrue(mostExpensive.isPresent());
        assertEquals(new BigDecimal("120000.00"), mostExpensive.get().getGiaBan());

        // 5. Calculate average order value
        double avg = fastFoodService.calculateAverageOrderValue();
        assertTrue(avg > 0);

        // 6. GroupingBy Category
        Map<String, List<MonAn>> byDM = fastFoodService.groupMonAnByDanhMuc();
        assertTrue(byDM.containsKey("DM01"));

        // 7. Total Completed Revenue equals exactly 3806250.00
        BigDecimal revenue = fastFoodService.calculateTotalCompletedRevenue();
        assertEquals(new BigDecimal("3806250.00"), revenue);

        // 8. Dynamic field update via BiConsumer
        boolean updated = monAnService.updateField("M001", "gia", "52000");
        assertTrue(updated);
        assertEquals(new BigDecimal("52000.00"), monAnService.getById("M001").getGiaBan());
    }
}
