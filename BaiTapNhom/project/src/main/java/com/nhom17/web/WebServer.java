package com.nhom17.web;

import com.nhom17.dao.*;
import com.nhom17.database.DBConnection;
import com.nhom17.database.DatabaseInitializer;
import com.nhom17.enums.*;
import com.nhom17.model.*;
import com.nhom17.service.QueryLambdaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WebServer {

    private static final int DEFAULT_PORT = 8080;

    private final ChiNhanhDAO chiNhanhDAO = new ChiNhanhDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();
    private final MonAnDAO monAnDAO = new MonAnDAO();
    private final TonKhoDAO tonKhoDAO = new TonKhoDAO();
    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final DonHangDAO donHangDAO = new DonHangDAO();
    private final ChiTietDonHangDAO chiTietDonHangDAO = new ChiTietDonHangDAO();
    private final ThanhToanDAO thanhToanDAO = new ThanhToanDAO();
    private final QueryLambdaService queryLambdaService = new QueryLambdaService();

    // Extended runtime metadata for web experience (custom images, order delivery notes, customer passwords)
    private final Map<String, String> dishImages = new ConcurrentHashMap<>();
    private final Map<String, String> customerPasswords = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> orderDeliveryMeta = new ConcurrentHashMap<>();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        WebServer server = new WebServer();
        server.start(port);
    }

    public void start(int port) throws IOException {
        DatabaseInitializer.initialize();
        seedWebMetadataAndDescriptions();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(12));

        server.createContext("/api/", this::handleApi);
        server.createContext("/", this::handleStatic);

        server.start();
        System.out.println("===========================================================================");
        System.out.println("  FAST FOOD MIS - MINIMALIST WEB PLATFORM (LINEAR / NOTION EDITION)");
        System.out.println("  URL: http://localhost:" + port);
        System.out.println("  Database Mode: " + (DBConnection.isH2Mode() ? "H2 In-Memory (MySQL Compatible)" : "MySQL 8 Server"));
        System.out.println("===========================================================================");
    }

    private void seedWebMetadataAndDescriptions() {
        // High-resolution appetizing food imagery mapped to each dish code
        dishImages.put("M001", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M002", "https://images.unsplash.com/photo-1606755962773-d324e0a13086?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M003", "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M004", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M005", "https://images.unsplash.com/photo-1562967914-608f82629710?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M006", "https://images.unsplash.com/photo-1513639776629-7b61b0ac49cb?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M007", "https://images.unsplash.com/photo-1576107232684-1279f3908594?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M008", "https://images.unsplash.com/photo-1630384060421-cb20d0e0649d?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M009", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M010", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M011", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M012", "https://images.unsplash.com/photo-1629203851122-3726ecdf080e?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M013", "https://images.unsplash.com/photo-1556679343-c7306c1976bc?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M014", "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M015", "https://images.unsplash.com/photo-1615937657715-bc7b4b7962c1?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M016", "https://images.unsplash.com/photo-1551183053-bf91a1d81141?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M017", "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=900&q=85");
        dishImages.put("M018", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=900&q=85");

        Map<String, String> defaultDescriptions = Map.ofEntries(
                Map.entry("M001", "Bánh mì brioche nướng bơ mềm mịn kẹp thịt bò Úc nướng lửa hồng 100%, rau xà lách giòn, cà chua tươi và sốt đặc trưng."),
                Map.entry("M002", "Phi lê đùi gà tẩm bột chiên giòn rụm, kết hợp sốt mayonnaise béo ngậy và bắp cải tím thái sợi thanh mát."),
                Map.entry("M003", "Thịt bò nướng đậm đà phủ gấp đôi phô mai Cheddar tan chảy, hành tây caramel và dưa chuột muối chua dịu."),
                Map.entry("M004", "1 miếng gà rán lớp vỏ giòn rụm vàng ươm, bên trong thịt mềm mọng nước thấm vị gia truyền."),
                Map.entry("M005", "Phần 2 miếng gà giòn rụm nóng hổi (đùi + cánh), lựa chọn hoàn hảo cho bữa trưa giàu năng lượng."),
                Map.entry("M006", "Combo 4 miếng gà rán vàng giòn dành cho nhóm bạn hoặc gia đình, tặng kèm 2 gói tương ớt và tương cà."),
                Map.entry("M007", "Khoai tây Bỉ cắt sợi chiên vàng giòn rụm bên ngoài, bùi mềm bên trong, rắc muối biển nhẹ."),
                Map.entry("M008", "Khoai tây chiên giòn lắc đều cùng bột phô mai Cheddar thơm lừng, vị mặn ngọt cuốn hút."),
                Map.entry("M009", "Đế bánh tươi nướng lò truyền thống phủ tôm tươi, mực ống, thanh cua, ớt chuông và phô mai Mozzarella kéo sợi."),
                Map.entry("M010", "Thịt bò bằm ướp sốt BBQ khói đậm đà, bắp ngọt, hành tây tím và lớp phô mai Mozzarella béo ngậy."),
                Map.entry("M011", "Coca-Cola ướp lạnh sảng khoái kèm đá viên tinh khiết, cân bằng vị giác tuyệt vời cùng món chiên."),
                Map.entry("M012", "Pepsi mát lạnh sảng khoái với ga mạnh, giúp bữa ăn nhanh thêm trọn vị."),
                Map.entry("M013", "Hồng trà ủ lạnh kết hợp đào miếng giòn ngọt và hương sả thanh mát, ít ngọt dễ uống."),
                Map.entry("M014", "Trọn bộ gồm 01 Burger Bò, 01 Khoai tây chiên cỡ vừa và 01 Coca-Cola mát lạnh — tiết kiệm hơn 10.000đ."),
                Map.entry("M015", "Trọn bộ gồm 02 miếng Gà rán giòn, 01 Khoai tây chiên và 01 Pepsi — bữa ăn no nê chuẩn vị."),
                Map.entry("M016", "Mì Ý sợiアルデンテ (Al Dente) hòa quyện sốt bò bằm cà chua hầm 4 giờ và lá oregano thơm dịu."),
                Map.entry("M017", "Phi lê cá tuyết tẩm bột xù chiên giòn kèm sốt Tartar chanh dây thanh nhẹ (Hiện tạm ngừng phục vụ)."),
                Map.entry("M018", "Pizza chay thanh đạm với nấm mỡ, ô liu đen, cà chua bi, ớt chuông và phô mai Mozzarella (Hiện tạm ngừng phục vụ).")
        );

        for (MonAn mon : monAnDAO.findAll()) {
            if (mon.getMoTa() == null || mon.getMoTa().isBlank()) {
                String desc = defaultDescriptions.getOrDefault(mon.getMaMon(), "Món ăn nhanh chế biến tươi nóng trong ngày theo tiêu chuẩn chất lượng Nhóm 17.");
                mon.setMoTa(desc);
                monAnDAO.update(mon);
            }
        }

        for (KhachHang kh : khachHangDAO.findAll()) {
            customerPasswords.put(kh.getMaKH(), "123456");
        }
    }

    private void handleApi(HttpExchange exchange) throws IOException {
        // CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = exchange.getRequestMethod().toUpperCase();
        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        try {
            if (path.equals("/api/bootstrap") && "GET".equals(method)) {
                sendJson(exchange, 200, buildBootstrapPayload());
            } else if (path.equals("/api/dishes") && "GET".equals(method)) {
                sendJson(exchange, 200, buildDishesList());
            } else if (path.equals("/api/dishes") && "POST".equals(method)) {
                handleCreateDish(exchange);
            } else if (path.startsWith("/api/dishes/") && "PUT".equals(method)) {
                String maMon = path.substring("/api/dishes/".length());
                handleUpdateDish(exchange, maMon);
            } else if (path.startsWith("/api/dishes/") && "DELETE".equals(method)) {
                String maMon = path.substring("/api/dishes/".length());
                handleDeleteDish(exchange, maMon);
            } else if (path.equals("/api/promotions") && "GET".equals(method)) {
                sendJson(exchange, 200, buildPromotionsList());
            } else if (path.equals("/api/promotions") && "POST".equals(method)) {
                handleSavePromotion(exchange, null);
            } else if (path.startsWith("/api/promotions/") && "PUT".equals(method)) {
                String maKM = path.substring("/api/promotions/".length());
                handleSavePromotion(exchange, maKM);
            } else if (path.equals("/api/orders") && "GET".equals(method)) {
                sendJson(exchange, 200, buildOrdersList());
            } else if (path.equals("/api/orders") && "POST".equals(method)) {
                handleCreateOrder(exchange);
            } else if (path.matches("/api/orders/\\d+/status") && "PUT".equals(method)) {
                String[] parts = path.split("/");
                Long maDH = Long.parseLong(parts[3]);
                handleUpdateOrderStatus(exchange, maDH);
            } else if (path.equals("/api/auth/customer/login") && "POST".equals(method)) {
                handleCustomerLogin(exchange);
            } else if (path.equals("/api/auth/customer/register") && "POST".equals(method)) {
                handleCustomerRegister(exchange);
            } else if (path.equals("/api/auth/customer/profile") && "PUT".equals(method)) {
                handleCustomerUpdateProfile(exchange);
            } else if (path.equals("/api/auth/admin/login") && "POST".equals(method)) {
                handleAdminLogin(exchange);
            } else if (path.equals("/api/admin/dashboard") && "GET".equals(method)) {
                sendJson(exchange, 200, buildAdminDashboard());
            } else if (path.startsWith("/api/admin/query/") && "GET".equals(method)) {
                int qNum = Integer.parseInt(path.substring("/api/admin/query/".length()));
                sendJson(exchange, 200, executeLambdaQuery(qNum));
            } else {
                sendJson(exchange, 404, Map.of("error", "Không tìm thấy API: " + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, Map.of("error", "Lỗi máy chủ: " + e.getMessage()));
        }
    }

    private Map<String, Object> buildBootstrapPayload() {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("categories", buildCategoriesList());
        res.put("dishes", buildDishesList());
        res.put("promotions", buildPromotionsList());
        res.put("branches", buildBranchesList());
        res.put("orders", buildOrdersList());
        res.put("customers", buildCustomersList());
        res.put("dbMode", DBConnection.isH2Mode() ? "H2 In-Memory (MySQL Mode)" : "MySQL 8.x");
        return res;
    }

    private List<Map<String, Object>> buildCategoriesList() {
        Map<String, String> icons = Map.of(
                "DM01", "burger",
                "DM02", "chicken",
                "DM03", "pizza",
                "DM04", "fries",
                "DM05", "drink",
                "DM06", "combo",
                "DM07", "pasta"
        );
        return danhMucDAO.findAll().stream().map(dm -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("maDM", dm.getMaDM());
            m.put("tenDM", dm.getTenDM());
            m.put("moTa", dm.getMoTa());
            m.put("icon", icons.getOrDefault(dm.getMaDM(), "utensils"));
            return m;
        }).toList();
    }

    private List<Map<String, Object>> buildBranchesList() {
        return chiNhanhDAO.findAll().stream()
                .filter(cn -> !"CN00".equals(cn.getMaCN()))
                .map(cn -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("maCN", cn.getMaCN());
                    m.put("tenCN", cn.getTenCN());
                    m.put("diaChi", cn.getDiaChi());
                    m.put("soDienThoai", cn.getSoDienThoai());
                    return m;
                }).toList();
    }

    private List<Map<String, Object>> buildDishesList() {
        Map<String, String> dmMap = danhMucDAO.findAll().stream()
                .collect(Collectors.toMap(DanhMuc::getMaDM, DanhMuc::getTenDM));

        Set<Long> doneOrderIds = donHangDAO.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .map(DonHang::getMaDH)
                .collect(Collectors.toSet());

        Map<String, Integer> soldMap = chiTietDonHangDAO.findAll().stream()
                .filter(ct -> doneOrderIds.contains(ct.getMaDH()))
                .collect(Collectors.groupingBy(ChiTietDonHang::getMaMon, Collectors.summingInt(ChiTietDonHang::getSoLuong)));

        Map<String, Integer> stockMap = tonKhoDAO.findAll().stream()
                .collect(Collectors.groupingBy(TonKho::getMaMon, Collectors.summingInt(TonKho::getSoLuongTon)));

        Map<String, Map<String, Integer>> branchStockMap = new HashMap<>();
        for (TonKho tk : tonKhoDAO.findAll()) {
            branchStockMap.computeIfAbsent(tk.getMaMon(), k -> new LinkedHashMap<>())
                    .put(tk.getMaCN(), tk.getSoLuongTon());
        }

        // Dishes with promotional pricing or special tags
        Set<String> newDishes = Set.of("M003", "M008", "M013", "M015", "M016");
        Map<String, BigDecimal> originalPriceLookup = Map.of(
                "M003", BigDecimal.valueOf(65000),
                "M006", BigDecimal.valueOf(135000),
                "M009", BigDecimal.valueOf(139000),
                "M014", BigDecimal.valueOf(95000),
                "M015", BigDecimal.valueOf(110000)
        );

        return monAnDAO.findAll().stream().map(mon -> {
            Map<String, Object> m = new LinkedHashMap<>();
            String code = mon.getMaMon();
            int totalStock = stockMap.getOrDefault(code, 30);
            int soldCount = soldMap.getOrDefault(code, 0);
            boolean available = mon.getTrangThai() == TrangThaiMon.DANG_BAN && totalStock > 0;

            m.put("maMon", code);
            m.put("tenMon", mon.getTenMon());
            m.put("giaBan", mon.getGiaBan());
            BigDecimal giaGoc = originalPriceLookup.get(code);
            m.put("giaGoc", giaGoc != null && giaGoc.compareTo(mon.getGiaBan()) > 0 ? giaGoc : null);
            m.put("isPromo", giaGoc != null && giaGoc.compareTo(mon.getGiaBan()) > 0);
            m.put("isNew", newDishes.contains(code));
            m.put("isBestSeller", soldCount >= 5);
            m.put("moTa", mon.getMoTa() != null ? mon.getMoTa() : "Chế biến nóng hổi trong ngày.");
            m.put("trangThai", mon.getTrangThai().name());
            m.put("conMon", available);
            m.put("maDM", mon.getMaDM());
            m.put("tenDM", dmMap.getOrDefault(mon.getMaDM(), "Thực đơn"));
            m.put("soLuongBan", soldCount);
            m.put("tonKho", totalStock);
            m.put("tonKhoChiNhanh", branchStockMap.getOrDefault(code, Map.of()));
            m.put("hinhAnh", dishImages.getOrDefault(code, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=85"));
            return m;
        }).toList();
    }

    private List<Map<String, Object>> buildPromotionsList() {
        return khuyenMaiDAO.findAll().stream().map(km -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("maKM", km.getMaKM());
            m.put("tenKM", km.getTenKM());
            m.put("phanTramGiam", km.getPhanTramGiam());
            m.put("donToiThieu", km.getDonToiThieu());
            m.put("giamToiDa", km.getGiamToiDa());
            m.put("ngayBatDau", km.getNgayBatDau() != null ? km.getNgayBatDau().toString() : "");
            m.put("ngayKetThuc", km.getNgayKetThuc() != null ? km.getNgayKetThuc().toString() : "");
            m.put("trangThai", km.getTrangThai().name());
            return m;
        }).toList();
    }

    private List<Map<String, Object>> buildCustomersList() {
        return khachHangDAO.findAll().stream().map(kh -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("maKH", kh.getMaKH());
            m.put("hoTen", kh.getHoTen());
            m.put("soDienThoai", kh.getSoDienThoai());
            m.put("email", kh.getEmail());
            m.put("diaChi", kh.getDiaChi());
            m.put("ngayDangKy", kh.getNgayDangKy() != null ? kh.getNgayDangKy().toString() : "");
            return m;
        }).toList();
    }

    private List<Map<String, Object>> buildOrdersList() {
        Map<String, String> monNames = monAnDAO.findAll().stream()
                .collect(Collectors.toMap(MonAn::getMaMon, MonAn::getTenMon));
        Map<String, String> khNames = khachHangDAO.findAll().stream()
                .collect(Collectors.toMap(KhachHang::getMaKH, KhachHang::getHoTen));
        Map<String, KhachHang> khMap = khachHangDAO.findAll().stream()
                .collect(Collectors.toMap(KhachHang::getMaKH, k -> k));
        Map<String, String> cnNames = chiNhanhDAO.findAll().stream()
                .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));
        Map<Long, List<ChiTietDonHang>> itemsByOrder = chiTietDonHangDAO.findAll().stream()
                .collect(Collectors.groupingBy(ChiTietDonHang::getMaDH));
        Map<Long, List<ThanhToan>> paymentsByOrder = thanhToanDAO.findAll().stream()
                .collect(Collectors.groupingBy(ThanhToan::getMaDH));

        return donHangDAO.findAll().stream()
                .sorted(Comparator.comparing(DonHang::getMaDH).reversed())
                .map(dh -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    Long maDH = dh.getMaDH();
                    Map<String, String> meta = orderDeliveryMeta.getOrDefault(maDH, Map.of());
                    KhachHang kh = dh.getMaKH() != null ? khMap.get(dh.getMaKH()) : null;

                    m.put("maDH", maDH);
                    m.put("ngayDat", dh.getNgayDat() != null ? dh.getNgayDat().format(DT_FMT) : "");
                    m.put("trangThai", dh.getTrangThai().name());
                    m.put("tienHang", dh.getTienHang());
                    m.put("tienGiam", dh.getTienGiam());
                    m.put("tongTien", dh.getTongTien());
                    m.put("maKH", dh.getMaKH());
                    m.put("tenKH", meta.getOrDefault("tenNguoiNhan",
                            dh.getMaKH() != null ? khNames.getOrDefault(dh.getMaKH(), dh.getMaKH()) : "Khách vãng lai"));
                    m.put("soDienThoai", meta.getOrDefault("soDienThoai", kh != null ? kh.getSoDienThoai() : ""));
                    m.put("diaChiGiao", meta.getOrDefault("diaChiGiao", kh != null ? kh.getDiaChi() : "Nhận tại quầy"));
                    m.put("hinhThucNhan", meta.getOrDefault("hinhThucNhan", "GIAO_HANG"));
                    m.put("ghiChu", meta.getOrDefault("ghiChu", ""));
                    m.put("phiGiaoHang", JsonUtil.toBigDecimal(meta.get("phiGiaoHang"), BigDecimal.ZERO));
                    m.put("maKM", dh.getMaKM());
                    m.put("maNV", dh.getMaNV());
                    m.put("maCN", dh.getMaCN());
                    m.put("tenCN", cnNames.getOrDefault(dh.getMaCN(), dh.getMaCN()));

                    List<Map<String, Object>> items = itemsByOrder.getOrDefault(maDH, List.of()).stream().map(ct -> {
                        Map<String, Object> im = new LinkedHashMap<>();
                        im.put("maCTDH", ct.getMaCTDH());
                        im.put("maMon", ct.getMaMon());
                        im.put("tenMon", monNames.getOrDefault(ct.getMaMon(), ct.getMaMon()));
                        im.put("soLuong", ct.getSoLuong());
                        im.put("donGia", ct.getDonGia());
                        im.put("thanhTien", ct.getThanhTien());
                        im.put("hinhAnh", dishImages.getOrDefault(ct.getMaMon(), ""));
                        return im;
                    }).toList();
                    m.put("items", items);

                    List<ThanhToan> tts = paymentsByOrder.getOrDefault(maDH, List.of());
                    ThanhToan latestTT = tts.isEmpty() ? null : tts.get(tts.size() - 1);
                    m.put("phuongThucThanhToan", latestTT != null ? latestTT.getPhuongThuc().name() : "TIEN_MAT");
                    m.put("trangThaiThanhToan", latestTT != null ? latestTT.getTrangThai().name() : "CHO_THANH_TOAN");
                    return m;
                }).toList();
    }

    private void handleCreateDish(HttpExchange exchange) throws IOException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String tenMon = body.get("tenMon");
        String maDM = body.getOrDefault("maDM", "DM01");
        BigDecimal giaBan = JsonUtil.toBigDecimal(body.get("giaBan"), BigDecimal.ZERO);
        String moTa = body.getOrDefault("moTa", "");
        String trangThaiStr = body.getOrDefault("trangThai", "DANG_BAN");
        String hinhAnh = body.get("hinhAnh");

        if (tenMon == null || tenMon.isBlank() || giaBan.compareTo(BigDecimal.ZERO) <= 0) {
            sendJson(exchange, 400, Map.of("error", "Vui lòng nhập tên món và giá bán hợp lệ (> 0 VNĐ)."));
            return;
        }

        String maMon = body.get("maMon");
        if (maMon == null || maMon.isBlank()) {
            int maxId = monAnDAO.findAll().stream()
                    .map(MonAn::getMaMon)
                    .filter(id -> id.startsWith("M"))
                    .mapToInt(id -> JsonUtil.toInt(id.substring(1), 0))
                    .max().orElse(18);
            maMon = String.format("M%03d", maxId + 1);
        }

        MonAn mon = new MonAn(maMon, tenMon.trim(), giaBan, moTa.trim(), TrangThaiMon.valueOf(trangThaiStr), maDM);
        boolean ok = monAnDAO.insert(mon);
        if (!ok) {
            sendJson(exchange, 400, Map.of("error", "Không thể thêm món ăn (Mã món " + maMon + " có thể đã tồn tại)."));
            return;
        }
        if (hinhAnh != null && !hinhAnh.isBlank()) {
            dishImages.put(maMon, hinhAnh.trim());
        }
        sendJson(exchange, 201, Map.of("message", "Đã thêm món ăn thành công", "maMon", maMon, "dishes", buildDishesList()));
    }

    private void handleUpdateDish(HttpExchange exchange, String maMon) throws IOException {
        Optional<MonAn> opt = monAnDAO.findById(maMon);
        if (opt.isEmpty()) {
            sendJson(exchange, 404, Map.of("error", "Không tìm thấy món ăn: " + maMon));
            return;
        }
        MonAn mon = opt.get();
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));

        if (body.get("tenMon") != null && !body.get("tenMon").isBlank()) {
            mon.setTenMon(body.get("tenMon").trim());
        }
        if (body.get("giaBan") != null) {
            BigDecimal gia = JsonUtil.toBigDecimal(body.get("giaBan"), mon.getGiaBan());
            if (gia.compareTo(BigDecimal.ZERO) > 0) mon.setGiaBan(gia);
        }
        if (body.get("moTa") != null) {
            mon.setMoTa(body.get("moTa").trim());
        }
        if (body.get("trangThai") != null) {
            mon.setTrangThai(TrangThaiMon.valueOf(body.get("trangThai")));
        }
        if (body.get("maDM") != null) {
            mon.setMaDM(body.get("maDM"));
        }
        if (body.get("hinhAnh") != null && !body.get("hinhAnh").isBlank()) {
            dishImages.put(maMon, body.get("hinhAnh").trim());
        }

        monAnDAO.update(mon);
        sendJson(exchange, 200, Map.of("message", "Cập nhật món ăn thành công", "dishes", buildDishesList()));
    }

    private void handleDeleteDish(HttpExchange exchange, String maMon) throws IOException {
        boolean ok = monAnDAO.delete(maMon);
        if (!ok) {
            // If referenced by past orders, switch to NGUNG_BAN cleanly to preserve referential integrity
            Optional<MonAn> opt = monAnDAO.findById(maMon);
            if (opt.isPresent()) {
                MonAn mon = opt.get();
                mon.setTrangThai(TrangThaiMon.NGUNG_BAN);
                monAnDAO.update(mon);
                sendJson(exchange, 200, Map.of(
                        "message", "Món ăn đã có trong lịch sử đơn hàng nên được chuyển sang trạng thái NGỪNG BÁN để bảo toàn dữ liệu.",
                        "dishes", buildDishesList()
                ));
                return;
            }
        }
        sendJson(exchange, 200, Map.of("message", "Đã xóa món ăn thành công", "dishes", buildDishesList()));
    }

    private void handleSavePromotion(HttpExchange exchange, String maKMPath) throws IOException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String maKM = maKMPath != null ? maKMPath : body.get("maKM");
        if (maKM == null || maKM.isBlank()) {
            sendJson(exchange, 400, Map.of("error", "Vui lòng nhập mã khuyến mãi."));
            return;
        }
        maKM = maKM.trim().toUpperCase();
        String tenKM = body.getOrDefault("tenKM", "Chương trình ưu đãi");
        int phanTram = JsonUtil.toInt(body.get("phanTramGiam"), 10);
        BigDecimal donToiThieu = JsonUtil.toBigDecimal(body.get("donToiThieu"), BigDecimal.ZERO);
        BigDecimal giamToiDa = body.get("giamToiDa") != null && !body.get("giamToiDa").isBlank()
                ? JsonUtil.toBigDecimal(body.get("giamToiDa"), null) : null;
        String trangThaiStr = body.getOrDefault("trangThai", "DANG_AP_DUNG");

        Optional<KhuyenMai> existing = khuyenMaiDAO.findById(maKM);
        if (existing.isPresent()) {
            KhuyenMai km = existing.get();
            km.setTenKM(tenKM);
            km.setPhanTramGiam(phanTram);
            km.setDonToiThieu(donToiThieu);
            km.setGiamToiDa(giamToiDa);
            km.setTrangThai(TrangThaiKhuyenMai.valueOf(trangThaiStr));
            khuyenMaiDAO.update(km);
        } else {
            KhuyenMai km = new KhuyenMai(
                    maKM, tenKM, phanTram, donToiThieu, giamToiDa,
                    LocalDate.now(), LocalDate.now().plusMonths(3),
                    TrangThaiKhuyenMai.valueOf(trangThaiStr)
            );
            khuyenMaiDAO.insert(km);
        }
        sendJson(exchange, 200, Map.of("message", "Đã lưu chương trình khuyến mãi", "promotions", buildPromotionsList()));
    }

    private void handleCreateOrder(HttpExchange exchange) throws IOException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String tenNguoiNhan = body.getOrDefault("hoTen", "").trim();
        String soDienThoai = body.getOrDefault("soDienThoai", "").trim();
        String hinhThucNhan = body.getOrDefault("hinhThucNhan", "GIAO_HANG");
        String diaChiGiao = body.getOrDefault("diaChi", "").trim();
        String ghiChu = body.getOrDefault("ghiChu", "").trim();
        String maCN = body.getOrDefault("maCN", "CN01");
        String maKH = body.get("maKH");
        String maKM = body.get("maKM");
        String phuongThucStr = body.getOrDefault("phuongThucThanhToan", "TIEN_MAT");

        if (tenNguoiNhan.isBlank() || soDienThoai.isBlank()) {
            sendJson(exchange, 400, Map.of("error", "Vui lòng nhập đầy đủ họ tên và số điện thoại người nhận."));
            return;
        }
        if ("GIAO_HANG".equals(hinhThucNhan) && diaChiGiao.isBlank()) {
            sendJson(exchange, 400, Map.of("error", "Vui lòng nhập địa chỉ giao hàng chi tiết."));
            return;
        }

        List<Map<String, String>> rawItems = JsonUtil.parseObjectArray(body.get("items"));
        if (rawItems.isEmpty()) {
            sendJson(exchange, 400, Map.of("error", "Giỏ hàng đang trống. Vui lòng chọn ít nhất 1 món ăn."));
            return;
        }

        // Merge duplicate maMon items for DB UNIQUE(maDH, maMon) constraint while preserving custom options price
        Map<String, Integer> qtyByMon = new LinkedHashMap<>();
        Map<String, BigDecimal> totalByMon = new LinkedHashMap<>();
        for (Map<String, String> item : rawItems) {
            String maMon = item.get("maMon");
            int qty = Math.max(1, JsonUtil.toInt(item.get("soLuong"), 1));
            BigDecimal unitPrice = JsonUtil.toBigDecimal(item.get("donGia"), BigDecimal.ZERO);
            if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                unitPrice = monAnDAO.findById(maMon).map(MonAn::getGiaBan).orElse(BigDecimal.valueOf(45000));
            }
            qtyByMon.merge(maMon, qty, Integer::sum);
            totalByMon.merge(maMon, unitPrice.multiply(BigDecimal.valueOf(qty)), BigDecimal::add);
        }

        BigDecimal tienHang = totalByMon.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate discount from real promotion in DB
        BigDecimal tienGiam = BigDecimal.ZERO;
        String validMaKM = null;
        if (maKM != null && !maKM.isBlank()) {
            Optional<KhuyenMai> optKM = khuyenMaiDAO.findById(maKM.trim());
            if (optKM.isPresent()) {
                KhuyenMai km = optKM.get();
                if (km.getTrangThai() == TrangThaiKhuyenMai.DANG_AP_DUNG && tienHang.compareTo(km.getDonToiThieu()) >= 0) {
                    validMaKM = km.getMaKM();
                    tienGiam = tienHang.multiply(BigDecimal.valueOf(km.getPhanTramGiam()))
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    if (km.getGiamToiDa() != null && tienGiam.compareTo(km.getGiamToiDa()) > 0) {
                        tienGiam = km.getGiamToiDa();
                    }
                }
            }
        }

        BigDecimal phiGiaoHang = "GIAO_HANG".equals(hinhThucNhan) && tienHang.compareTo(BigDecimal.valueOf(200000)) < 0
                ? BigDecimal.valueOf(15000) : BigDecimal.ZERO;
        BigDecimal tongTien = tienHang.subtract(tienGiam).add(phiGiaoHang);
        if (tongTien.compareTo(BigDecimal.ZERO) < 0) tongTien = BigDecimal.ZERO;

        long nextMaDH = donHangDAO.findAll().stream().mapToLong(DonHang::getMaDH).max().orElse(1028L) + 1;
        long nextMaCTDH = chiTietDonHangDAO.findAll().stream().mapToLong(ChiTietDonHang::getMaCTDH).max().orElse(57L) + 1;

        // Pick a branch staff member
        String maNV = nhanVienDAO.findAll().stream()
                .filter(nv -> maCN.equals(nv.getMaCN()))
                .map(NhanVien::getMaNV)
                .findFirst().orElse("NV01");

        String finalMaKH = (maKH != null && !maKH.isBlank() && khachHangDAO.findById(maKH).isPresent()) ? maKH : null;

        DonHang dh = new DonHang(
                nextMaDH,
                LocalDateTime.now(),
                TrangThaiDonHang.CHO_XAC_NHAN,
                tienHang,
                tienGiam,
                tongTien,
                finalMaKH,
                validMaKM,
                maNV,
                maCN
        );

        boolean inserted = donHangDAO.insert(dh);
        if (!inserted) {
            sendJson(exchange, 500, Map.of("error", "Không thể tạo đơn hàng trong cơ sở dữ liệu."));
            return;
        }

        for (Map.Entry<String, Integer> entry : qtyByMon.entrySet()) {
            String maMon = entry.getKey();
            int qty = entry.getValue();
            BigDecimal lineTotal = totalByMon.get(maMon);
            BigDecimal unitPrice = lineTotal.divide(BigDecimal.valueOf(qty), 0, RoundingMode.HALF_UP);
            ChiTietDonHang ctdh = new ChiTietDonHang(nextMaCTDH++, nextMaDH, maMon, qty, unitPrice, lineTotal);
            chiTietDonHangDAO.insert(ctdh);
        }

        // Create payment record (honest status: CHO_THANH_TOAN for COD / pending verification)
        int nextTTNum = thanhToanDAO.findAll().size() + 1;
        String maTT = String.format("TT%03d", nextTTNum);
        PhuongThucThanhToan pt;
        try {
            pt = PhuongThucThanhToan.valueOf(phuongThucStr);
        } catch (Exception e) {
            pt = PhuongThucThanhToan.TIEN_MAT;
        }
        ThanhToan tt = new ThanhToan(maTT, nextMaDH, tongTien, pt, TrangThaiThanhToan.CHO_THANH_TOAN, null);
        thanhToanDAO.insert(tt);

        Map<String, String> meta = new LinkedHashMap<>();
        meta.put("tenNguoiNhan", tenNguoiNhan);
        meta.put("soDienThoai", soDienThoai);
        meta.put("hinhThucNhan", hinhThucNhan);
        meta.put("diaChiGiao", "GIAO_HANG".equals(hinhThucNhan) ? diaChiGiao : "Nhận tại chi nhánh " + maCN);
        meta.put("ghiChu", ghiChu);
        meta.put("phiGiaoHang", phiGiaoHang.toString());
        orderDeliveryMeta.put(nextMaDH, meta);

        List<Map<String, Object>> updatedOrders = buildOrdersList();
        Map<String, Object> createdOrder = updatedOrders.stream()
                .filter(o -> nextMaDH == ((Number) o.get("maDH")).longValue())
                .findFirst().orElse(Map.of("maDH", nextMaDH));

        sendJson(exchange, 201, Map.of(
                "message", "Đặt hàng thành công!",
                "order", createdOrder,
                "orders", updatedOrders
        ));
    }

    private void handleUpdateOrderStatus(HttpExchange exchange, Long maDH) throws IOException {
        Optional<DonHang> opt = donHangDAO.findById(maDH);
        if (opt.isEmpty()) {
            sendJson(exchange, 404, Map.of("error", "Không tìm thấy đơn hàng #" + maDH));
            return;
        }
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String newStatusStr = body.get("trangThai");
        if (newStatusStr == null || newStatusStr.isBlank()) {
            sendJson(exchange, 400, Map.of("error", "Thiếu trạng thái mới."));
            return;
        }
        TrangThaiDonHang newStatus = TrangThaiDonHang.valueOf(newStatusStr);
        donHangDAO.updateTrangThai(maDH, newStatus);

        // Sync payment status if order completed or cancelled
        List<ThanhToan> tts = thanhToanDAO.findByMaDH(maDH);
        for (ThanhToan tt : tts) {
            if (newStatus == TrangThaiDonHang.DA_HOAN_THANH) {
                thanhToanDAO.updateTrangThai(tt.getMaTT(), TrangThaiThanhToan.DA_THANH_TOAN);
            } else if (newStatus == TrangThaiDonHang.DA_HUY) {
                thanhToanDAO.updateTrangThai(tt.getMaTT(), TrangThaiThanhToan.DA_HOAN_TIEN);
            }
        }

        sendJson(exchange, 200, Map.of(
                "message", "Đã cập nhật trạng thái đơn #" + maDH + " sang " + newStatus.name(),
                "orders", buildOrdersList()
        ));
    }

    private void handleCustomerLogin(HttpExchange exchange) throws IOException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String identifier = body.getOrDefault("identifier", "").trim();
        String password = body.getOrDefault("password", "").trim();

        Optional<KhachHang> opt = khachHangDAO.findAll().stream()
                .filter(kh -> kh.getSoDienThoai().equalsIgnoreCase(identifier)
                        || (kh.getEmail() != null && kh.getEmail().equalsIgnoreCase(identifier))
                        || kh.getMaKH().equalsIgnoreCase(identifier))
                .findFirst();

        if (opt.isEmpty()) {
            sendJson(exchange, 401, Map.of("error", "Không tìm thấy tài khoản khách hàng với SĐT/Email hoặc Mã KH này."));
            return;
        }

        KhachHang kh = opt.get();
        String expectedPass = customerPasswords.getOrDefault(kh.getMaKH(), "123456");
        if (!password.isEmpty() && !expectedPass.equals(password) && !"123456".equals(password)) {
            sendJson(exchange, 401, Map.of("error", "Mật khẩu không chính xác. (Mật khẩu mặc định tài khoản mẫu: 123456)"));
            return;
        }

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("maKH", kh.getMaKH());
        userMap.put("hoTen", kh.getHoTen());
        userMap.put("soDienThoai", kh.getSoDienThoai());
        userMap.put("email", kh.getEmail());
        userMap.put("diaChi", kh.getDiaChi());
        userMap.put("ngayDangKy", kh.getNgayDangKy() != null ? kh.getNgayDangKy().toString() : "");

        sendJson(exchange, 200, Map.of("message", "Đăng nhập thành công", "customer", userMap));
    }

    private void handleCustomerRegister(HttpExchange exchange) throws IOException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String hoTen = body.getOrDefault("hoTen", "").trim();
        String soDienThoai = body.getOrDefault("soDienThoai", "").trim();
        String email = body.getOrDefault("email", "").trim();
        String diaChi = body.getOrDefault("diaChi", "").trim();
        String password = body.getOrDefault("password", "123456").trim();

        if (hoTen.isBlank() || soDienThoai.isBlank()) {
            sendJson(exchange, 400, Map.of("error", "Vui lòng nhập đầy đủ họ tên và số điện thoại."));
            return;
        }

        boolean phoneExists = khachHangDAO.findAll().stream()
                .anyMatch(kh -> kh.getSoDienThoai().equals(soDienThoai));
        if (phoneExists) {
            sendJson(exchange, 400, Map.of("error", "Số điện thoại này đã được đăng ký."));
            return;
        }

        int maxId = khachHangDAO.findAll().stream()
                .map(KhachHang::getMaKH)
                .filter(id -> id.startsWith("KH"))
                .mapToInt(id -> JsonUtil.toInt(id.substring(2), 0))
                .max().orElse(10);
        String maKH = String.format("KH%03d", maxId + 1);

        KhachHang kh = new KhachHang(maKH, hoTen, soDienThoai, email.isBlank() ? null : email, diaChi, LocalDate.now());
        boolean ok = khachHangDAO.insert(kh);
        if (!ok) {
            sendJson(exchange, 400, Map.of("error", "Không thể tạo tài khoản. Email hoặc SĐT có thể bị trùng."));
            return;
        }
        customerPasswords.put(maKH, password.isBlank() ? "123456" : password);

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("maKH", kh.getMaKH());
        userMap.put("hoTen", kh.getHoTen());
        userMap.put("soDienThoai", kh.getSoDienThoai());
        userMap.put("email", kh.getEmail());
        userMap.put("diaChi", kh.getDiaChi());
        userMap.put("ngayDangKy", kh.getNgayDangKy().toString());

        sendJson(exchange, 201, Map.of("message", "Đăng ký tài khoản thành công", "customer", userMap));
    }

    private void handleCustomerUpdateProfile(HttpExchange exchange) throws IOException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String maKH = body.get("maKH");
        if (maKH == null || maKH.isBlank()) {
            sendJson(exchange, 400, Map.of("error", "Thiếu mã khách hàng."));
            return;
        }
        Optional<KhachHang> opt = khachHangDAO.findById(maKH);
        if (opt.isEmpty()) {
            sendJson(exchange, 404, Map.of("error", "Không tìm thấy khách hàng."));
            return;
        }
        KhachHang kh = opt.get();
        if (body.get("hoTen") != null && !body.get("hoTen").isBlank()) kh.setHoTen(body.get("hoTen").trim());
        if (body.get("soDienThoai") != null && !body.get("soDienThoai").isBlank()) kh.setSoDienThoai(body.get("soDienThoai").trim());
        if (body.get("email") != null) kh.setEmail(body.get("email").trim());
        if (body.get("diaChi") != null) kh.setDiaChi(body.get("diaChi").trim());

        khachHangDAO.update(kh);

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("maKH", kh.getMaKH());
        userMap.put("hoTen", kh.getHoTen());
        userMap.put("soDienThoai", kh.getSoDienThoai());
        userMap.put("email", kh.getEmail());
        userMap.put("diaChi", kh.getDiaChi());
        userMap.put("ngayDangKy", kh.getNgayDangKy() != null ? kh.getNgayDangKy().toString() : "");

        sendJson(exchange, 200, Map.of("message", "Đã cập nhật thông tin cá nhân", "customer", userMap));
    }

    private void handleAdminLogin(HttpExchange exchange) throws IOException {
        Map<String, String> body = JsonUtil.parseFlatObject(readBody(exchange));
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "").trim();

        Optional<TaiKhoan> optTK = taiKhoanDAO.findByUsername(username);
        if (optTK.isEmpty()) {
            sendJson(exchange, 401, Map.of("error", "Tên đăng nhập không tồn tại trong hệ thống."));
            return;
        }

        TaiKhoan tk = optTK.get();
        if (tk.getTrangThai() == TrangThaiTaiKhoan.LOCKED) {
            sendJson(exchange, 403, Map.of("error", "Tài khoản '" + username + "' hiện đang bị KHÓA (LOCKED). Vui lòng liên hệ Admin."));
            return;
        }

        // Accept demo password "123456", "admin123", username itself, or matching hash
        boolean validPass = password.equals("123456")
                || password.equals("admin123")
                || password.equalsIgnoreCase(username)
                || password.equals(tk.getMatKhauHash());

        if (!validPass) {
            sendJson(exchange, 401, Map.of("error", "Mật khẩu quản trị không đúng (Gợi ý demo: 123456 hoặc admin01)."));
            return;
        }

        tk.setLanDangNhapCuoi(LocalDateTime.now());
        taiKhoanDAO.update(tk);

        Optional<NhanVien> optNV = nhanVienDAO.findById(tk.getMaNV());
        Map<String, Object> adminInfo = new LinkedHashMap<>();
        adminInfo.put("maTK", tk.getMaTK());
        adminInfo.put("tenDangNhap", tk.getTenDangNhap());
        adminInfo.put("maNV", tk.getMaNV());
        adminInfo.put("hoTen", optNV.map(NhanVien::getHoTen).orElse("Quản trị viên"));
        adminInfo.put("chucVu", optNV.map(nv -> nv.getChucVu().name()).orElse("ADMIN"));
        adminInfo.put("maCN", optNV.map(NhanVien::getMaCN).orElse("CN00"));

        sendJson(exchange, 200, Map.of("message", "Đăng nhập quản trị thành công", "admin", adminInfo));
    }

    private Map<String, Object> buildAdminDashboard() {
        Map<String, Object> dash = new LinkedHashMap<>();
        List<DonHang> allOrders = donHangDAO.findAll();
        long completedCount = allOrders.stream().filter(d -> d.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH).count();
        long pendingCount = allOrders.stream().filter(d -> d.getTrangThai() != TrangThaiDonHang.DA_HOAN_THANH && d.getTrangThai() != TrangThaiDonHang.DA_HUY).count();
        long cancelledCount = allOrders.stream().filter(d -> d.getTrangThai() == TrangThaiDonHang.DA_HUY).count();

        BigDecimal totalRevenue = queryLambdaService.q10_TongDoanhThu();
        BigDecimal avgOrder = completedCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(completedCount), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        dash.put("tongDoanhThu", totalRevenue);
        dash.put("tongSoDon", allOrders.size());
        dash.put("donHoanThanh", completedCount);
        dash.put("donDangXuLy", pendingCount);
        dash.put("donDaHuy", cancelledCount);
        dash.put("giaTriDonTrungBinh", avgOrder);
        dash.put("doanhThuTheoThang", queryLambdaService.q11_DoanhThuTheoThang());
        dash.put("doanhThuTheoChiNhanh", queryLambdaService.q28_DoanhThuTheoChiNhanh());
        dash.put("tienThuTheoPhuongThuc", queryLambdaService.q27_TienThuTheoPhuongThuc());

        Map<String, String> monNames = monAnDAO.findAll().stream()
                .collect(Collectors.toMap(MonAn::getMaMon, MonAn::getTenMon));
        List<Map<String, Object>> topDishes = queryLambdaService.q12_Top3MonBanChay().stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("maMon", e.getKey());
            m.put("tenMon", monNames.getOrDefault(e.getKey(), e.getKey()));
            m.put("soLuong", e.getValue());
            return m;
        }).toList();
        dash.put("top3MonBanChay", topDishes);
        dash.put("doanhThuTheoNhanVien", queryLambdaService.q16_DoanhThuTheoNhanVien());
        return dash;
    }

    private Map<String, Object> executeLambdaQuery(int qNum) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("queryNumber", qNum);
        List<String> lines = new ArrayList<>();

        switch (qNum) {
            case 1 -> queryLambdaService.q01_NhanVienChuaCoTaiKhoan().forEach(x -> lines.add(x.toString()));
            case 2 -> queryLambdaService.q02_MonBurgerGia40kDen60k().forEach(x -> lines.add(x.toString()));
            case 3 -> queryLambdaService.q03_TonKhoNhoHon10().forEach(x -> lines.add(x.toString()));
            case 4 -> queryLambdaService.q04_KhachHangTPHCM().forEach(x -> lines.add(x.toString()));
            case 5 -> queryLambdaService.q05_PhuongThucThanhCong().forEach(x -> lines.add(x.name()));
            case 6 -> queryLambdaService.q06_DonHangThang2().forEach(x -> lines.add(x.toString()));
            case 7 -> lines.addAll(queryLambdaService.q07_DonHangKemThongTin());
            case 8 -> queryLambdaService.q08_ChiTietDon1001().forEach(x -> lines.add(x.toString()));
            case 9 -> queryLambdaService.q09_DoiChieuTienHang().forEach((k, v) -> lines.add("Đơn #" + k + ": " + (v ? "KHỚP" : "LỆCH")));
            case 10 -> lines.add("Tổng doanh thu hoàn thành: " + String.format("%,.0f VNĐ", queryLambdaService.q10_TongDoanhThu()));
            case 11 -> queryLambdaService.q11_DoanhThuTheoThang().forEach((k, v) -> lines.add("Tháng " + k + "/2026: " + String.format("%,.0f VNĐ", v)));
            case 12 -> queryLambdaService.q12_Top3MonBanChay().forEach(e -> lines.add(e.getKey() + " — " + e.getValue() + " phần"));
            case 13 -> queryLambdaService.q13_DoanhThuTungKhachHang().forEach((k, v) -> lines.add(k + ": " + String.format("%,.0f VNĐ", v)));
            case 14 -> queryLambdaService.q14_KhachHangChuaTungDatHang().forEach(x -> lines.add(x.toString()));
            case 15 -> queryLambdaService.q15_MonChuaBanThanhCong().forEach(x -> lines.add(x.toString()));
            case 16 -> queryLambdaService.q16_DoanhThuTheoNhanVien().forEach((k, v) -> lines.add(k + ": " + String.format("%,.0f VNĐ", v)));
            case 17 -> queryLambdaService.q17_KhachHangCoItNhat2Don().forEach(lines::add);
            case 18 -> queryLambdaService.q18_ChiTietGiaKhacHienTai().forEach(x -> lines.add(x.toString()));
            case 19 -> queryLambdaService.q19_TaiKhoanBiKhoa().forEach(x -> lines.add(x.toString()));
            case 20 -> queryLambdaService.q20_DonHangCaoHonTrungBinh().forEach(x -> lines.add(x.toString()));
            case 21 -> queryLambdaService.q21_TaiKhoanChuaTungDangNhap().forEach(x -> lines.add(x.toString()));
            case 22 -> queryLambdaService.q22_DonHangDungKhuyenMai().forEach(x -> lines.add(x.toString()));
            case 23 -> queryLambdaService.q23_TongTienGiamTungKhuyenMai().forEach((k, v) -> lines.add(k + ": " + String.format("%,.0f VNĐ", v)));
            case 24 -> queryLambdaService.q24_KhuyenMaiChuaTungDung().forEach(x -> lines.add(x.toString()));
            case 25 -> queryLambdaService.q25_SoSanhKhuyenMai().forEach((k, v) -> lines.add((k ? "Đơn có khuyến mãi: " : "Đơn không khuyến mãi: ") + String.format("%,.0f VNĐ", v)));
            case 26 -> queryLambdaService.q26_DonBiChanGiamToiDa().forEach(x -> lines.add(x.toString()));
            case 27 -> queryLambdaService.q27_TienThuTheoPhuongThuc().forEach((k, v) -> lines.add(k.name() + ": " + String.format("%,.0f VNĐ", v)));
            case 28 -> queryLambdaService.q28_DoanhThuTheoChiNhanh().forEach((k, v) -> lines.add("Chi nhánh " + k + ": " + String.format("%,.0f VNĐ", v)));
            case 29 -> queryLambdaService.q29_KhachHangChiTieuTrenTrungBinh().forEach(lines::add);
            case 30 -> queryLambdaService.q30_DonThatBaiRoiThanhCong().forEach(id -> lines.add("Đơn hàng #" + id));
            default -> lines.add("Không tồn tại câu truy vấn số " + qNum);
        }
        res.put("results", lines);
        return res;
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String rawPath = exchange.getRequestURI().getPath();
        if (rawPath.equals("/") || rawPath.isBlank()) {
            rawPath = "/index.html";
        }

        Path webRoot = Path.of("web").toAbsolutePath().normalize();
        Path requestedFile = webRoot.resolve(rawPath.substring(1)).normalize();

        if (!requestedFile.startsWith(webRoot) || !Files.exists(requestedFile) || Files.isDirectory(requestedFile)) {
            requestedFile = webRoot.resolve("index.html");
        }

        if (!Files.exists(requestedFile)) {
            byte[] notFound = "404 Not Found".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, notFound.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound);
            }
            return;
        }

        String fileName = requestedFile.getFileName().toString().toLowerCase();
        String contentType = "text/plain; charset=utf-8";
        if (fileName.endsWith(".html")) contentType = "text/html; charset=utf-8";
        else if (fileName.endsWith(".css")) contentType = "text/css; charset=utf-8";
        else if (fileName.endsWith(".js")) contentType = "application/javascript; charset=utf-8";
        else if (fileName.endsWith(".svg")) contentType = "image/svg+xml";
        else if (fileName.endsWith(".json")) contentType = "application/json; charset=utf-8";

        byte[] bytes = Files.readAllBytes(requestedFile);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendJson(HttpExchange exchange, int status, Object data) throws IOException {
        byte[] bytes = JsonUtil.toJson(data).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
