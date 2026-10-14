package com.nhom17;

import com.nhom17.database.DatabaseInitializer;
import com.nhom17.enums.TrangThaiMon;
import com.nhom17.model.MonAn;
import com.nhom17.service.FastFoodService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StreamApiTest {

    private static FastFoodService service;

    @BeforeAll
    public static void setUp() {
        DatabaseInitializer.reinitialize();
        service = new FastFoodService();
    }

    @Test
    public void testStreamApiMethods() {
        // 1. Filter MonAn
        List<MonAn> burgers = service.filterMonAn(m -> m.getMaDM().equals("DM01"));
        assertFalse(burgers.isEmpty());

        // 2. Search NhanVien by Name
        assertFalse(service.searchNhanVienByName("An").isEmpty());

        // 3. Username exists
        assertTrue(service.usernameExists("admin01"));
        assertFalse(service.usernameExists("non_existing_user_999"));

        // 4. Sort Asc / Desc
        List<MonAn> sortedAsc = service.sortMonAnByPriceAsc();
        assertTrue(sortedAsc.get(0).getGiaBan().compareTo(sortedAsc.get(sortedAsc.size() - 1).getGiaBan()) <= 0);

        // 5. Max food
        Optional<MonAn> mostExpensive = service.findMostExpensiveFood();
        assertTrue(mostExpensive.isPresent());
        assertEquals(new BigDecimal("120000.00"), mostExpensive.get().getGiaBan());

        // 6. Average order value
        double avg = service.calculateAverageOrderValue();
        assertTrue(avg > 0);

        // 7. GroupingBy
        Map<String, List<MonAn>> byDM = service.groupMonAnByDanhMuc();
        assertTrue(byDM.containsKey("DM01"));

        // 8. Total Completed Revenue
        BigDecimal rev = service.calculateTotalCompletedRevenue();
        assertEquals(new BigDecimal("3806250.00"), rev);
    }
}
