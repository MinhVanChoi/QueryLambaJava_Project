package com.fastfood.service;

import com.fastfood.dto.QueryComparisonDTO;
import com.fastfood.dto.QueryMetaDTO;
import com.fastfood.dto.QueryResultDTO;
import com.fastfood.entity.*;
import com.fastfood.enums.*;
import com.fastfood.exception.BadRequestException;
import com.fastfood.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QueryService {

    private final JdbcTemplate jdbcTemplate;
    private final ChiNhanhRepository chiNhanhRepository;
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final MonAnRepository monAnRepository;
    private final TonKhoRepository tonKhoRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ThanhToanRepository thanhToanRepository;

    public QueryService(JdbcTemplate jdbcTemplate,
                        ChiNhanhRepository chiNhanhRepository,
                        NhanVienRepository nhanVienRepository,
                        TaiKhoanRepository taiKhoanRepository,
                        KhachHangRepository khachHangRepository,
                        DanhMucRepository danhMucRepository,
                        MonAnRepository monAnRepository,
                        TonKhoRepository tonKhoRepository,
                        KhuyenMaiRepository khuyenMaiRepository,
                        DonHangRepository donHangRepository,
                        ChiTietDonHangRepository chiTietDonHangRepository,
                        ThanhToanRepository thanhToanRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.chiNhanhRepository = chiNhanhRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.danhMucRepository = danhMucRepository;
        this.monAnRepository = monAnRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    private static final Map<Integer, QueryMetaDTO> QUERY_METAS = new LinkedHashMap<>();

    static {
        registerMeta(1,
                "Query 1: Employees Without an Account",
                "Find all employees who have not been assigned a user account yet.",
                "Human Resources & Accounts",
                """
                SELECT nv.maNV, nv.hoTen, nv.soDienThoai, nv.chucVu, cn.tenCN
                FROM nhan_vien nv
                JOIN chi_nhanh cn ON nv.maCN = cn.maCN
                LEFT JOIN tai_khoan tk ON nv.maNV = tk.maNV
                WHERE tk.maTK IS NULL
                ORDER BY nv.maNV;
                """);

        registerMeta(2,
                "Query 2: Burgers Priced Between 40,000 and 60,000 VND",
                "Retrieve all burger items with price ranging from 40,000 to 60,000 VND, sorted descending by price.",
                "Food & Inventory",
                """
                SELECT m.maMon, m.tenMon, m.giaBan, d.tenDM, m.trangThai
                FROM mon_an m
                JOIN danh_muc d ON m.maDM = d.maDM
                WHERE d.tenDM = 'Burger' AND m.giaBan BETWEEN 40000 AND 60000
                ORDER BY m.giaBan DESC;
                """);

        registerMeta(3,
                "Query 3: Low Inventory (< 10) by Branch",
                "Identify food items with inventory stock less than 10 units at each branch.",
                "Food & Inventory",
                """
                SELECT tk.maTon, cn.tenCN, m.maMon, m.tenMon, tk.soLuongTon
                FROM ton_kho tk
                JOIN chi_nhanh cn ON tk.maCN = cn.maCN
                JOIN mon_an m ON tk.maMon = m.maMon
                WHERE tk.soLuongTon < 10
                ORDER BY cn.maCN, m.maMon;
                """);

        registerMeta(4,
                "Query 4: Customers in Ho Chi Minh City",
                "List all customers residing in Ho Chi Minh City (TP.HCM or TPHCM).",
                "Customer Analytics",
                """
                SELECT maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy
                FROM khach_hang
                WHERE diaChi LIKE '%TP.HCM%' OR diaChi LIKE '%TPHCM%'
                ORDER BY maKH;
                """);

        registerMeta(5,
                "Query 5: Payment Methods Successfully Used",
                "Find distinct payment methods that have been successfully processed at least once.",
                "Payments & Financials",
                """
                SELECT DISTINCT phuongThuc, 'Successfully Used' AS note
                FROM thanh_toan
                WHERE trangThai = 'DA_THANH_TOAN'
                ORDER BY phuongThuc;
                """);

        registerMeta(6,
                "Query 6: Completed Orders in February 2026",
                "Retrieve all orders completed during February 2026.",
                "Orders & Revenue",
                """
                SELECT maDH, ngayDat, tienHang, tienGiam, tongTien, trangThai
                FROM don_hang
                WHERE trangThai = 'DA_HOAN_THANH'
                  AND ngayDat >= '2026-02-01 00:00:00'
                  AND ngayDat <= '2026-02-28 23:59:59'
                ORDER BY maDH;
                """);

        registerMeta(7,
                "Query 7: Orders with Customer, Employee, and Branch Names",
                "List all orders with detailed names of customer (or Walk-in), handling employee, and branch.",
                "Orders & Revenue",
                """
                SELECT dh.maDH, dh.ngayDat,
                       COALESCE(kh.hoTen, '(Walk-in Customer)') AS customerName,
                       nv.hoTen AS employeeName,
                       cn.tenCN AS branchName,
                       dh.tongTien AS totalAmount
                FROM don_hang dh
                INNER JOIN nhan_vien nv ON dh.maNV = nv.maNV
                INNER JOIN chi_nhanh cn ON dh.maCN = cn.maCN
                LEFT JOIN khach_hang kh ON dh.maKH = kh.maKH
                ORDER BY dh.maDH;
                """);

        registerMeta(8,
                "Query 8: Order Details for Order 1001",
                "Show all line items, quantities, and prices for order #1001.",
                "Orders & Revenue",
                """
                SELECT ct.maCTDH, ct.maDH, m.tenMon, ct.soLuong, ct.donGia, ct.thanhTien
                FROM chi_tiet_don_hang ct
                JOIN mon_an m ON ct.maMon = m.maMon
                WHERE ct.maDH = 1001
                ORDER BY ct.maCTDH;
                """);

        registerMeta(9,
                "Query 9: Cross-check Line Items Sum vs Order Subtotal",
                "Verify accounting integrity by comparing sum of detail line items against order subtotal (tienHang).",
                "Orders & Revenue",
                """
                SELECT dh.maDH,
                       SUM(ct.thanhTien) AS calculatedSubtotal,
                       dh.tienHang AS orderSubtotal,
                       CASE WHEN SUM(ct.thanhTien) = dh.tienHang THEN 'MATCH' ELSE 'MISMATCH' END AS auditStatus,
                       dh.tienGiam AS discount,
                       dh.tongTien AS total
                FROM don_hang dh
                JOIN chi_tiet_don_hang ct ON dh.maDH = ct.maDH
                GROUP BY dh.maDH, dh.tienHang, dh.tienGiam, dh.tongTien
                ORDER BY dh.maDH;
                """);

        registerMeta(10,
                "Query 10: Total Revenue from Completed Orders",
                "Calculate total order count, subtotal, total discount, and net revenue for completed orders.",
                "Orders & Revenue",
                """
                SELECT COUNT(*) AS completedOrders,
                       SUM(tienHang) AS totalGoodsAmount,
                       SUM(tienGiam) AS totalDiscountAmount,
                       SUM(tongTien) AS netRevenue
                FROM don_hang
                WHERE trangThai = 'DA_HOAN_THANH';
                """);

        registerMeta(11,
                "Query 11: Monthly Revenue in 2026",
                "Aggregate completed order count and net revenue by calendar month in 2026.",
                "Orders & Revenue",
                """
                SELECT CONCAT('Month ', MONTH(ngayDat), '/', YEAR(ngayDat)) AS period,
                       COUNT(*) AS orderCount,
                       SUM(tongTien) AS revenue
                FROM don_hang
                WHERE trangThai = 'DA_HOAN_THANH'
                GROUP BY CONCAT('Month ', MONTH(ngayDat), '/', YEAR(ngayDat)), YEAR(ngayDat), MONTH(ngayDat)
                ORDER BY YEAR(ngayDat), MONTH(ngayDat);
                """);

        registerMeta(12,
                "Query 12: Top 3 Best-Selling Food Items",
                "Find the top 3 most popular items ranked by total quantity sold in completed orders.",
                "Food & Inventory",
                """
                SELECT m.maMon, m.tenMon, SUM(ct.soLuong) AS totalQuantitySold, m.giaBan
                FROM chi_tiet_don_hang ct
                JOIN don_hang dh ON ct.maDH = dh.maDH
                JOIN mon_an m ON ct.maMon = m.maMon
                WHERE dh.trangThai = 'DA_HOAN_THANH'
                GROUP BY m.maMon, m.tenMon, m.giaBan
                ORDER BY totalQuantitySold DESC
                LIMIT 3;
                """);

        registerMeta(13,
                "Query 13: Customer Spending Summary",
                "Calculate total completed orders and total spending for each registered customer.",
                "Customer Analytics",
                """
                SELECT kh.maKH, kh.hoTen,
                       COUNT(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.maDH END) AS completedOrders,
                       COALESCE(SUM(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.tongTien ELSE 0 END), 0) AS totalSpending
                FROM khach_hang kh
                LEFT JOIN don_hang dh ON kh.maKH = dh.maKH
                GROUP BY kh.maKH, kh.hoTen
                ORDER BY totalSpending DESC, kh.maKH;
                """);

        registerMeta(14,
                "Query 14: Customers Who Never Ordered",
                "Identify registered customers who have never placed any order.",
                "Customer Analytics",
                """
                SELECT kh.maKH, kh.hoTen, kh.soDienThoai, kh.email, kh.diaChi, kh.ngayDangKy
                FROM khach_hang kh
                LEFT JOIN don_hang dh ON kh.maKH = dh.maKH
                WHERE dh.maDH IS NULL
                ORDER BY kh.maKH;
                """);

        registerMeta(15,
                "Query 15: Food Items Never Sold Successfully",
                "Find menu items that have never appeared in any completed order.",
                "Food & Inventory",
                """
                SELECT m.maMon, m.tenMon, m.giaBan, m.trangThai, m.maDM
                FROM mon_an m
                WHERE m.maMon NOT IN (
                    SELECT DISTINCT ct.maMon
                    FROM chi_tiet_don_hang ct
                    JOIN don_hang dh ON ct.maDH = dh.maDH
                    WHERE dh.trangThai = 'DA_HOAN_THANH'
                )
                ORDER BY m.maMon;
                """);

        registerMeta(16,
                "Query 16: Sales Performance by Employee",
                "Show completed order count and total revenue generated per employee.",
                "Human Resources & Accounts",
                """
                SELECT nv.maNV, nv.hoTen, nv.chucVu,
                       COUNT(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.maDH END) AS completedOrders,
                       COALESCE(SUM(CASE WHEN dh.trangThai = 'DA_HOAN_THANH' THEN dh.tongTien ELSE 0 END), 0) AS totalRevenue
                FROM nhan_vien nv
                LEFT JOIN don_hang dh ON nv.maNV = dh.maNV
                GROUP BY nv.maNV, nv.hoTen, nv.chucVu
                ORDER BY totalRevenue DESC, nv.maNV;
                """);

        registerMeta(17,
                "Query 17: Customers with At Least 2 Completed Orders",
                "List repeat customers who have completed 2 or more orders.",
                "Customer Analytics",
                """
                SELECT kh.maKH, kh.hoTen,
                       COUNT(dh.maDH) AS completedOrders,
                       SUM(dh.tongTien) AS totalSpending
                FROM khach_hang kh
                JOIN don_hang dh ON kh.maKH = dh.maKH
                WHERE dh.trangThai = 'DA_HOAN_THANH'
                GROUP BY kh.maKH, kh.hoTen
                HAVING COUNT(dh.maDH) >= 2
                ORDER BY completedOrders DESC, totalSpending DESC;
                """);

        registerMeta(18,
                "Query 18: Order Details Where Historical Unit Price != Current Price",
                "Identify order line items where historical unit price differs from the current menu price.",
                "Food & Inventory",
                """
                SELECT ct.maCTDH, ct.maDH, m.maMon, m.tenMon, ct.donGia, m.giaBan, (m.giaBan - ct.donGia) AS priceDifference
                FROM chi_tiet_don_hang ct
                JOIN mon_an m ON ct.maMon = m.maMon
                WHERE ct.donGia <> m.giaBan
                ORDER BY ct.maDH, ct.maCTDH;
                """);

        registerMeta(19,
                "Query 19: Locked Accounts",
                "List all user accounts that are currently in LOCKED status.",
                "Human Resources & Accounts",
                """
                SELECT tk.maTK, tk.tenDangNhap, nv.hoTen, nv.chucVu, cn.tenCN, tk.trangThai
                FROM tai_khoan tk
                JOIN nhan_vien nv ON tk.maNV = nv.maNV
                JOIN chi_nhanh cn ON nv.maCN = cn.maCN
                WHERE tk.trangThai = 'LOCKED'
                ORDER BY tk.maTK;
                """);

        registerMeta(20,
                "Query 20: Orders Above Average Order Value",
                "Find all orders whose total amount is greater than the overall average order total.",
                "Orders & Revenue",
                """
                SELECT maDH, ngayDat, tongTien, trangThai, maCN
                FROM don_hang
                WHERE tongTien > (SELECT AVG(tongTien) FROM don_hang)
                ORDER BY tongTien DESC;
                """);

        registerMeta(21,
                "Query 21: Accounts That Never Logged In",
                "Find accounts where last login timestamp is NULL.",
                "Human Resources & Accounts",
                """
                SELECT tk.maTK, tk.tenDangNhap, nv.hoTen, nv.chucVu, cn.tenCN, tk.ngayTao
                FROM tai_khoan tk
                JOIN nhan_vien nv ON tk.maNV = nv.maNV
                JOIN chi_nhanh cn ON nv.maCN = cn.maCN
                WHERE tk.lanDangNhapCuoi IS NULL
                ORDER BY tk.maTK;
                """);

        registerMeta(22,
                "Query 22: Orders Applying Promotions",
                "List all orders that utilized a promotion discount.",
                "Promotions & Discounts",
                """
                SELECT dh.maDH, km.tenKM, dh.tienHang, dh.tienGiam, dh.tongTien, dh.trangThai
                FROM don_hang dh
                JOIN khuyen_mai km ON dh.maKM = km.maKM
                ORDER BY dh.maDH;
                """);

        registerMeta(23,
                "Query 23: Total Discount Amount per Promotion",
                "Calculate total orders and total monetary discount granted by each promotion.",
                "Promotions & Discounts",
                """
                SELECT km.maKM, km.tenKM,
                       COUNT(dh.maDH) AS ordersCount,
                       COALESCE(SUM(dh.tienGiam), 0) AS totalDiscountGiven
                FROM khuyen_mai km
                LEFT JOIN don_hang dh ON km.maKM = dh.maKM
                GROUP BY km.maKM, km.tenKM
                ORDER BY totalDiscountGiven DESC, km.maKM;
                """);

        registerMeta(24,
                "Query 24: Unused Promotions",
                "Identify promotion campaigns that have never been applied to any order.",
                "Promotions & Discounts",
                """
                SELECT km.maKM, km.tenKM, CONCAT(km.phanTramGiam, '%') AS discountPercent, km.donToiThieu, km.trangThai
                FROM khuyen_mai km
                LEFT JOIN don_hang dh ON km.maKM = dh.maKM
                WHERE dh.maDH IS NULL
                ORDER BY km.maKM;
                """);

        registerMeta(25,
                "Query 25: Comparison Between Orders With vs Without Promotion",
                "Compare completed order metrics between orders that used a promo vs those without.",
                "Promotions & Discounts",
                """
                SELECT CASE WHEN maKM IS NOT NULL THEN 'With Promotion' ELSE 'Without Promotion' END AS orderType,
                       COUNT(*) AS orderCount,
                       SUM(tienHang) AS subtotal,
                       SUM(tienGiam) AS totalDiscount,
                       SUM(tongTien) AS revenue
                FROM don_hang
                WHERE trangThai = 'DA_HOAN_THANH'
                GROUP BY CASE WHEN maKM IS NOT NULL THEN 'With Promotion' ELSE 'Without Promotion' END
                ORDER BY revenue DESC;
                """);

        registerMeta(26,
                "Query 26: Orders Capped by Maximum Discount",
                "Find orders where the percentage discount would have exceeded the max discount cap.",
                "Promotions & Discounts",
                """
                SELECT dh.maDH, km.maKM, dh.tienHang,
                       CONCAT(km.phanTramGiam, '%') AS promoRate,
                       (dh.tienHang * km.phanTramGiam / 100) AS theoreticalDiscount,
                       km.giamToiDa AS maxCap,
                       dh.tienGiam AS actualDiscount
                FROM don_hang dh
                JOIN khuyen_mai km ON dh.maKM = km.maKM
                WHERE km.giamToiDa IS NOT NULL
                  AND dh.tienGiam = km.giamToiDa
                  AND (dh.tienHang * km.phanTramGiam / 100) > km.giamToiDa
                ORDER BY dh.maDH;
                """);

        registerMeta(27,
                "Query 27: Revenue by Payment Method",
                "Aggregate transaction count and total amount collected by each payment method.",
                "Payments & Financials",
                """
                SELECT phuongThuc,
                       COUNT(*) AS transactionCount,
                       SUM(soTien) AS totalCollected
                FROM thanh_toan
                WHERE trangThai = 'DA_THANH_TOAN'
                GROUP BY phuongThuc
                ORDER BY totalCollected DESC;
                """);

        registerMeta(28,
                "Query 28: Revenue by Branch",
                "Show completed orders and net revenue per branch.",
                "Orders & Revenue",
                """
                SELECT cn.maCN, cn.tenCN,
                       COUNT(dh.maDH) AS completedOrders,
                       SUM(dh.tongTien) AS revenue
                FROM chi_nhanh cn
                JOIN don_hang dh ON cn.maCN = dh.maCN
                WHERE dh.trangThai = 'DA_HOAN_THANH'
                GROUP BY cn.maCN, cn.tenCN
                ORDER BY revenue DESC;
                """);

        registerMeta(29,
                "Query 29: Customers with Spending Above Average",
                "Identify customers whose total spending exceeds the average spending across all purchasing customers.",
                "Customer Analytics",
                """
                SELECT kh.maKH, kh.hoTen, SUM(dh.tongTien) AS totalSpending
                FROM khach_hang kh
                JOIN don_hang dh ON kh.maKH = dh.maKH
                WHERE dh.trangThai = 'DA_HOAN_THANH'
                GROUP BY kh.maKH, kh.hoTen
                HAVING SUM(dh.tongTien) > (
                    SELECT AVG(t.chiTieuKhach)
                    FROM (
                        SELECT SUM(dh2.tongTien) AS chiTieuKhach
                        FROM don_hang dh2
                        WHERE dh2.trangThai = 'DA_HOAN_THANH' AND dh2.maKH IS NOT NULL
                        GROUP BY dh2.maKH
                    ) t
                )
                ORDER BY totalSpending DESC;
                """);

        registerMeta(30,
                "Query 30: Orders with Failed Payment Followed by Successful Payment",
                "Identify orders that experienced a failed payment attempt and subsequently completed successfully.",
                "Payments & Financials",
                """
                SELECT DISTINCT dh.maDH, dh.ngayDat, dh.tongTien, dh.trangThai, 'Had THAT_BAI followed by DA_THANH_TOAN' AS auditNote
                FROM don_hang dh
                JOIN thanh_toan t1 ON dh.maDH = t1.maDH AND t1.trangThai = 'THAT_BAI'
                JOIN thanh_toan t2 ON dh.maDH = t2.maDH AND t2.trangThai = 'DA_THANH_TOAN'
                ORDER BY dh.maDH;
                """);
    }

    private static void registerMeta(int id, String title, String description, String category, String sql) {
        QUERY_METAS.put(id, QueryMetaDTO.builder()
                .id(id)
                .title(title)
                .description(description)
                .category(category)
                .sql(sql.trim())
                .build());
    }

    public List<QueryMetaDTO> getAllQueryMetas() {
        return new ArrayList<>(QUERY_METAS.values());
    }

    public QueryMetaDTO getQueryMeta(int id) {
        QueryMetaDTO meta = QUERY_METAS.get(id);
        if (meta == null) {
            throw new BadRequestException("Query ID not recognized (1-30): " + id);
        }
        return meta;
    }

    public QueryResultDTO executeQuery(int id, String mode) {
        QueryMetaDTO meta = getQueryMeta(id);
        if ("lambda".equalsIgnoreCase(mode) || "stream".equalsIgnoreCase(mode)) {
            return executeStreamQuery(id, meta);
        } else {
            return executeSqlQuery(id, meta);
        }
    }

    public QueryResultDTO executeSqlQuery(int id, QueryMetaDTO meta) {
        long start = System.currentTimeMillis();
        List<String> headers = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        jdbcTemplate.query(meta.getSql(), rs -> {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            if (headers.isEmpty()) {
                for (int i = 1; i <= colCount; i++) {
                    headers.add(rsmd.getColumnLabel(i));
                }
            }
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= colCount; i++) {
                Object val = rs.getObject(i);
                row.add(val != null ? val : "NULL");
            }
            rows.add(row);
        });

        long elapsed = System.currentTimeMillis() - start;

        return QueryResultDTO.builder()
                .queryId(id)
                .title(meta.getTitle())
                .description(meta.getDescription())
                .sql(meta.getSql())
                .executionMode("SQL")
                .headers(headers)
                .rows(rows)
                .totalRows(rows.size())
                .executionTimeMs(elapsed)
                .build();
    }

    public QueryResultDTO executeStreamQuery(int id, QueryMetaDTO meta) {
        long start = System.currentTimeMillis();
        List<String> headers = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        switch (id) {
            case 1 -> {
                headers.addAll(List.of("maNV", "hoTen", "soDienThoai", "chucVu", "tenCN"));
                Set<String> hasAccount = taiKhoanRepository.findAll().stream()
                        .map(TaiKhoan::getMaNV)
                        .collect(Collectors.toSet());
                Map<String, String> branchNames = chiNhanhRepository.findAll().stream()
                        .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));

                nhanVienRepository.findAll().stream()
                        .filter(nv -> !hasAccount.contains(nv.getMaNV()))
                        .sorted(Comparator.comparing(NhanVien::getMaNV))
                        .forEach(nv -> rows.add(List.of(
                                nv.getMaNV(), nv.getHoTen(), nv.getSoDienThoai(),
                                nv.getChucVu().name(), branchNames.getOrDefault(nv.getMaCN(), nv.getMaCN())
                        )));
            }
            case 2 -> {
                headers.addAll(List.of("maMon", "tenMon", "giaBan", "tenDM", "trangThai"));
                String burgerDmId = danhMucRepository.findByTenDM("Burger")
                        .map(DanhMuc::getMaDM).orElse("DM01");

                monAnRepository.findAll().stream()
                        .filter(m -> burgerDmId.equals(m.getMaDM()))
                        .filter(m -> m.getGiaBan().compareTo(BigDecimal.valueOf(40000)) >= 0
                                  && m.getGiaBan().compareTo(BigDecimal.valueOf(60000)) <= 0)
                        .sorted(Comparator.comparing(MonAn::getGiaBan).reversed())
                        .forEach(m -> rows.add(List.of(
                                m.getMaMon(), m.getTenMon(), m.getGiaBan(), "Burger", m.getTrangThai().name()
                        )));
            }
            case 3 -> {
                headers.addAll(List.of("maTon", "tenCN", "maMon", "tenMon", "soLuongTon"));
                Map<String, String> branchNames = chiNhanhRepository.findAll().stream()
                        .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));
                Map<String, String> foodNames = monAnRepository.findAll().stream()
                        .collect(Collectors.toMap(MonAn::getMaMon, MonAn::getTenMon));

                tonKhoRepository.findAll().stream()
                        .filter(tk -> tk.getSoLuongTon() != null && tk.getSoLuongTon() < 10)
                        .sorted(Comparator.comparing(TonKho::getMaCN).thenComparing(TonKho::getMaMon))
                        .forEach(tk -> rows.add(List.of(
                                tk.getMaTon(),
                                branchNames.getOrDefault(tk.getMaCN(), tk.getMaCN()),
                                tk.getMaMon(),
                                foodNames.getOrDefault(tk.getMaMon(), tk.getMaMon()),
                                tk.getSoLuongTon()
                        )));
            }
            case 4 -> {
                headers.addAll(List.of("maKH", "hoTen", "soDienThoai", "email", "diaChi", "ngayDangKy"));
                khachHangRepository.findAll().stream()
                        .filter(kh -> kh.getDiaChi() != null &&
                                (kh.getDiaChi().contains("TP.HCM") || kh.getDiaChi().contains("TPHCM")))
                        .sorted(Comparator.comparing(KhachHang::getMaKH))
                        .forEach(kh -> rows.add(List.of(
                                kh.getMaKH(), kh.getHoTen(), kh.getSoDienThoai(),
                                kh.getEmail() != null ? kh.getEmail() : "NULL",
                                kh.getDiaChi(), kh.getNgayDangKy().toString()
                        )));
            }
            case 5 -> {
                headers.addAll(List.of("phuongThuc", "ghiChu"));
                thanhToanRepository.findAll().stream()
                        .filter(tt -> tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                        .map(ThanhToan::getPhuongThuc)
                        .distinct()
                        .sorted(Comparator.comparing(PhuongThucThanhToan::name))
                        .forEach(pt -> rows.add(List.of(pt.name(), "Da tung thanh cong")));
            }
            case 6 -> {
                headers.addAll(List.of("maDH", "ngayDat", "tienHang", "tienGiam", "tongTien", "trangThai"));
                donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .filter(dh -> dh.getNgayDat().getYear() == 2026 && dh.getNgayDat().getMonthValue() == 2)
                        .sorted(Comparator.comparing(DonHang::getMaDH))
                        .forEach(dh -> rows.add(List.of(
                                dh.getMaDH(), dh.getNgayDat().toString(), dh.getTienHang(),
                                dh.getTienGiam(), dh.getTongTien(), dh.getTrangThai().name()
                        )));
            }
            case 7 -> {
                headers.addAll(List.of("maDH", "ngayDat", "tenKhach", "tenNhanVien", "tenCN", "tongTien"));
                Map<String, String> custNames = khachHangRepository.findAll().stream()
                        .collect(Collectors.toMap(KhachHang::getMaKH, KhachHang::getHoTen));
                Map<String, String> empNames = nhanVienRepository.findAll().stream()
                        .collect(Collectors.toMap(NhanVien::getMaNV, NhanVien::getHoTen));
                Map<String, String> branchNames = chiNhanhRepository.findAll().stream()
                        .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));

                donHangRepository.findAll().stream()
                        .sorted(Comparator.comparing(DonHang::getMaDH))
                        .forEach(dh -> rows.add(List.of(
                                dh.getMaDH(),
                                dh.getNgayDat().toString(),
                                dh.getMaKH() != null ? custNames.getOrDefault(dh.getMaKH(), "(Khach vang lai)") : "(Khach vang lai)",
                                empNames.getOrDefault(dh.getMaNV(), dh.getMaNV()),
                                branchNames.getOrDefault(dh.getMaCN(), dh.getMaCN()),
                                dh.getTongTien()
                        )));
            }
            case 8 -> {
                headers.addAll(List.of("maCTDH", "maDH", "tenMon", "soLuong", "donGia", "thanhTien"));
                Map<String, String> foodNames = monAnRepository.findAll().stream()
                        .collect(Collectors.toMap(MonAn::getMaMon, MonAn::getTenMon));

                chiTietDonHangRepository.findAll().stream()
                        .filter(ct -> ct.getMaDH().equals(1001L))
                        .sorted(Comparator.comparing(ChiTietDonHang::getMaCTDH))
                        .forEach(ct -> rows.add(List.of(
                                ct.getMaCTDH(), ct.getMaDH(), foodNames.getOrDefault(ct.getMaMon(), ct.getMaMon()),
                                ct.getSoLuong(), ct.getDonGia(), ct.getThanhTien()
                        )));
            }
            case 9 -> {
                headers.addAll(List.of("maDH", "tinhTienHang", "tienHang", "doiChieu", "tienGiam", "tongTien"));
                Map<Long, BigDecimal> sumMap = chiTietDonHangRepository.findAll().stream()
                        .collect(Collectors.groupingBy(ChiTietDonHang::getMaDH,
                                Collectors.reducing(BigDecimal.ZERO, ChiTietDonHang::getThanhTien, BigDecimal::add)));

                donHangRepository.findAll().stream()
                        .sorted(Comparator.comparing(DonHang::getMaDH))
                        .forEach(dh -> {
                            BigDecimal calculated = sumMap.getOrDefault(dh.getMaDH(), BigDecimal.ZERO);
                            boolean matches = dh.getTienHang().compareTo(calculated) == 0;
                            rows.add(List.of(
                                    dh.getMaDH(), calculated, dh.getTienHang(),
                                    matches ? "KHOP" : "LECH", dh.getTienGiam(), dh.getTongTien()
                            ));
                        });
            }
            case 10 -> {
                headers.addAll(List.of("soDon", "tongTienHang", "tongTienGiam", "tongDoanhThu"));
                List<DonHang> completed = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .toList();
                long soDon = completed.size();
                BigDecimal totalGoods = completed.stream().map(DonHang::getTienHang).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalDiscount = completed.stream().map(DonHang::getTienGiam).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalRevenue = completed.stream().map(DonHang::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);

                rows.add(List.of(soDon, totalGoods, totalDiscount, totalRevenue));
            }
            case 11 -> {
                headers.addAll(List.of("thoiGian", "soDon", "doanhThu"));
                Map<Integer, List<DonHang>> byMonth = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH && dh.getNgayDat().getYear() == 2026)
                        .collect(Collectors.groupingBy(dh -> dh.getNgayDat().getMonthValue(), TreeMap::new, Collectors.toList()));

                byMonth.forEach((m, list) -> {
                    BigDecimal rev = list.stream().map(DonHang::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);
                    rows.add(List.of("Thang " + m + "/2026", list.size(), rev));
                });
            }
            case 12 -> {
                headers.addAll(List.of("maMon", "tenMon", "tongSL", "giaBan"));
                Set<Long> doneOrderIds = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .map(DonHang::getMaDH)
                        .collect(Collectors.toSet());

                Map<String, Integer> foodQty = chiTietDonHangRepository.findAll().stream()
                        .filter(ct -> doneOrderIds.contains(ct.getMaDH()))
                        .collect(Collectors.groupingBy(ChiTietDonHang::getMaMon, Collectors.summingInt(ChiTietDonHang::getSoLuong)));

                Map<String, MonAn> foodMap = monAnRepository.findAll().stream()
                        .collect(Collectors.toMap(MonAn::getMaMon, m -> m));

                foodQty.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .limit(3)
                        .forEach(e -> {
                            MonAn m = foodMap.get(e.getKey());
                            rows.add(List.of(e.getKey(), m != null ? m.getTenMon() : e.getKey(), e.getValue(), m != null ? m.getGiaBan() : 0));
                        });
            }
            case 13 -> {
                headers.addAll(List.of("maKH", "hoTen", "soDon", "tongChiTieu"));
                Map<String, List<DonHang>> ordersByCust = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getMaKH() != null)
                        .collect(Collectors.groupingBy(DonHang::getMaKH));

                khachHangRepository.findAll().stream()
                        .map(kh -> {
                            List<DonHang> custOrders = ordersByCust.getOrDefault(kh.getMaKH(), Collections.emptyList()).stream()
                                    .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                                    .toList();
                            BigDecimal spend = custOrders.stream().map(DonHang::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);
                            return List.of((Object) kh.getMaKH(), kh.getHoTen(), custOrders.size(), spend);
                        })
                        .sorted((a, b) -> ((BigDecimal) b.get(3)).compareTo((BigDecimal) a.get(3)))
                        .forEach(rows::add);
            }
            case 14 -> {
                headers.addAll(List.of("maKH", "hoTen", "soDienThoai", "email", "diaChi", "ngayDangKy"));
                Set<String> buyingCustIds = donHangRepository.findAll().stream()
                        .map(DonHang::getMaKH)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                khachHangRepository.findAll().stream()
                        .filter(kh -> !buyingCustIds.contains(kh.getMaKH()))
                        .sorted(Comparator.comparing(KhachHang::getMaKH))
                        .forEach(kh -> rows.add(List.of(
                                kh.getMaKH(), kh.getHoTen(), kh.getSoDienThoai(),
                                kh.getEmail() != null ? kh.getEmail() : "NULL",
                                kh.getDiaChi(), kh.getNgayDangKy().toString()
                        )));
            }
            case 15 -> {
                headers.addAll(List.of("maMon", "tenMon", "giaBan", "trangThai", "maDM"));
                Set<Long> doneOrders = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .map(DonHang::getMaDH)
                        .collect(Collectors.toSet());

                Set<String> soldFoodIds = chiTietDonHangRepository.findAll().stream()
                        .filter(ct -> doneOrders.contains(ct.getMaDH()))
                        .map(ChiTietDonHang::getMaMon)
                        .collect(Collectors.toSet());

                monAnRepository.findAll().stream()
                        .filter(m -> !soldFoodIds.contains(m.getMaMon()))
                        .sorted(Comparator.comparing(MonAn::getMaMon))
                        .forEach(m -> rows.add(List.of(
                                m.getMaMon(), m.getTenMon(), m.getGiaBan(), m.getTrangThai().name(), m.getMaDM()
                        )));
            }
            case 16 -> {
                headers.addAll(List.of("maNV", "hoTen", "chucVu", "soDon", "tongDoanhThu"));
                Map<String, List<DonHang>> empOrders = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .collect(Collectors.groupingBy(DonHang::getMaNV));

                nhanVienRepository.findAll().stream()
                        .map(nv -> {
                            List<DonHang> orders = empOrders.getOrDefault(nv.getMaNV(), Collections.emptyList());
                            BigDecimal rev = orders.stream().map(DonHang::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);
                            return List.of((Object) nv.getMaNV(), nv.getHoTen(), nv.getChucVu().name(), orders.size(), rev);
                        })
                        .sorted((a, b) -> ((BigDecimal) b.get(4)).compareTo((BigDecimal) a.get(4)))
                        .forEach(rows::add);
            }
            case 17 -> {
                headers.addAll(List.of("maKH", "hoTen", "soDon", "tongChiTieu"));
                Map<String, String> custNames = khachHangRepository.findAll().stream()
                        .collect(Collectors.toMap(KhachHang::getMaKH, KhachHang::getHoTen));

                donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH && dh.getMaKH() != null)
                        .collect(Collectors.groupingBy(DonHang::getMaKH))
                        .entrySet().stream()
                        .filter(e -> e.getValue().size() >= 2)
                        .map(e -> {
                            BigDecimal spend = e.getValue().stream().map(DonHang::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);
                            return List.of((Object) e.getKey(), custNames.getOrDefault(e.getKey(), e.getKey()), e.getValue().size(), spend);
                        })
                        .sorted((a, b) -> {
                            int cmp = Integer.compare((Integer) b.get(2), (Integer) a.get(2));
                            if (cmp != 0) return cmp;
                            return ((BigDecimal) b.get(3)).compareTo((BigDecimal) a.get(3));
                        })
                        .forEach(rows::add);
            }
            case 18 -> {
                headers.addAll(List.of("maCTDH", "maDH", "maMon", "tenMon", "donGia", "giaBan", "chenhLech"));
                Map<String, MonAn> foodMap = monAnRepository.findAll().stream()
                        .collect(Collectors.toMap(MonAn::getMaMon, m -> m));

                chiTietDonHangRepository.findAll().stream()
                        .filter(ct -> {
                            MonAn m = foodMap.get(ct.getMaMon());
                            return m != null && ct.getDonGia().compareTo(m.getGiaBan()) != 0;
                        })
                        .sorted(Comparator.comparing(ChiTietDonHang::getMaDH).thenComparing(ChiTietDonHang::getMaCTDH))
                        .forEach(ct -> {
                            MonAn m = foodMap.get(ct.getMaMon());
                            BigDecimal diff = m.getGiaBan().subtract(ct.getDonGia());
                            rows.add(List.of(
                                    ct.getMaCTDH(), ct.getMaDH(), m.getMaMon(), m.getTenMon(),
                                    ct.getDonGia(), m.getGiaBan(), diff
                            ));
                        });
            }
            case 19 -> {
                headers.addAll(List.of("maTK", "tenDangNhap", "hoTen", "chucVu", "tenCN", "trangThai"));
                Map<String, NhanVien> empMap = nhanVienRepository.findAll().stream()
                        .collect(Collectors.toMap(NhanVien::getMaNV, nv -> nv));
                Map<String, String> branchMap = chiNhanhRepository.findAll().stream()
                        .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));

                taiKhoanRepository.findAll().stream()
                        .filter(tk -> tk.getTrangThai() == TrangThaiTaiKhoan.LOCKED)
                        .sorted(Comparator.comparing(TaiKhoan::getMaTK))
                        .forEach(tk -> {
                            NhanVien nv = empMap.get(tk.getMaNV());
                            rows.add(List.of(
                                    tk.getMaTK(), tk.getTenDangNhap(),
                                    nv != null ? nv.getHoTen() : "NULL",
                                    nv != null ? nv.getChucVu().name() : "NULL",
                                    nv != null ? branchMap.getOrDefault(nv.getMaCN(), nv.getMaCN()) : "NULL",
                                    tk.getTrangThai().name()
                            ));
                        });
            }
            case 20 -> {
                headers.addAll(List.of("maDH", "ngayDat", "tongTien", "trangThai", "maCN"));
                double avg = donHangRepository.findAll().stream()
                        .mapToDouble(dh -> dh.getTongTien().doubleValue())
                        .average().orElse(0.0);

                donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTongTien().doubleValue() > avg)
                        .sorted(Comparator.comparing(DonHang::getTongTien).reversed())
                        .forEach(dh -> rows.add(List.of(
                                dh.getMaDH(), dh.getNgayDat().toString(), dh.getTongTien(),
                                dh.getTrangThai().name(), dh.getMaCN()
                        )));
            }
            case 21 -> {
                headers.addAll(List.of("maTK", "tenDangNhap", "hoTen", "chucVu", "tenCN", "ngayTao"));
                Map<String, NhanVien> empMap = nhanVienRepository.findAll().stream()
                        .collect(Collectors.toMap(NhanVien::getMaNV, nv -> nv));
                Map<String, String> branchMap = chiNhanhRepository.findAll().stream()
                        .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));

                taiKhoanRepository.findAll().stream()
                        .filter(tk -> tk.getLanDangNhapCuoi() == null)
                        .sorted(Comparator.comparing(TaiKhoan::getMaTK))
                        .forEach(tk -> {
                            NhanVien nv = empMap.get(tk.getMaNV());
                            rows.add(List.of(
                                    tk.getMaTK(), tk.getTenDangNhap(),
                                    nv != null ? nv.getHoTen() : "NULL",
                                    nv != null ? nv.getChucVu().name() : "NULL",
                                    nv != null ? branchMap.getOrDefault(nv.getMaCN(), nv.getMaCN()) : "NULL",
                                    tk.getNgayTao().toString()
                            ));
                        });
            }
            case 22 -> {
                headers.addAll(List.of("maDH", "tenKM", "tienHang", "tienGiam", "tongTien", "trangThai"));
                Map<String, String> promoNames = khuyenMaiRepository.findAll().stream()
                        .collect(Collectors.toMap(KhuyenMai::getMaKM, KhuyenMai::getTenKM));

                donHangRepository.findAll().stream()
                        .filter(dh -> dh.getMaKM() != null && !dh.getMaKM().isBlank())
                        .sorted(Comparator.comparing(DonHang::getMaDH))
                        .forEach(dh -> rows.add(List.of(
                                dh.getMaDH(), promoNames.getOrDefault(dh.getMaKM(), dh.getMaKM()),
                                dh.getTienHang(), dh.getTienGiam(), dh.getTongTien(), dh.getTrangThai().name()
                        )));
            }
            case 23 -> {
                headers.addAll(List.of("maKM", "tenKM", "soDon", "tongGiam"));
                Map<String, List<DonHang>> ordersByPromo = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getMaKM() != null)
                        .collect(Collectors.groupingBy(DonHang::getMaKM));

                khuyenMaiRepository.findAll().stream()
                        .map(km -> {
                            List<DonHang> orders = ordersByPromo.getOrDefault(km.getMaKM(), Collections.emptyList());
                            BigDecimal totalGiam = orders.stream().map(DonHang::getTienGiam).reduce(BigDecimal.ZERO, BigDecimal::add);
                            return List.of((Object) km.getMaKM(), km.getTenKM(), orders.size(), totalGiam);
                        })
                        .sorted((a, b) -> ((BigDecimal) b.get(3)).compareTo((BigDecimal) a.get(3)))
                        .forEach(rows::add);
            }
            case 24 -> {
                headers.addAll(List.of("maKM", "tenKM", "phanTramGiam", "donToiThieu", "trangThai"));
                Set<String> usedPromoIds = donHangRepository.findAll().stream()
                        .map(DonHang::getMaKM)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                khuyenMaiRepository.findAll().stream()
                        .filter(km -> !usedPromoIds.contains(km.getMaKM()))
                        .sorted(Comparator.comparing(KhuyenMai::getMaKM))
                        .forEach(km -> rows.add(List.of(
                                km.getMaKM(), km.getTenKM(), km.getPhanTramGiam() + "%",
                                km.getDonToiThieu(), km.getTrangThai().name()
                        )));
            }
            case 25 -> {
                headers.addAll(List.of("loaiDon", "soDon", "tongTienHang", "tongTienGiam", "doanhThu"));
                Map<Boolean, List<DonHang>> partitioned = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .collect(Collectors.partitioningBy(dh -> dh.getMaKM() != null && !dh.getMaKM().isBlank()));

                List<List<Object>> groupRows = new ArrayList<>();
                for (boolean withPromo : List.of(true, false)) {
                    List<DonHang> list = partitioned.getOrDefault(withPromo, Collections.emptyList());
                    String label = withPromo ? "Co dung Khuyen mai" : "Khong dung Khuyen mai";
                    long cnt = list.size();
                    BigDecimal th = list.stream().map(DonHang::getTienHang).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal tg = list.stream().map(DonHang::getTienGiam).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal tt = list.stream().map(DonHang::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);
                    groupRows.add(List.of(label, cnt, th, tg, tt));
                }
                groupRows.sort((a, b) -> ((BigDecimal) b.get(4)).compareTo((BigDecimal) a.get(4)));
                rows.addAll(groupRows);
            }
            case 26 -> {
                headers.addAll(List.of("maDH", "maKM", "tienHang", "phanTramGiam", "giamTheoPT", "giamToiDa", "tienGiam"));
                Map<String, KhuyenMai> promoMap = khuyenMaiRepository.findAll().stream()
                        .collect(Collectors.toMap(KhuyenMai::getMaKM, km -> km));

                donHangRepository.findAll().stream()
                        .filter(dh -> dh.getMaKM() != null)
                        .filter(dh -> {
                            KhuyenMai km = promoMap.get(dh.getMaKM());
                            if (km == null || km.getGiamToiDa() == null) return false;
                            BigDecimal byPercent = dh.getTienHang().multiply(BigDecimal.valueOf(km.getPhanTramGiam()))
                                    .divide(BigDecimal.valueOf(100));
                            return dh.getTienGiam().compareTo(km.getGiamToiDa()) == 0
                                && byPercent.compareTo(km.getGiamToiDa()) > 0;
                        })
                        .sorted(Comparator.comparing(DonHang::getMaDH))
                        .forEach(dh -> {
                            KhuyenMai km = promoMap.get(dh.getMaKM());
                            BigDecimal byPercent = dh.getTienHang().multiply(BigDecimal.valueOf(km.getPhanTramGiam()))
                                    .divide(BigDecimal.valueOf(100));
                            rows.add(List.of(
                                    dh.getMaDH(), km.getMaKM(), dh.getTienHang(),
                                    km.getPhanTramGiam() + "%", byPercent, km.getGiamToiDa(), dh.getTienGiam()
                            ));
                        });
            }
            case 27 -> {
                headers.addAll(List.of("phuongThuc", "soGiaoDich", "tongTienThu"));
                thanhToanRepository.findAll().stream()
                        .filter(tt -> tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                        .collect(Collectors.groupingBy(ThanhToan::getPhuongThuc))
                        .entrySet().stream()
                        .map(e -> {
                            BigDecimal total = e.getValue().stream().map(ThanhToan::getSoTien).reduce(BigDecimal.ZERO, BigDecimal::add);
                            return List.of((Object) e.getKey().name(), e.getValue().size(), total);
                        })
                        .sorted((a, b) -> ((BigDecimal) b.get(2)).compareTo((BigDecimal) a.get(2)))
                        .forEach(rows::add);
            }
            case 28 -> {
                headers.addAll(List.of("maCN", "tenCN", "soDon", "doanhThu"));
                Map<String, String> branchNames = chiNhanhRepository.findAll().stream()
                        .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));

                donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                        .collect(Collectors.groupingBy(DonHang::getMaCN))
                        .entrySet().stream()
                        .map(e -> {
                            BigDecimal rev = e.getValue().stream().map(DonHang::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);
                            return List.of((Object) e.getKey(), branchNames.getOrDefault(e.getKey(), e.getKey()), e.getValue().size(), rev);
                        })
                        .sorted((a, b) -> ((BigDecimal) b.get(3)).compareTo((BigDecimal) a.get(3)))
                        .forEach(rows::add);
            }
            case 29 -> {
                headers.addAll(List.of("maKH", "hoTen", "tongChiTieu"));
                Map<String, String> custNames = khachHangRepository.findAll().stream()
                        .collect(Collectors.toMap(KhachHang::getMaKH, KhachHang::getHoTen));

                Map<String, BigDecimal> custSpend = donHangRepository.findAll().stream()
                        .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH && dh.getMaKH() != null)
                        .collect(Collectors.groupingBy(DonHang::getMaKH,
                                Collectors.reducing(BigDecimal.ZERO, DonHang::getTongTien, BigDecimal::add)));

                double avgSpend = custSpend.values().stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0.0);

                custSpend.entrySet().stream()
                        .filter(e -> e.getValue().doubleValue() > avgSpend)
                        .map(e -> List.of((Object) e.getKey(), custNames.getOrDefault(e.getKey(), e.getKey()), e.getValue()))
                        .sorted((a, b) -> ((BigDecimal) b.get(2)).compareTo((BigDecimal) a.get(2)))
                        .forEach(rows::add);
            }
            case 30 -> {
                headers.addAll(List.of("maDH", "ngayDat", "tongTien", "trangThai", "ghiChu"));
                Map<Long, List<ThanhToan>> paymentsByOrder = thanhToanRepository.findAll().stream()
                        .collect(Collectors.groupingBy(ThanhToan::getMaDH));

                donHangRepository.findAll().stream()
                        .filter(dh -> {
                            List<ThanhToan> pts = paymentsByOrder.getOrDefault(dh.getMaDH(), Collections.emptyList());
                            boolean hasFail = pts.stream().anyMatch(p -> p.getTrangThai() == TrangThaiThanhToan.THAT_BAI);
                            boolean hasSuccess = pts.stream().anyMatch(p -> p.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN);
                            return hasFail && hasSuccess;
                        })
                        .sorted(Comparator.comparing(DonHang::getMaDH))
                        .forEach(dh -> rows.add(List.of(
                                dh.getMaDH(), dh.getNgayDat().toString(), dh.getTongTien(),
                                dh.getTrangThai().name(), "Co GD THAT_BAI va DA_THANH_TOAN"
                        )));
            }
            default -> throw new BadRequestException("Query ID out of range (1-30): " + id);
        }

        long elapsed = System.currentTimeMillis() - start;

        return QueryResultDTO.builder()
                .queryId(id)
                .title(meta.getTitle())
                .description(meta.getDescription())
                .sql(meta.getSql())
                .executionMode("STREAM_API")
                .headers(headers)
                .rows(rows)
                .totalRows(rows.size())
                .executionTimeMs(elapsed)
                .build();
    }

    public QueryComparisonDTO compareQuery(int id) {
        QueryMetaDTO meta = getQueryMeta(id);
        QueryResultDTO sqlResult = executeSqlQuery(id, meta);
        QueryResultDTO lambdaResult = executeStreamQuery(id, meta);

        boolean match = (sqlResult.getTotalRows() == lambdaResult.getTotalRows());
        String summary = match
                ? String.format("PARITY CONFIRMED: Both SQL and Stream API returned exactly %d records.", sqlResult.getTotalRows())
                : String.format("MISMATCH: SQL returned %d records while Stream API returned %d records.", sqlResult.getTotalRows(), lambdaResult.getTotalRows());

        return QueryComparisonDTO.builder()
                .queryId(id)
                .title(meta.getTitle())
                .sqlResult(sqlResult)
                .lambdaResult(lambdaResult)
                .matched(match)
                .matchSummary(summary)
                .build();
    }
}
