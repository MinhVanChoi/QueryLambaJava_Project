package com.fastfood.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DatabaseInitService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${app.seed-on-startup:true}")
    private boolean seedOnStartup;

    @PostConstruct
    public void onStartup() {
        if (seedOnStartup) {
            log.info("Checking database schema and sample data on startup...");
            initialize();
        }
    }

    public synchronized void initialize() {
        try {
            createTables();
            // Check if chi_nhanh table is empty
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM chi_nhanh", Integer.class);
            if (count == null || count == 0) {
                log.info("Database is empty. Seeding Section 7 sample data...");
                insertSampleData();
                log.info("Sample data seeded successfully.");
            } else {
                log.info("Database already contains data ({} branches found). Skipping seeding.", count);
            }
        } catch (Exception e) {
            log.error("Error during database initialization: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public synchronized void reinitialize() {
        log.warn("Reinitializing database: dropping all tables, recreating, and seeding fresh sample data...");
        try {
            dropAllTables();
            createTables();
            insertSampleData();
            log.info("Database successfully reinitialized!");
        } catch (Exception e) {
            log.error("Failed to reinitialize database: {}", e.getMessage(), e);
            throw new RuntimeException("Reinitialization failed: " + e.getMessage(), e);
        }
    }

    private void dropAllTables() {
        String[] tables = {
                "thanh_toan", "chi_tiet_don_hang", "don_hang", "ton_kho",
                "mon_an", "danh_muc", "khuyen_mai", "khach_hang",
                "tai_khoan", "nhan_vien", "chi_nhanh"
        };
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        } catch (Exception ignored) {}

        for (String tbl : tables) {
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + tbl);
        }

        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (Exception ignored) {}
    }

    private void createTables() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS chi_nhanh (
                maCN VARCHAR(10) PRIMARY KEY,
                tenCN VARCHAR(100) NOT NULL,
                diaChi VARCHAR(255) NOT NULL,
                soDienThoai VARCHAR(20) NOT NULL UNIQUE
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS nhan_vien (
                maNV VARCHAR(10) PRIMARY KEY,
                hoTen VARCHAR(100) NOT NULL,
                soDienThoai VARCHAR(20) NOT NULL UNIQUE,
                chucVu VARCHAR(20) NOT NULL,
                maCN VARCHAR(10) NOT NULL,
                FOREIGN KEY (maCN) REFERENCES chi_nhanh(maCN)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tai_khoan (
                maTK VARCHAR(10) PRIMARY KEY,
                maNV VARCHAR(10) NOT NULL UNIQUE,
                tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
                matKhauHash VARCHAR(255) NOT NULL,
                trangThai VARCHAR(20) NOT NULL,
                ngayTao DATETIME NOT NULL,
                lanDangNhapCuoi DATETIME,
                FOREIGN KEY (maNV) REFERENCES nhan_vien(maNV)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS khach_hang (
                maKH VARCHAR(10) PRIMARY KEY,
                hoTen VARCHAR(100) NOT NULL,
                soDienThoai VARCHAR(20) NOT NULL UNIQUE,
                email VARCHAR(100) UNIQUE,
                diaChi VARCHAR(255),
                ngayDangKy DATE NOT NULL
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS danh_muc (
                maDM VARCHAR(10) PRIMARY KEY,
                tenDM VARCHAR(100) NOT NULL UNIQUE,
                moTa VARCHAR(255)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS mon_an (
                maMon VARCHAR(10) PRIMARY KEY,
                tenMon VARCHAR(100) NOT NULL,
                giaBan DECIMAL(12,2) NOT NULL,
                moTa VARCHAR(255),
                trangThai VARCHAR(20) NOT NULL,
                maDM VARCHAR(10) NOT NULL,
                FOREIGN KEY (maDM) REFERENCES danh_muc(maDM)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ton_kho (
                maTon VARCHAR(10) PRIMARY KEY,
                maMon VARCHAR(10) NOT NULL,
                maCN VARCHAR(10) NOT NULL,
                soLuongTon INT NOT NULL,
                ngayCapNhat DATETIME NOT NULL,
                UNIQUE (maMon, maCN),
                FOREIGN KEY (maMon) REFERENCES mon_an(maMon),
                FOREIGN KEY (maCN) REFERENCES chi_nhanh(maCN)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS khuyen_mai (
                maKM VARCHAR(10) PRIMARY KEY,
                tenKM VARCHAR(150) NOT NULL,
                phanTramGiam INT NOT NULL,
                donToiThieu DECIMAL(12,2) NOT NULL,
                giamToiDa DECIMAL(12,2),
                ngayBatDau DATE NOT NULL,
                ngayKetThuc DATE NOT NULL,
                trangThai VARCHAR(20) NOT NULL
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS don_hang (
                maDH BIGINT PRIMARY KEY,
                ngayDat DATETIME NOT NULL,
                trangThai VARCHAR(20) NOT NULL,
                tienHang DECIMAL(12,2) NOT NULL,
                tienGiam DECIMAL(12,2) NOT NULL,
                tongTien DECIMAL(12,2) NOT NULL,
                maKH VARCHAR(10),
                maKM VARCHAR(10),
                maNV VARCHAR(10) NOT NULL,
                maCN VARCHAR(10) NOT NULL,
                FOREIGN KEY (maKH) REFERENCES khach_hang(maKH),
                FOREIGN KEY (maKM) REFERENCES khuyen_mai(maKM),
                FOREIGN KEY (maNV) REFERENCES nhan_vien(maNV),
                FOREIGN KEY (maCN) REFERENCES chi_nhanh(maCN)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS chi_tiet_don_hang (
                maCTDH BIGINT PRIMARY KEY,
                maDH BIGINT NOT NULL,
                maMon VARCHAR(10) NOT NULL,
                soLuong INT NOT NULL,
                donGia DECIMAL(12,2) NOT NULL,
                thanhTien DECIMAL(12,2) NOT NULL,
                UNIQUE (maDH, maMon),
                FOREIGN KEY (maDH) REFERENCES don_hang(maDH),
                FOREIGN KEY (maMon) REFERENCES mon_an(maMon)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS thanh_toan (
                maTT VARCHAR(10) PRIMARY KEY,
                maDH BIGINT NOT NULL,
                soTien DECIMAL(12,2) NOT NULL,
                phuongThuc VARCHAR(20) NOT NULL,
                trangThai VARCHAR(20) NOT NULL,
                ngayThanhToan DATETIME,
                FOREIGN KEY (maDH) REFERENCES don_hang(maDH)
            )
        """);
    }

    private void insertSampleData() {
        // 7.1 ChiNhanh
        jdbcTemplate.execute("""
            INSERT INTO chi_nhanh (maCN, tenCN, diaChi, soDienThoai) VALUES
            ('CN00', 'FastFood Tru So', '88 Nguyen Thi Minh Khai, TP.HCM', '0901000000'),
            ('CN01', 'FastFood Quan 1', '25 Nguyen Hue, TP.HCM', '0901000001'),
            ('CN02', 'FastFood Quan 3', '120 Vo Van Tan, TP.HCM', '0901000002'),
            ('CN03', 'FastFood Thu Duc', '45 Vo Van Ngan, TP.HCM', '0901000003')
        """);

        // 7.2 NhanVien
        jdbcTemplate.execute("""
            INSERT INTO nhan_vien (maNV, hoTen, soDienThoai, chucVu, maCN) VALUES
            ('NV01', 'Nguyen Van An', '0911000001', 'QUAN_LY', 'CN01'),
            ('NV02', 'Tran Minh Binh', '0911000002', 'NHAN_VIEN', 'CN01'),
            ('NV03', 'Le Hoang Nam', '0911000003', 'NHAN_VIEN', 'CN02'),
            ('NV04', 'Pham Thi Lan', '0911000004', 'NHAN_VIEN', 'CN02'),
            ('NV05', 'Vo Minh Khang', '0911000005', 'NHAN_VIEN', 'CN03'),
            ('NV06', 'Dang Quoc Huy', '0911000006', 'THU_NGAN', 'CN03'),
            ('NV07', 'Ly Gia Hung', '0911000007', 'ADMIN', 'CN00'),
            ('NV08', 'Ngo Thanh Thao', '0911000008', 'THU_NGAN', 'CN01')
        """);

        // 7.11 TaiKhoan
        jdbcTemplate.execute("""
            INSERT INTO tai_khoan (maTK, maNV, tenDangNhap, matKhauHash, trangThai, ngayTao, lanDangNhapCuoi) VALUES
            ('TK01', 'NV07', 'admin01', '$2a$10$DEMO_HASH_TK01', 'ACTIVE', '2026-01-01 08:00:00', '2026-06-30 08:30:00'),
            ('TK02', 'NV01', 'nv01', '$2a$10$DEMO_HASH_TK02', 'ACTIVE', '2026-01-01 09:00:00', '2026-06-30 08:45:00'),
            ('TK03', 'NV02', 'nv02', '$2a$10$DEMO_HASH_TK03', 'ACTIVE', '2026-01-01 09:30:00', '2026-06-30 08:50:00'),
            ('TK04', 'NV03', 'nv03', '$2a$10$DEMO_HASH_TK04', 'ACTIVE', '2026-01-02 08:30:00', '2026-06-30 09:00:00'),
            ('TK05', 'NV04', 'nv04', '$2a$10$DEMO_HASH_TK05', 'LOCKED', '2026-01-02 08:45:00', '2026-06-20 17:50:00'),
            ('TK06', 'NV05', 'nv05', '$2a$10$DEMO_HASH_TK06', 'ACTIVE', '2026-01-03 08:30:00', '2026-06-29 11:00:00'),
            ('TK07', 'NV08', 'nv08', '$2a$10$DEMO_HASH_TK07', 'ACTIVE', '2026-06-29 09:00:00', NULL)
        """);

        // 7.3 KhachHang
        jdbcTemplate.execute("""
            INSERT INTO khach_hang (maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy) VALUES
            ('KH001', 'Nguyen Minh Anh', '0921000001', 'anh@gmail.com', 'TP.HCM', '2025-12-20'),
            ('KH002', 'Tran Quoc Bao', '0921000002', 'bao@gmail.com', 'TP.HCM', '2025-12-28'),
            ('KH003', 'Le Thi Mai', '0921000003', 'mai@gmail.com', 'Binh Duong', '2026-01-02'),
            ('KH004', 'Pham Hoang Nam', '0921000004', 'nam@gmail.com', 'TP.HCM', '2026-01-15'),
            ('KH005', 'Vo Thanh Ha', '0921000005', 'ha@gmail.com', 'Dong Nai', '2026-01-25'),
            ('KH006', 'Nguyen Duc Long', '0921000006', 'long@gmail.com', 'TP.HCM', '2026-02-01'),
            ('KH007', 'Dang Minh Chau', '0921000007', 'chau@gmail.com', 'Long An', '2026-02-20'),
            ('KH008', 'Bui Quoc Viet', '0921000008', 'viet@gmail.com', 'TP.HCM', '2026-03-01'),
            ('KH009', 'Do Minh Duc', '0921000009', 'duc@gmail.com', 'Can Tho', '2026-03-10'),
            ('KH010', 'Hoang Thu Trang', '0921000010', 'trang@gmail.com', 'TP.HCM', '2026-04-01')
        """);

        // 7.4 DanhMuc
        jdbcTemplate.execute("""
            INSERT INTO danh_muc (maDM, tenDM, moTa) VALUES
            ('DM01', 'Burger', NULL),
            ('DM02', 'Ga ran', NULL),
            ('DM03', 'Pizza', NULL),
            ('DM04', 'Do an phu', NULL),
            ('DM05', 'Nuoc uong', NULL),
            ('DM06', 'Combo', NULL),
            ('DM07', 'Mi Y', NULL)
        """);

        // 7.5 MonAn
        jdbcTemplate.execute("""
            INSERT INTO mon_an (maMon, tenMon, giaBan, maDM, trangThai) VALUES
            ('M001', 'Burger Bo', 50000, 'DM01', 'DANG_BAN'),
            ('M002', 'Burger Ga', 40000, 'DM01', 'DANG_BAN'),
            ('M003', 'Burger Pho Mai', 55000, 'DM01', 'DANG_BAN'),
            ('M004', 'Ga Ran 1 Mieng', 35000, 'DM02', 'DANG_BAN'),
            ('M005', 'Ga Ran 2 Mieng', 65000, 'DM02', 'DANG_BAN'),
            ('M006', 'Ga Ran 4 Mieng', 120000, 'DM02', 'DANG_BAN'),
            ('M007', 'Khoai Tay Chien', 30000, 'DM04', 'DANG_BAN'),
            ('M008', 'Khoai Lac', 35000, 'DM04', 'DANG_BAN'),
            ('M009', 'Pizza Hai San', 120000, 'DM03', 'DANG_BAN'),
            ('M010', 'Pizza Bo', 110000, 'DM03', 'DANG_BAN'),
            ('M011', 'Coca Cola', 15000, 'DM05', 'DANG_BAN'),
            ('M012', 'Pepsi', 15000, 'DM05', 'DANG_BAN'),
            ('M013', 'Tra Dao', 25000, 'DM05', 'DANG_BAN'),
            ('M014', 'Combo Burger', 85000, 'DM06', 'DANG_BAN'),
            ('M015', 'Combo Ga', 95000, 'DM06', 'DANG_BAN'),
            ('M016', 'Mi Y Bo', 75000, 'DM07', 'DANG_BAN'),
            ('M017', 'Burger Ca', 50000, 'DM01', 'NGUNG_BAN'),
            ('M018', 'Pizza Rau Cu', 100000, 'DM03', 'NGUNG_BAN')
        """);

        // 7.6 TonKho
        jdbcTemplate.execute("""
            INSERT INTO ton_kho (maTon, maMon, maCN, soLuongTon, ngayCapNhat) VALUES
            ('T001', 'M001', 'CN01', 50, '2026-06-30 08:00:00'),
            ('T002', 'M002', 'CN01', 35, '2026-06-30 08:00:00'),
            ('T003', 'M003', 'CN01', 8, '2026-06-30 08:00:00'),
            ('T004', 'M004', 'CN01', 70, '2026-06-30 08:00:00'),
            ('T005', 'M005', 'CN01', 5, '2026-06-30 08:00:00'),
            ('T006', 'M006', 'CN01', 20, '2026-06-30 08:00:00'),
            ('T007', 'M007', 'CN01', 45, '2026-06-30 08:00:00'),
            ('T008', 'M008', 'CN01', 7, '2026-06-30 08:00:00'),
            ('T009', 'M009', 'CN01', 15, '2026-06-30 08:00:00'),
            ('T010', 'M010', 'CN01', 4, '2026-06-30 08:00:00'),
            ('T011', 'M011', 'CN01', 100, '2026-06-30 08:00:00'),
            ('T012', 'M012', 'CN01', 80, '2026-06-30 08:00:00'),
            ('T013', 'M013', 'CN01', 6, '2026-06-30 08:00:00'),
            ('T014', 'M014', 'CN01', 30, '2026-06-30 08:00:00'),
            ('T015', 'M015', 'CN01', 9, '2026-06-30 08:00:00'),
            ('T016', 'M016', 'CN01', 25, '2026-06-30 08:00:00'),
            ('T017', 'M017', 'CN01', 12, '2026-06-30 08:00:00'),
            ('T018', 'M018', 'CN01', 2, '2026-06-30 08:00:00'),
            ('T019', 'M001', 'CN02', 40, '2026-06-30 08:00:00'),
            ('T020', 'M002', 'CN02', 12, '2026-06-30 08:00:00'),
            ('T021', 'M003', 'CN02', 20, '2026-06-30 08:00:00'),
            ('T022', 'M004', 'CN02', 55, '2026-06-30 08:00:00'),
            ('T023', 'M005', 'CN02', 25, '2026-06-30 08:00:00'),
            ('T024', 'M006', 'CN02', 9, '2026-06-30 08:00:00'),
            ('T025', 'M007', 'CN02', 30, '2026-06-30 08:00:00'),
            ('T026', 'M008', 'CN02', 3, '2026-06-30 08:00:00'),
            ('T027', 'M009', 'CN02', 18, '2026-06-30 08:00:00'),
            ('T028', 'M010', 'CN02', 14, '2026-06-30 08:00:00'),
            ('T029', 'M011', 'CN02', 60, '2026-06-30 08:00:00'),
            ('T030', 'M012', 'CN02', 90, '2026-06-30 08:00:00'),
            ('T031', 'M013', 'CN02', 22, '2026-06-30 08:00:00'),
            ('T032', 'M014', 'CN02', 11, '2026-06-30 08:00:00'),
            ('T033', 'M015', 'CN02', 6, '2026-06-30 08:00:00'),
            ('T034', 'M016', 'CN02', 8, '2026-06-30 08:00:00'),
            ('T035', 'M017', 'CN02', 0, '2026-06-30 08:00:00'),
            ('T036', 'M018', 'CN02', 5, '2026-06-30 08:00:00'),
            ('T037', 'M001', 'CN03', 30, '2026-06-30 08:00:00'),
            ('T038', 'M002', 'CN03', 8, '2026-06-30 08:00:00'),
            ('T039', 'M003', 'CN03', 25, '2026-06-30 08:00:00'),
            ('T040', 'M004', 'CN03', 40, '2026-06-30 08:00:00'),
            ('T041', 'M005', 'CN03', 10, '2026-06-30 08:00:00'),
            ('T042', 'M006', 'CN03', 15, '2026-06-30 08:00:00'),
            ('T043', 'M007', 'CN03', 6, '2026-06-30 08:00:00'),
            ('T044', 'M008', 'CN03', 22, '2026-06-30 08:00:00'),
            ('T045', 'M009', 'CN03', 12, '2026-06-30 08:00:00'),
            ('T046', 'M010', 'CN03', 9, '2026-06-30 08:00:00'),
            ('T047', 'M011', 'CN03', 70, '2026-06-30 08:00:00'),
            ('T048', 'M012', 'CN03', 50, '2026-06-30 08:00:00'),
            ('T049', 'M013', 'CN03', 18, '2026-06-30 08:00:00'),
            ('T050', 'M014', 'CN03', 16, '2026-06-30 08:00:00'),
            ('T051', 'M015', 'CN03', 20, '2026-06-30 08:00:00'),
            ('T052', 'M016', 'CN03', 4, '2026-06-30 08:00:00'),
            ('T053', 'M017', 'CN03', 3, '2026-06-30 08:00:00'),
            ('T054', 'M018', 'CN03', 0, '2026-06-30 08:00:00')
        """);

        // 7.7 KhuyenMai
        jdbcTemplate.execute("""
            INSERT INTO khuyen_mai (maKM, tenKM, phanTramGiam, donToiThieu, giamToiDa, ngayBatDau, ngayKetThuc, trangThai) VALUES
            ('KM01', 'Giam 10% don tu 150.000', 10, 150000, 30000, '2026-02-01', '2026-02-28', 'HET_HAN'),
            ('KM02', 'Uu dai thang 3 giam 15%', 15, 100000, 40000, '2026-03-01', '2026-03-31', 'HET_HAN'),
            ('KM03', 'He soi dong giam 10%', 10, 100000, 20000, '2026-06-01', '2026-08-31', 'DANG_AP_DUNG'),
            ('KM04', 'Thanh vien moi giam 20%', 20, 0, NULL, '2026-01-01', '2026-12-31', 'TAM_DUNG')
        """);

        // 7.8 DonHang
        jdbcTemplate.execute("""
            INSERT INTO don_hang (maDH, ngayDat, trangThai, tienHang, tienGiam, tongTien, maKM, maKH, maNV, maCN) VALUES
            (1001, '2026-01-05 10:15:00', 'DA_HOAN_THANH', 150000, 0, 150000, NULL, 'KH001', 'NV01', 'CN01'),
            (1002, '2026-01-12 12:20:00', 'DA_HOAN_THANH', 165000, 0, 165000, NULL, 'KH002', 'NV02', 'CN01'),
            (1003, '2026-01-20 18:30:00', 'DA_HOAN_THANH', 145000, 0, 145000, NULL, 'KH003', 'NV03', 'CN02'),
            (1004, '2026-02-02 11:10:00', 'DA_HOAN_THANH', 110000, 0, 110000, NULL, 'KH001', 'NV02', 'CN01'),
            (1005, '2026-02-05 12:00:00', 'DA_HOAN_THANH', 150000, 0, 150000, NULL, 'KH004', 'NV03', 'CN02'),
            (1006, '2026-02-08 19:10:00', 'DA_HOAN_THANH', 185000, 18500, 166500, 'KM01', 'KH005', 'NV04', 'CN02'),
            (1007, '2026-02-12 13:20:00', 'DA_HOAN_THANH', 205000, 20500, 184500, 'KM01', 'KH001', 'NV01', 'CN01'),
            (1008, '2026-02-15 18:40:00', 'DA_HOAN_THANH', 155000, 0, 155000, NULL, 'KH006', 'NV02', 'CN01'),
            (1009, '2026-02-20 11:30:00', 'DA_HOAN_THANH', 140000, 0, 140000, NULL, 'KH002', 'NV03', 'CN02'),
            (1010, '2026-02-25 20:00:00', 'DA_HOAN_THANH', 160000, 16000, 144000, 'KM01', 'KH004', 'NV04', 'CN02'),
            (1011, '2026-03-03 10:00:00', 'DA_HOAN_THANH', 195000, 29250, 165750, 'KM02', 'KH007', 'NV05', 'CN03'),
            (1012, '2026-03-08 12:30:00', 'DA_HOAN_THANH', 175000, 0, 175000, NULL, 'KH001', 'NV01', 'CN01'),
            (1013, '2026-03-15 19:00:00', 'DA_HOAN_THANH', 160000, 24000, 136000, 'KM02', 'KH008', 'NV05', 'CN03'),
            (1014, '2026-03-20 14:15:00', 'DA_HOAN_THANH', 135000, 0, 135000, NULL, 'KH002', 'NV02', 'CN01'),
            (1015, '2026-03-25 18:00:00', 'DA_HOAN_THANH', 190000, 28500, 161500, 'KM02', 'KH004', 'NV04', 'CN02'),
            (1016, '2026-04-02 11:00:00', 'DA_HOAN_THANH', 150000, 0, 150000, NULL, 'KH001', 'NV01', 'CN01'),
            (1017, '2026-04-10 13:20:00', 'DANG_CHUAN_BI', 120000, 0, 120000, NULL, 'KH005', 'NV03', 'CN02'),
            (1018, '2026-04-15 19:30:00', 'DA_HUY', 115000, 0, 115000, NULL, 'KH006', 'NV02', 'CN01'),
            (1019, '2026-04-22 12:00:00', 'DA_HOAN_THANH', 200000, 0, 200000, NULL, 'KH008', 'NV05', 'CN03'),
            (1020, '2026-05-03 10:40:00', 'DA_HOAN_THANH', 155000, 0, 155000, NULL, 'KH002', 'NV02', 'CN01'),
            (1021, '2026-05-12 18:20:00', 'DA_HOAN_THANH', 205000, 0, 205000, NULL, 'KH004', 'NV04', 'CN02'),
            (1022, '2026-05-20 13:00:00', 'DA_HOAN_THANH', 270000, 0, 270000, NULL, 'KH001', 'NV01', 'CN01'),
            (1023, '2026-06-01 11:30:00', 'DA_HOAN_THANH', 170000, 17000, 153000, 'KM03', 'KH003', 'NV03', 'CN02'),
            (1024, '2026-06-10 19:00:00', 'DA_HOAN_THANH', 220000, 20000, 200000, 'KM03', 'KH006', 'NV02', 'CN01'),
            (1025, '2026-06-15 12:15:00', 'DA_HOAN_THANH', 210000, 20000, 190000, 'KM03', 'KH008', 'NV05', 'CN03'),
            (1026, '2026-06-20 18:00:00', 'SAN_SANG', 100000, 10000, 90000, 'KM03', 'KH005', 'NV04', 'CN02'),
            (1027, '2026-06-22 11:45:00', 'DA_XAC_NHAN', 135000, 13500, 121500, 'KM03', 'KH007', 'NV05', 'CN03'),
            (1028, '2026-06-25 12:10:00', 'CHO_XAC_NHAN', 80000, 0, 80000, NULL, NULL, 'NV03', 'CN02')
        """);

        // 7.9 ChiTietDonHang
        jdbcTemplate.execute("""
            INSERT INTO chi_tiet_don_hang (maCTDH, maDH, maMon, soLuong, donGia, thanhTien) VALUES
            (1, 1001, 'M001', 2, 45000, 90000),
            (2, 1001, 'M007', 1, 30000, 30000),
            (3, 1001, 'M011', 2, 15000, 30000),
            (4, 1002, 'M004', 2, 35000, 70000),
            (5, 1002, 'M005', 1, 65000, 65000),
            (6, 1002, 'M012', 2, 15000, 30000),
            (7, 1003, 'M009', 1, 120000, 120000),
            (8, 1003, 'M013', 1, 25000, 25000),
            (9, 1004, 'M002', 2, 40000, 80000),
            (10, 1004, 'M007', 1, 30000, 30000),
            (11, 1005, 'M006', 1, 120000, 120000),
            (12, 1005, 'M011', 2, 15000, 30000),
            (13, 1006, 'M014', 2, 85000, 170000),
            (14, 1006, 'M012', 1, 15000, 15000),
            (15, 1007, 'M001', 3, 45000, 135000),
            (16, 1007, 'M008', 2, 35000, 70000),
            (17, 1008, 'M015', 1, 95000, 95000),
            (18, 1008, 'M007', 2, 30000, 60000),
            (19, 1009, 'M003', 2, 55000, 110000),
            (20, 1009, 'M011', 2, 15000, 30000),
            (21, 1010, 'M010', 1, 110000, 110000),
            (22, 1010, 'M013', 2, 25000, 50000),
            (23, 1011, 'M004', 3, 35000, 105000),
            (24, 1011, 'M007', 3, 30000, 90000),
            (25, 1012, 'M001', 2, 45000, 90000),
            (26, 1012, 'M014', 1, 85000, 85000),
            (27, 1013, 'M005', 2, 65000, 130000),
            (28, 1013, 'M012', 2, 15000, 30000),
            (29, 1014, 'M002', 3, 40000, 120000),
            (30, 1014, 'M011', 1, 15000, 15000),
            (31, 1015, 'M006', 1, 120000, 120000),
            (32, 1015, 'M008', 2, 35000, 70000),
            (33, 1016, 'M009', 1, 120000, 120000),
            (34, 1016, 'M011', 2, 15000, 30000),
            (35, 1017, 'M015', 1, 95000, 95000),
            (36, 1017, 'M013', 1, 25000, 25000),
            (37, 1018, 'M005', 1, 65000, 65000),
            (38, 1018, 'M017', 1, 50000, 50000),
            (39, 1019, 'M014', 2, 85000, 170000),
            (40, 1019, 'M012', 2, 15000, 30000),
            (41, 1020, 'M001', 2, 45000, 90000),
            (42, 1020, 'M005', 1, 65000, 65000),
            (43, 1021, 'M003', 1, 55000, 55000),
            (44, 1021, 'M016', 2, 75000, 150000),
            (45, 1022, 'M006', 2, 120000, 240000),
            (46, 1022, 'M007', 1, 30000, 30000),
            (47, 1023, 'M009', 1, 120000, 120000),
            (48, 1023, 'M013', 2, 25000, 50000),
            (49, 1024, 'M015', 2, 95000, 190000),
            (50, 1024, 'M011', 2, 15000, 30000),
            (51, 1025, 'M001', 2, 50000, 100000),
            (52, 1025, 'M010', 1, 110000, 110000),
            (53, 1026, 'M016', 1, 75000, 75000),
            (54, 1026, 'M013', 1, 25000, 25000),
            (55, 1027, 'M006', 1, 120000, 120000),
            (56, 1027, 'M012', 1, 15000, 15000),
            (57, 1028, 'M002', 2, 40000, 80000)
        """);

        // 7.10 ThanhToan
        jdbcTemplate.execute("""
            INSERT INTO thanh_toan (maTT, maDH, soTien, ngayThanhToan, phuongThuc, trangThai) VALUES
            ('TT001', 1001, 150000, '2026-01-05 10:20:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT002', 1002, 165000, '2026-01-12 12:25:00', 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
            ('TT003', 1003, 145000, '2026-01-20 18:35:00', 'THE', 'DA_THANH_TOAN'),
            ('TT004', 1004, 110000, '2026-02-02 11:15:00', 'VI_DIEN_TU', 'DA_THANH_TOAN'),
            ('TT005', 1005, 150000, '2026-02-05 12:05:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT006', 1006, 166500, '2026-02-08 19:15:00', 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
            ('TT007', 1007, 184500, '2026-02-12 13:25:00', 'THE', 'DA_THANH_TOAN'),
            ('TT008', 1008, 155000, '2026-02-15 18:45:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT009', 1009, 140000, '2026-02-20 11:35:00', 'VI_DIEN_TU', 'DA_THANH_TOAN'),
            ('TT010', 1010, 144000, '2026-02-25 20:05:00', 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
            ('TT011', 1011, 165750, '2026-03-03 10:05:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT012', 1012, 175000, '2026-03-08 12:35:00', 'THE', 'DA_THANH_TOAN'),
            ('TT013', 1013, 136000, '2026-03-15 19:05:00', 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
            ('TT014', 1014, 135000, '2026-03-20 14:20:00', 'VI_DIEN_TU', 'DA_THANH_TOAN'),
            ('TT015', 1015, 161500, '2026-03-25 18:03:00', 'THE', 'THAT_BAI'),
            ('TT016', 1015, 161500, '2026-03-25 18:06:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT017', 1016, 150000, '2026-04-02 11:05:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT018', 1017, 120000, NULL, 'TIEN_MAT', 'CHO_THANH_TOAN'),
            ('TT019', 1018, 115000, '2026-04-15 19:35:00', 'VI_DIEN_TU', 'DA_HOAN_TIEN'),
            ('TT020', 1019, 200000, '2026-04-22 12:05:00', 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
            ('TT021', 1020, 155000, '2026-05-03 10:45:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT022', 1021, 205000, '2026-05-12 18:25:00', 'THE', 'DA_THANH_TOAN'),
            ('TT023', 1022, 270000, '2026-05-20 13:05:00', 'VI_DIEN_TU', 'DA_THANH_TOAN'),
            ('TT024', 1023, 153000, '2026-06-01 11:35:00', 'CHUYEN_KHOAN', 'DA_THANH_TOAN'),
            ('TT025', 1024, 200000, '2026-06-10 19:05:00', 'THE', 'DA_THANH_TOAN'),
            ('TT026', 1025, 190000, '2026-06-15 12:20:00', 'TIEN_MAT', 'DA_THANH_TOAN'),
            ('TT027', 1026, 90000, NULL, 'TIEN_MAT', 'CHO_THANH_TOAN'),
            ('TT028', 1027, 121500, '2026-06-22 11:46:00', 'VI_DIEN_TU', 'DA_THANH_TOAN')
        """);
    }
}
