package com.nhom17;

import com.nhom17.database.DatabaseInitializer;
import com.nhom17.enums.*;
import com.nhom17.model.*;
import com.nhom17.service.*;
import com.nhom17.util.InputUtil;
import com.nhom17.util.TablePrinter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final ChiNhanhService CHI_NHANH_SERVICE = new ChiNhanhService();
    private static final DanhMucService DANH_MUC_SERVICE = new DanhMucService();
    private static final MonAnService MON_AN_SERVICE = new MonAnService();
    private static final KhachHangService KHACH_HANG_SERVICE = new KhachHangService();
    private static final NhanVienService NHAN_VIEN_SERVICE = new NhanVienService();
    private static final TaiKhoanService TAI_KHOAN_SERVICE = new TaiKhoanService();
    private static final TonKhoService TON_KHO_SERVICE = new TonKhoService();
    private static final KhuyenMaiService KHUYEN_MAI_SERVICE = new KhuyenMaiService();
    private static final DonHangService DON_HANG_SERVICE = new DonHangService();
    private static final ChiTietDonHangService CHI_TIET_DON_HANG_SERVICE = new ChiTietDonHangService();
    private static final ThanhToanService THANH_TOAN_SERVICE = new ThanhToanService();
    private static final QueryLambdaService QUERY_LAMBDA_SERVICE = new QueryLambdaService();

    public static void main(String[] args) {
        System.out.println("=".repeat(75));
        System.out.println("  FAST FOOD MANAGEMENT SYSTEM (MIS) - NHOM 17");
        System.out.println("  Thanh vien: Nguyen Mai Thien Qui - Nguyen Quoc Tien - Choi Minh Van");
        System.out.println("=".repeat(75));

        //DatabaseInitializer.initialize();

        int choice;
        do {
            showMainMenu();
            choice = InputUtil.readInt(SCANNER, "Chon chuc nang [0-5]: ");

            switch (choice) {
                case 1 -> menuCRUD();
                case 2 -> menu30TruyVan();
                case 3 -> menuStreamApiLambda();
                case 4 -> reinitializeDatabase();
                case 5 -> menuBanHangDemo();
                case 0 -> System.out.println("\nCam on ban da su dung he thong MIS Quan ly Cua Hang Do An Nhanh!");
                default -> System.out.println("(!) Lua chon khong hop le, vui long chon lai.");
            }
        } while (choice != 0);
    }


    private static void menuBanHangDemo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   DEMO: NHAN VIEN BAN HANG (Khach vang lai)");
        System.out.println("=".repeat(60));

        // BUOC 1: DANG NHAP
        System.out.println("\n[BUOC 1] DANG NHAP HE THONG");
        System.out.print("  Ten dang nhap: ");
        String username = SCANNER.nextLine().trim();
        System.out.print("  Mat khau     : ");
        String password = SCANNER.nextLine().trim();

        var taiKhoan = TAI_KHOAN_SERVICE.getByUsername(username);
        if (taiKhoan.isEmpty()) {
            System.out.println("(!) Ten dang nhap khong ton tai.");
            return;
        }
        var tk = taiKhoan.get();
        if (!tk.getMatKhauHash().equals(password)) {
            System.out.println("(!) Mat khau sai.");
            return;
        }
        if (tk.getTrangThai() == com.nhom17.enums.TrangThaiTaiKhoan.LOCKED) {
            System.out.println("(!) Tai khoan da bi khoa.");
            return;
        }

        var nhanVien = NHAN_VIEN_SERVICE.getAll().stream()
                .filter(nv -> nv.getMaNV().equals(tk.getMaNV()))
                .findFirst().orElse(null);
        String tenNV = nhanVien != null ? nhanVien.getHoTen() : username;
        String maNV  = tk.getMaNV();
        String maCN  = nhanVien != null ? nhanVien.getMaCN() : "CN01";
        System.out.println("  >> Xin chao, " + tenNV + "! Chi nhanh: " + maCN);

        // BUOC 2: CHON KHACH HANG
        System.out.println("\n[BUOC 2] THONG TIN KHACH HANG");
        System.out.println("  1. Khach hang co tai khoan");
        System.out.println("  2. Khach vang lai (khong can dang ky)");
        int khachChoice = InputUtil.readInt(SCANNER, "  Chon [1-2]: ");
        String maKHDon = null;
        String tenKHDon = "(Khach vang lai)";
        if (khachChoice == 1) {
            var danhSachKH = KHACH_HANG_SERVICE.getAll();
            System.out.printf("  %-8s | %-20s | %-15s%n", "Ma KH", "Ho Ten", "So Dien Thoai");
            System.out.println("  " + "-".repeat(50));
            danhSachKH.forEach(kh -> System.out.printf("  %-8s | %-20s | %-15s%n",
                    kh.getMaKH(), kh.getHoTen(), kh.getSoDienThoai() != null ? kh.getSoDienThoai() : ""));
            System.out.print("  Nhap ma khach hang: ");
            String maKHInput = SCANNER.nextLine().trim();
            var khOpt = KHACH_HANG_SERVICE.getAll().stream()
                    .filter(kh -> kh.getMaKH().equalsIgnoreCase(maKHInput))
                    .findFirst();
            if (khOpt.isPresent()) {
                maKHDon = khOpt.get().getMaKH();
                tenKHDon = khOpt.get().getHoTen();
                System.out.println("  >> Khach hang: " + tenKHDon + " (" + maKHDon + ")");
            } else {
                System.out.println("  (!) Khong tim thay khach hang, chuyen sang khach vang lai.");
            }
        } else {
            System.out.println("  >> Khach vang lai - khong luu thong tin.");
        }

        // BUOC 3: XEM THUC DON
        System.out.println("\n[BUOC 3] THUC DON HIEN TAI");
        System.out.printf("  %-6s | %-25s | %-12s%n", "Ma Mon", "Ten Mon", "Gia Ban");
        System.out.println("  " + "-".repeat(50));
        MON_AN_SERVICE.getAll().stream()
                .filter(m -> m.getTrangThai() == TrangThaiMon.DANG_BAN)
                .forEach(m -> System.out.printf("  %-6s | %-25s | %,12.0f%n",
                        m.getMaMon(), m.getTenMon(), m.getGiaBan().doubleValue()));

        // BUOC 4: CHON MON
        System.out.println("\n[BUOC 4] CHON MON CHO KHACH");
        List<ChiTietDonHang> gioHang = new ArrayList<>();
        while (true) {
            System.out.print("  Nhap ma mon (Enter de ket thuc): ");
            String maMon = SCANNER.nextLine().trim();
            if (maMon.isEmpty()) break;
            var mon = MON_AN_SERVICE.getAll().stream()
                    .filter(m -> m.getMaMon().equalsIgnoreCase(maMon) && m.getTrangThai() == TrangThaiMon.DANG_BAN)
                    .findFirst();
            if (mon.isEmpty()) {
                System.out.println("  (!) Mon khong ton tai hoac ngung ban.");
                continue;
            }
            int sl = InputUtil.readInt(SCANNER, "  So luong: ");
            if (sl <= 0) { System.out.println("  (!) So luong phai > 0."); continue; }
            gioHang.add(new ChiTietDonHang(null, null, mon.get().getMaMon(), mon.get().getTenMon(), sl, mon.get().getGiaBan(), mon.get().getGiaBan().multiply(java.math.BigDecimal.valueOf(sl))));
            System.out.printf("  >> Da them: %s x%d = %,.0f VND%n", mon.get().getTenMon(), sl, mon.get().getGiaBan().multiply(java.math.BigDecimal.valueOf(sl)).doubleValue());
        }
        if (gioHang.isEmpty()) {
            System.out.println("(!) Gio hang trong. Huy giao dich.");
            return;
        }

        // BUOC 5: XEM HOA DON TAM
        java.math.BigDecimal tienHang = gioHang.stream()
                .map(ct -> ct.getDonGia().multiply(java.math.BigDecimal.valueOf(ct.getSoLuong())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        System.out.println("\n[BUOC 5] HOA DON TAM");
        System.out.printf("  Khach hang  : %s%n", tenKHDon);
        System.out.printf("  Nhan vien   : %s | Chi nhanh: %s%n", tenNV, maCN);
        System.out.printf("  %-25s | %-5s | %-12s | %-12s%n", "Ten Mon", "SL", "Don Gia", "Thanh Tien");
        System.out.println("  " + "-".repeat(60));
        gioHang.forEach(ct -> System.out.printf("  %-25s | %-5d | %,12.0f | %,12.0f%n",
                ct.getTenMon(), ct.getSoLuong(), ct.getDonGia().doubleValue(),
                ct.getDonGia().multiply(java.math.BigDecimal.valueOf(ct.getSoLuong())).doubleValue()));
        System.out.println("  " + "-".repeat(60));
        System.out.printf("  Tien hang: %,.0f VND%n", tienHang.doubleValue());

        // BUOC 6: KHUYEN MAI
        System.out.println("\n[BUOC 6] AP DUNG KHUYEN MAI");
        var activeKM = KHUYEN_MAI_SERVICE.getAll().stream()
                .filter(km -> km.getTrangThai() == com.nhom17.enums.TrangThaiKhuyenMai.DANG_AP_DUNG)
                .filter(km -> tienHang.compareTo(km.getDonToiThieu()) >= 0)
                .toList();
        java.math.BigDecimal tienGiam = java.math.BigDecimal.ZERO;
        String maKM = null;
        if (activeKM.isEmpty()) {
            System.out.println("  Khong co khuyen mai phu hop.");
        } else {
            System.out.println("  Cac khuyen mai co the ap dung:");
            activeKM.forEach(km -> System.out.printf("  [%s] %s - Giam %d%%, don toi thieu %,.0f%n",
                    km.getMaKM(), km.getTenKM(), km.getPhanTramGiam(), km.getDonToiThieu().doubleValue()));
            System.out.print("  Nhap ma khuyen mai (Enter de bo qua): ");
            String maKMInput = SCANNER.nextLine().trim();
            if (!maKMInput.isEmpty()) {
                var kmOpt = activeKM.stream().filter(km -> km.getMaKM().equalsIgnoreCase(maKMInput)).findFirst();
                if (kmOpt.isPresent()) {
                    var km = kmOpt.get();
                    tienGiam = tienHang.multiply(java.math.BigDecimal.valueOf(km.getPhanTramGiam())).divide(java.math.BigDecimal.valueOf(100));
                    if (km.getGiamToiDa() != null && tienGiam.compareTo(km.getGiamToiDa()) > 0)
                        tienGiam = km.getGiamToiDa();
                    maKM = km.getMaKM();
                    System.out.printf("  >> Ap dung [%s]: giam %,.0f VND%n", km.getTenKM(), tienGiam.doubleValue());
                } else {
                    System.out.println("  (!) Ma khuyen mai khong hop le, bo qua.");
                }
            }
        }
        java.math.BigDecimal tongTien = tienHang.subtract(tienGiam);
        System.out.printf("  Tong tien phai tra: %,.0f VND%n", tongTien.doubleValue());

        // BUOC 7: THANH TOAN
        System.out.println("\n[BUOC 7] PHUONG THUC THANH TOAN");
        System.out.println("  1. TIEN_MAT  2. CHUYEN_KHOAN  3. VI_DIEN_TU  4. THE");
        int ptChoice = InputUtil.readInt(SCANNER, "  Chon phuong thuc [1-4]: ");
        PhuongThucThanhToan phuongThuc = switch (ptChoice) {
            case 2 -> PhuongThucThanhToan.CHUYEN_KHOAN;
            case 3 -> PhuongThucThanhToan.VI_DIEN_TU;
            case 4 -> PhuongThucThanhToan.THE;
            default -> PhuongThucThanhToan.TIEN_MAT;
        };

        // BUOC 8: LUU VAO DATABASE
        long newMaDH = DON_HANG_SERVICE.getAll().stream()
                .mapToLong(dh -> dh.getMaDH()).max().orElse(1000L) + 1;
        String newMaTT = "TT" + String.format("%03d", THANH_TOAN_SERVICE.getAll().size() + 1);

        DonHang donHang = new DonHang(newMaDH, java.time.LocalDateTime.now(),
                TrangThaiDonHang.DA_HOAN_THANH, tienHang, tienGiam, tongTien,
                maKHDon, maKM, maNV, maCN);

        List<ChiTietDonHang> chiTiets = new ArrayList<>();
        for (int i = 0; i < gioHang.size(); i++) {
            var ct = gioHang.get(i);
            chiTiets.add(new ChiTietDonHang(
                    (long)(newMaDH * 100 + i + 1), newMaDH,
                    ct.getMaMon(), ct.getSoLuong(), ct.getDonGia()));
        }

        ThanhToan thanhToan = new ThanhToan(newMaTT, newMaDH, tongTien,
                phuongThuc, TrangThaiThanhToan.DA_THANH_TOAN, java.time.LocalDateTime.now());

        boolean ok = DON_HANG_SERVICE.addDonHang(donHang, chiTiets, List.of(thanhToan));

        // KET QUA
        System.out.println("\n" + "=".repeat(60));
        if (ok) {
            System.out.println("   THANH TOAN THANH CONG!");
            System.out.printf("   Ma don hang : #%d%n", newMaDH);
            System.out.printf("   Khach hang  : %s%n", tenKHDon);
            System.out.printf("   Nhan vien   : %s%n", tenNV);
            System.out.printf("   Tien hang   : %,.0f VND%n", tienHang.doubleValue());
            System.out.printf("   Tien giam   : %,.0f VND%n", tienGiam.doubleValue());
            System.out.printf("   Tong thanh toan: %,.0f VND%n", tongTien.doubleValue());
            System.out.printf("   Phuong thuc : %s%n", phuongThuc);
        } else {
            System.out.println("   (!) Luu don hang that bai. Vui long thu lai.");
        }
        System.out.println("=".repeat(60));
    }

    private static void showMainMenu() {
        System.out.println("""

                =============================================================
                          FAST FOOD CHAIN MIS - MENU CHINH
                =============================================================
                 1. Quan ly thuc the co ban (CRUD Khach hang, Mon an, Don hang...)
                 2. Thuc thi 30 cau truy van (Section 9 - 3 Thanh vien)
                 3. Thao tac nang cao voi Java Stream API / Lambda Functions
                 4. Tao lai toan bo database & nap lai du lieu mau tu dau
                 5. [DEMO] Nhan vien ban hang cho khach vang lai
                 0. Thoat chuong trinh
                =============================================================
                """);
    }

    private static void reinitializeDatabase() {
        System.out.println("\n--- TAO LAI DATABASE & NAP DU LIEU MAU TU DAU ---");
        System.out.print("Ban co chac chan muon xoa toan bo va nap lai du lieu mau? (y/n): ");
        String confirm = SCANNER.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            DatabaseInitializer.reinitialize();
            System.out.println(">> Da tao lai database va nap toan bo du lieu mau thanh cong!");
        } else {
            System.out.println(">> Da huy thao tac.");
        }
    }

    // =============================================================
    // 1. MENU CRUD VA THAO TAC CO BAN
    // =============================================================
    private static void menuCRUD() {
        int choice;
        do {
            System.out.println("""

                    -------------------------------------------------------------
                                QUAN LY THUC THE CO BAN (CRUD)
                    -------------------------------------------------------------
                     1. Danh sach Chi Nhanh
                     2. Danh sach Danh Muc
                     3. Danh sach Mon An
                     4. Them Mon An moi
                     5. Cap nhat Mon An (1 field dong bang Lambda BiConsumer)
                     6. Xoa Mon An
                     7. Danh sach Khach Hang
                     8. Them Khach Hang moi (kiem tra trung SDT)
                     9. Danh sach Nhan Vien
                    10. Them Nhan Vien moi
                    11. Danh sach Tai Khoan
                    12. Khoa Tai Khoan Nhan Vien (LOCKED)
                    13. Danh sach Ton Kho theo Chi Nhanh
                    14. Cap nhat so luong Ton Kho
                    15. Danh sach Don Hang & Thanh Toan
                     0. Quay lai Menu Chinh
                    -------------------------------------------------------------
                    """);
            choice = InputUtil.readInt(SCANNER, "Chon chuc nang [0-15]: ");

            switch (choice) {
                case 1 -> showAllChiNhanh();
                case 2 -> showAllDanhMuc();
                case 3 -> showAllMonAn();
                case 4 -> addMonAn();
                case 5 -> updateMonAnField();
                case 6 -> deleteMonAn();
                case 7 -> showAllKhachHang();
                case 8 -> addKhachHang();
                case 9 -> showAllNhanVien();
                case 10 -> addNhanVien();
                case 11 -> showAllTaiKhoan();
                case 12 -> lockTaiKhoan();
                case 13 -> showAllTonKho();
                case 14 -> updateTonKho();
                case 15 -> showAllDonHang();
                case 0 -> System.out.println("Dang quay lai menu chinh...");
                default -> System.out.println("(!) Lua chon khong hop le.");
            }
        } while (choice != 0);
    }

    private static void showAllChiNhanh() {
        System.out.println("\n--- DANH SACH CHI NHANH ---");
        printList(CHI_NHANH_SERVICE.getAll());
    }

    private static void showAllDanhMuc() {
        System.out.println("\n--- DANH SACH DANH MUC ---");
        printList(DANH_MUC_SERVICE.getAll());
    }

    private static void showAllMonAn() {
        System.out.println("\n--- DANH SACH MON AN ---");
        printList(MON_AN_SERVICE.getAll());
    }

    private static void addMonAn() {
        System.out.println("\n--- THEM MON AN MOI ---");
        String maMon = InputUtil.readString(SCANNER, "Ma mon (VD: M019): ");
        String tenMon = InputUtil.readString(SCANNER, "Ten mon: ");
        BigDecimal giaBan = InputUtil.readBigDecimal(SCANNER, "Gia ban (> 0): ");
        String moTa = InputUtil.readNullableString(SCANNER, "Mo ta: ");
        TrangThaiMon trangThai = InputUtil.readEnum(SCANNER, "Chon trang thai:", TrangThaiMon.class);

        showAllDanhMuc();
        String maDM = InputUtil.readString(SCANNER, "Nhap ma danh muc (VD: DM01): ");

        MonAn mon = new MonAn(maMon, tenMon, giaBan, moTa, trangThai, maDM);
        boolean ok = MON_AN_SERVICE.addMonAn(mon);
        System.out.println(ok ? ">> Them mon an thanh cong!" : ">> Them mon an that bai.");
    }

    private static void updateMonAnField() {
        System.out.println("\n--- CAP NHAT 1 TRUONG DU LIEU CUA MON AN (Lambda BiConsumer) ---");
        String maMon = InputUtil.readString(SCANNER, "Nhap ma mon can cap nhat (VD: M001): ");
        System.out.println("Cac truong ho tro: [ten, gia, mota, trangthai, danhmuc]");
        String field = InputUtil.readString(SCANNER, "Chon truong cap nhat: ");
        String newValue = InputUtil.readString(SCANNER, "Nhap gia tri moi: ");

        boolean ok = MON_AN_SERVICE.updateSingleField(maMon, field, newValue);
        System.out.println(ok ? ">> Cap nhat mon an thanh cong!" : ">> Cap nhat that bai (kiem tra ma mon hoac dinh dang).");
    }

    private static void deleteMonAn() {
        System.out.println("\n--- XOA MON AN ---");
        String maMon = InputUtil.readString(SCANNER, "Nhap ma mon can xoa: ");
        boolean ok = MON_AN_SERVICE.deleteMonAn(maMon);
        System.out.println(ok ? ">> Xoa mon an thanh cong!" : ">> Xoa that bai (co the mon dang co trong don hang/ton kho).");
    }

    private static void showAllKhachHang() {
        System.out.println("\n--- DANH SACH KHACH HANG ---");
        printList(KHACH_HANG_SERVICE.getAll());
    }

    private static void addKhachHang() {
        System.out.println("\n--- THEM KHACH HANG MOI ---");
        String maKH = InputUtil.readString(SCANNER, "Ma KH (VD: KH011): ");
        String hoTen = InputUtil.readString(SCANNER, "Ho ten: ");
        String phone = InputUtil.readString(SCANNER, "So dien thoai: ");

        if (KHACH_HANG_SERVICE.phoneExists(phone)) {
            System.out.println("(!) So dien thoai da ton tai trong he thong (UNIQUE)! Huy them moi.");
            return;
        }

        String email = InputUtil.readNullableString(SCANNER, "Email (de trong neu khong co): ");
        String diaChi = InputUtil.readNullableString(SCANNER, "Dia chi: ");
        LocalDate ngayDK = LocalDate.now();

        KhachHang kh = new KhachHang(maKH, hoTen, phone, email, diaChi, ngayDK);
        boolean ok = KHACH_HANG_SERVICE.addKhachHang(kh);
        System.out.println(ok ? ">> Them khach hang thanh cong!" : ">> Them khach hang that bai.");
    }

    private static void showAllNhanVien() {
        System.out.println("\n--- DANH SACH NHAN VIEN ---");
        printList(NHAN_VIEN_SERVICE.getAll());
    }

    private static void addNhanVien() {
        System.out.println("\n--- THEM NHAN VIEN MOI ---");
        String maNV = InputUtil.readString(SCANNER, "Ma NV (VD: NV09): ");
        String hoTen = InputUtil.readString(SCANNER, "Ho ten: ");
        String phone = InputUtil.readString(SCANNER, "So dien thoai: ");
        ChucVu chucVu = InputUtil.readEnum(SCANNER, "Chon chuc vu:", ChucVu.class);

        showAllChiNhanh();
        String maCN = InputUtil.readString(SCANNER, "Nhap ma chi nhanh truc thuoc (VD: CN01): ");

        NhanVien nv = new NhanVien(maNV, hoTen, phone, chucVu, maCN);
        boolean ok = NHAN_VIEN_SERVICE.addNhanVien(nv);
        System.out.println(ok ? ">> Them nhan vien thanh cong!" : ">> Them nhan vien that bai.");
    }

    private static void showAllTaiKhoan() {
        System.out.println("\n--- DANH SACH TAI KHOAN ---");
        printList(TAI_KHOAN_SERVICE.getAll());
    }

    private static void lockTaiKhoan() {
        System.out.println("\n--- KHOA TAI KHOAN (LOCKED) ---");
        String maTK = InputUtil.readString(SCANNER, "Nhap ma tai khoan can khoa (VD: TK05): ");
        boolean ok = TAI_KHOAN_SERVICE.lockTaiKhoan(maTK);
        System.out.println(ok ? ">> Khoa tai khoan thanh cong!" : ">> Khoa tai khoan that bai.");
    }

    private static void showAllTonKho() {
        System.out.println("\n--- DANH SACH TON KHO THEO CHI NHANH ---");
        printList(TON_KHO_SERVICE.getAll());
    }

    private static void updateTonKho() {
        System.out.println("\n--- CAP NHAT SO LUONG TON KHO ---");
        String maTon = InputUtil.readString(SCANNER, "Nhap ma ban ghi ton kho (VD: T001): ");
        int soLuong = InputUtil.readInt(SCANNER, "Nhap so luong ton moi (>= 0): ");
        boolean ok = TON_KHO_SERVICE.updateSoLuong(maTon, soLuong);
        System.out.println(ok ? ">> Cap nhat ton kho thanh cong!" : ">> Cap nhat ton kho that bai.");
    }

    private static void showAllDonHang() {
        System.out.println("\n--- DANH SACH DON HANG ---");
        printList(DON_HANG_SERVICE.getAll());
    }

    // =============================================================
    // 2. MENU 30 CAU TRUY VAN SECTION 9
    // =============================================================
    private static void menu30TruyVan() {
        int choice;
        do {
            System.out.println("""

                    =============================================================
                                30 CAU TRUY VAN (SECTION 9 BAO CAO)
                    =============================================================
                    --- [CHOI MINH VAN: CAU 1 - 10] ---
                      1. Nhan vien chua duoc cap tai khoan
                      2. Mon Burger gia tu 40k den 60k (sap xep giam dan)
                      3. Mon ton kho < 10 tai tung chi nhanh
                      4. Khach hang tai TP.HCM
                      5. Phuong thuc thanh toan tung thanh cong
                      6. Don hang hoan thanh trong thang 02/2026
                      7. Don hang kem ten khach hang & nhan vien (ke ca vang lai)
                      8. Chi tiet don hang 1001
                      9. Doi chieu tien hang tu chi tiet voi tien hang cua don
                     10. Tong doanh thu cac don da hoan thanh

                    --- [NGUYEN QUOC TIEN: CAU 11 - 20] ---
                     11. Thong ke doanh thu theo tung thang trong nam 2026
                     12. Top 3 mon ban chay nhat (theo tong so luong)
                     13. Tong chi tieu tung khach hang (ke ca khach 0 don)
                     14. Khach hang chua tung dat don nao
                     15. Mon an chua tung ban thanh cong
                     16. Thong ke so don va doanh thu theo nhan vien
                     17. Khach hang co it nhat 2 don hoan thanh
                     18. Dong chi tiet co don gia khac gia hien tai (lich su gia)
                     19. Tai khoan dang bi khoa (LOCKED)
                     20. Don hang co tong tien > muc trung binh he thong

                    --- [NGUYEN MAI THIEN QUI: CAU 21 - 30] ---
                     21. Tai khoan chua tung dang nhap
                     22. Don hang co ap dung ma khuyen mai
                     23. Tong tien giam cua tung chuong trinh khuyen mai
                     24. Khuyen mai chua tung duoc ap dung
                     25. So sanh doanh thu don co va khong dung khuyen mai
                     26. Don co tien giam bi chan boi muc tran toi da
                     27. Tong tien thu theo phuong thuc thanh toan
                     28. Doanh thu theo tung chi nhanh (giam dan)
                     29. Khach hang chi tieu cao hon trung binh
                     30. Don hang thanh toan that bai roi sau do thanh cong

                    --- [TIEN ICH CHAY HANG LOAT] ---
                     31. Chay 10 cau cua Choi Minh Van (1 - 10)
                     32. Chay 10 cau cua Nguyen Quoc Tien (11 - 20)
                     33. Chay 10 cau cua Nguyen Mai Thien Qui (21 - 30)
                     99. CHAY TOAN BO 30 CAU TRUY VAN
                      0. Quay lai Menu Chinh
                    =============================================================
                    """);
            choice = InputUtil.readInt(SCANNER, "Chon cau truy van can chay [0-33, 99]: ");

            if (choice >= 1 && choice <= 30) {
                QUERY_LAMBDA_SERVICE.run(choice);
            } else if (choice == 31) {
                for (int i = 1; i <= 10; i++) QUERY_LAMBDA_SERVICE.run(i);
            } else if (choice == 32) {
                for (int i = 11; i <= 20; i++) QUERY_LAMBDA_SERVICE.run(i);
            } else if (choice == 33) {
                for (int i = 21; i <= 30; i++) QUERY_LAMBDA_SERVICE.run(i);
            } else if (choice == 99) {
                for (int i = 1; i <= 30; i++) QUERY_LAMBDA_SERVICE.run(i);
            } else if (choice == 0) {
                System.out.println("Dang quay lai menu chinh...");
            } else {
                System.out.println("(!) Lua chon khong hop le.");
            }
        } while (choice != 0);
    }






    
    private static void menuStreamApiLambda() {
        int choice;
        do {
            System.out.println("""

                    =============================================================
                            JAVA STREAM API & LAMBDA EXPRESSIONS DEMO
                    =============================================================
                     1. Loc mon an gia >= 50.000 va dang ban          [filter Lambda]
                     2. Loc khach hang co email @gmail.com           [Predicate Lambda]
                     3. Tim kiem nhan vien theo tu khoa ten          [filter Lambda]
                     4. Kiem tra su ton tai cua ten dang nhap        [anyMatch]
                     5. Sap xep mon an theo gia tang dan             [Comparator]
                     6. Sap xep mon an: Gia giam dan + Ten tang dan  [Combined Comparator]
                     7. Tim mon an co gia cao nhat                   [Stream max]
                     8. Tinh gia tri don hang trung binh             [mapToDouble + average]
                     9. Tong doanh thu don hoan thanh                [reduce]
                    10. Nhom mon an theo Danh Muc                    [groupingBy]
                    11. Nhom nhan vien theo Chi Nhanh                [groupingBy]
                    12. Thong ke so luong don theo trang thai        [groupingBy + counting]
                    13. Thong ke doanh thu theo phuong thuc TT       [groupingBy + reducing]
                     0. Quay lai Menu Chinh
                    =============================================================
                    """);
            choice = InputUtil.readInt(SCANNER, "Chon chuc nang [0-13]: ");

            switch (choice) {
                case 1 -> demoFilterMonAn();
                case 2 -> demoFilterKhachHang();
                case 3 -> demoSearchNhanVien();
                case 4 -> demoCheckUsername();
                case 5 -> demoSortMonAnAsc();
                case 6 -> demoSortMonAnDescThenName();
                case 7 -> demoMostExpensiveFood();
                case 8 -> demoAverageOrderValue();
                case 9 -> demoTotalRevenueReduce();
                case 10 -> demoGroupMonAnByDanhMuc();
                case 11 -> demoGroupNhanVienByChiNhanh();
                case 12 -> demoCountOrdersByStatus();
                case 13 -> demoRevenueByPaymentMethod();
                case 0 -> System.out.println("Dang quay lai menu chinh...");
                default -> System.out.println("(!) Lua chon khong hop le.");
            }
        } while (choice != 0);
    }

    private static void demoFilterMonAn() {
        System.out.println("\n--- [STREAM FILTER] LOC MON AN GIA >= 50.000 VA DANG BAN ---");
        Predicate<MonAn> condition = m -> m.getGiaBan().compareTo(new BigDecimal("50000")) >= 0
                && m.getTrangThai() == TrangThaiMon.DANG_BAN;
        printList(MON_AN_SERVICE.filter(condition));



    }

    private static void demoFilterKhachHang() {
        System.out.println("\n--- [PREDICATE LAMBDA] KHACH HANG CO EMAIL @GMAIL.COM ---");
        Predicate<KhachHang> condition = kh -> kh.getEmail() != null && kh.getEmail().toLowerCase().endsWith("@gmail.com");
        printList(KHACH_HANG_SERVICE.filter(condition));
    }

    private static void demoSearchNhanVien() {
        System.out.print("\nNhap tu khoa ho ten nhan vien can tim: ");
        String keyword = SCANNER.nextLine().trim();
        printList(NHAN_VIEN_SERVICE.searchByName(keyword));
    }

    private static void demoCheckUsername() {
        System.out.print("\nNhap ten dang nhap can kiem tra ton tai: ");
        String username = SCANNER.nextLine().trim();
        boolean exists = TAI_KHOAN_SERVICE.usernameExists(username);
        System.out.println(exists
                ? ">> Ten dang nhap [" + username + "] DA TON TAI trong he thong!"
                : ">> Ten dang nhap [" + username + "] CHUA TON TAI (co the dang ky moi).");
    }

    private static void demoSortMonAnAsc() {
        System.out.println("\n--- [COMPARATOR] SAP XEP MON AN THEO GIA TANG DAN ---");
        printList(MON_AN_SERVICE.sortByPriceAsc());
    }

    private static void demoSortMonAnDescThenName() {
        System.out.println("\n--- [COMBINED COMPARATOR] GIA GIAM DAN, NEU BANG NHAU THI TEN TANG DAN ---");
        printList(MON_AN_SERVICE.sortByPriceDescThenName());
    }

    private static void demoMostExpensiveFood() {
        System.out.println("\n--- [STREAM MAX] MON AN CO GIA DAT NHAT ---");
        MON_AN_SERVICE.findMostExpensive().ifPresentOrElse(
                m -> System.out.printf("Mon dat nhat: %s (%s) - Gia: %s%n",
                        m.getTenMon(), m.getMaMon(), TablePrinter.formatCurrency(m.getGiaBan())),
                () -> System.out.println("Khong co du lieu mon an.")
        );
    }

    private static void demoAverageOrderValue() {
        System.out.println("\n--- [MAPTODOUBLE + AVERAGE] GIA TRI DON HANG TRUNG BINH ---");
        double avg = DON_HANG_SERVICE.calculateAverageOrderValue();
        System.out.printf("Gia tri don hang trung binh: %s%n", TablePrinter.formatCurrency(avg));
    }

    private static void demoTotalRevenueReduce() {
        System.out.println("\n--- [REDUCE] TONG DOANH THU DON HOAN THANH ---");
        BigDecimal total = DON_HANG_SERVICE.calculateTotalCompletedRevenue();
        System.out.printf("Tong doanh thu thuc te: %s%n", TablePrinter.formatCurrency(total));
    }

    private static void demoGroupMonAnByDanhMuc() {
        System.out.println("\n--- [COLLECTORS.GROUPINGBY] NHOM MON AN THEO DANH MUC ---");
        Map<String, List<MonAn>> groups = MON_AN_SERVICE.groupByDanhMuc();
        groups.forEach((maDM, list) -> {
            System.out.println("\n[ Danh Muc: " + maDM + " - Tong so mon: " + list.size() + " ]");
            for (MonAn m : list) {
                System.out.printf("  - %-6s | %-20s | %s%n", m.getMaMon(), m.getTenMon(), TablePrinter.formatCurrency(m.getGiaBan()));
            }
        });
    }

    private static void demoGroupNhanVienByChiNhanh() {
        System.out.println("\n--- [COLLECTORS.GROUPINGBY] NHOM NHAN VIEN THEO CHI NHANH ---");
        Map<String, List<NhanVien>> groups = NHAN_VIEN_SERVICE.groupByChiNhanh();
        groups.forEach((maCN, list) -> {
            System.out.println("\n[ Chi Nhanh: " + maCN + " - Nhan su: " + list.size() + " nguoi ]");
            for (NhanVien nv : list) {
                System.out.printf("  - %-6s | %-18s | %-12s | %s%n", nv.getMaNV(), nv.getHoTen(), nv.getChucVu().name(), nv.getSoDienThoai());
            }
        });
    }

    private static void demoCountOrdersByStatus() {
        System.out.println("\n--- [GROUPINGBY + COUNTING] SO LUONG DON THEO TRANG THAI ---");
        Map<TrangThaiDonHang, Long> counts = DON_HANG_SERVICE.countOrdersByStatus();
        counts.forEach((status, count) ->
                System.out.printf("%-20s : %d don%n", status.name(), count)
        );
    }

    private static void demoRevenueByPaymentMethod() {
        System.out.println("\n--- [GROUPINGBY + REDUCING] DOANH THU THEO PHUONG THUC THANH TOAN ---");
        Map<PhuongThucThanhToan, BigDecimal> rev = THANH_TOAN_SERVICE.revenueByPaymentMethod();
        rev.forEach((method, amount) ->
                System.out.printf("%-20s : %s%n", method.name(), TablePrinter.formatCurrency(amount))
        );
    }

    private static <T> void printList(List<T> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("(Khong co du lieu)");
            return;
        }
        list.forEach(System.out::println);
    }
}