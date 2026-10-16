package com.fastfood;

import com.fastfood.dto.QueryComparisonDTO;
import com.fastfood.dto.QueryResultDTO;
import com.fastfood.service.DatabaseInitService;
import com.fastfood.service.QueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class All30QueriesTest {

    @Autowired
    private QueryService queryService;

    @Autowired
    private DatabaseInitService databaseInitService;

    @BeforeEach
    public void setUp() {
        databaseInitService.reinitialize();
    }

    @Test
    public void testAll30QueriesParity() {
        // Test all 30 queries via SQL and Stream API, verify results
        for (int i = 1; i <= 30; i++) {
            QueryResultDTO sqlRes = queryService.executeQuery(i, "sql");
            assertNotNull(sqlRes, "SQL Query " + i + " should not be null");
            assertNotNull(sqlRes.getRows(), "SQL Query " + i + " rows should not be null");

            QueryResultDTO lambdaRes = queryService.executeQuery(i, "lambda");
            assertNotNull(lambdaRes, "Lambda Query " + i + " should not be null");
            assertNotNull(lambdaRes.getRows(), "Lambda Query " + i + " rows should not be null");

            QueryComparisonDTO comp = queryService.compareQuery(i);
            assertTrue(comp.isMatched(), "Query " + i + " parity mismatch: " + comp.getMatchSummary());
        }

        // Verify specific counts as in original baseline
        assertEquals(1, queryService.executeQuery(1, "sql").getTotalRows(), "Q1 NV06");
        assertEquals(4, queryService.executeQuery(2, "sql").getTotalRows(), "Q2 4 burgers");
        assertEquals(19, queryService.executeQuery(3, "sql").getTotalRows(), "Q3 19 low stock");
        assertEquals(6, queryService.executeQuery(4, "sql").getTotalRows(), "Q4 6 HCM customers");
        assertEquals(4, queryService.executeQuery(5, "sql").getTotalRows(), "Q5 4 payment methods");
        assertEquals(7, queryService.executeQuery(6, "sql").getTotalRows(), "Q6 7 orders in Feb 2026");
        assertEquals(28, queryService.executeQuery(7, "sql").getTotalRows(), "Q7 28 orders");
        assertEquals(3, queryService.executeQuery(8, "sql").getTotalRows(), "Q8 3 items in order 1001");
        assertEquals(28, queryService.executeQuery(9, "sql").getTotalRows(), "Q9 28 orders reconciled");
        assertEquals(1, queryService.executeQuery(10, "sql").getTotalRows(), "Q10 total revenue record");
        assertEquals(6, queryService.executeQuery(11, "sql").getTotalRows(), "Q11 6 months in 2026");
        assertEquals(3, queryService.executeQuery(12, "sql").getTotalRows(), "Q12 Top 3 food items");
        assertEquals(10, queryService.executeQuery(13, "sql").getTotalRows(), "Q13 10 customers");
        assertEquals(2, queryService.executeQuery(14, "sql").getTotalRows(), "Q14 2 customers without orders");
        assertEquals(2, queryService.executeQuery(15, "sql").getTotalRows(), "Q15 2 unsold food items");
        assertEquals(8, queryService.executeQuery(16, "sql").getTotalRows(), "Q16 8 employees");
        assertEquals(6, queryService.executeQuery(17, "sql").getTotalRows(), "Q17 6 repeat customers");
        assertEquals(4, queryService.executeQuery(18, "sql").getTotalRows(), "Q18 4 price differences");
        assertEquals(1, queryService.executeQuery(19, "sql").getTotalRows(), "Q19 1 locked account");
        assertEquals(13, queryService.executeQuery(20, "sql").getTotalRows(), "Q20 13 above-average orders");
        assertEquals(1, queryService.executeQuery(21, "sql").getTotalRows(), "Q21 1 account never logged in");
        assertEquals(11, queryService.executeQuery(22, "sql").getTotalRows(), "Q22 11 orders with promo");
        assertEquals(4, queryService.executeQuery(23, "sql").getTotalRows(), "Q23 4 promotions");
        assertEquals(1, queryService.executeQuery(24, "sql").getTotalRows(), "Q24 1 unused promo");
        assertEquals(2, queryService.executeQuery(25, "sql").getTotalRows(), "Q25 2 promo comparison groups");
        assertEquals(2, queryService.executeQuery(26, "sql").getTotalRows(), "Q26 2 capped discount orders");
        assertEquals(4, queryService.executeQuery(27, "sql").getTotalRows(), "Q27 4 payment methods");
        assertEquals(3, queryService.executeQuery(28, "sql").getTotalRows(), "Q28 3 selling branches");
        assertEquals(4, queryService.executeQuery(29, "sql").getTotalRows(), "Q29 4 above-average spenders");
        assertEquals(1, queryService.executeQuery(30, "sql").getTotalRows(), "Q30 1 order with failed then success");
    }
}
