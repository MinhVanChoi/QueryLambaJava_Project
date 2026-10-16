package com.fastfood.service;

import com.fastfood.dto.DashboardStatsDTO;
import com.fastfood.entity.*;
import com.fastfood.enums.*;
import com.fastfood.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FastFoodService {

    private final ChiNhanhRepository chiNhanhRepository;
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final MonAnRepository monAnRepository;
    private final TonKhoRepository tonKhoRepository;
    private final DonHangRepository donHangRepository;
    private final ThanhToanRepository thanhToanRepository;

    public FastFoodService(ChiNhanhRepository chiNhanhRepository,
                           NhanVienRepository nhanVienRepository,
                           TaiKhoanRepository taiKhoanRepository,
                           KhachHangRepository khachHangRepository,
                           DanhMucRepository danhMucRepository,
                           MonAnRepository monAnRepository,
                           TonKhoRepository tonKhoRepository,
                           DonHangRepository donHangRepository,
                           ThanhToanRepository thanhToanRepository) {
        this.chiNhanhRepository = chiNhanhRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.danhMucRepository = danhMucRepository;
        this.monAnRepository = monAnRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.donHangRepository = donHangRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    public long countCompletedOrders() {
        return donHangRepository.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .count();
    }

    public Optional<MonAn> findMostExpensiveFood() {
        return monAnRepository.findAll().stream()
                .max(Comparator.comparing(MonAn::getGiaBan));
    }

    public double calculateAverageOrderValue() {
        return donHangRepository.findAll().stream()
                .mapToDouble(dh -> dh.getTongTien().doubleValue())
                .average()
                .orElse(0.0);
    }

    public BigDecimal calculateTotalCompletedRevenue() {
        return donHangRepository.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .map(DonHang::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, List<MonAn>> groupMonAnByDanhMuc() {
        return monAnRepository.findAll().stream()
                .collect(Collectors.groupingBy(MonAn::getMaDM, TreeMap::new, Collectors.toList()));
    }

    public Map<String, List<NhanVien>> groupNhanVienByChiNhanh() {
        return nhanVienRepository.findAll().stream()
                .collect(Collectors.groupingBy(NhanVien::getMaCN, TreeMap::new, Collectors.toList()));
    }

    public Map<String, Long> countOrdersByStatus() {
        return donHangRepository.findAll().stream()
                .collect(Collectors.groupingBy(dh -> dh.getTrangThai().name(), TreeMap::new, Collectors.counting()));
    }

    public Map<String, BigDecimal> revenueByPaymentMethod() {
        return thanhToanRepository.findAll().stream()
                .filter(tt -> tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                .collect(Collectors.groupingBy(
                        tt -> tt.getPhuongThuc().name(),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, ThanhToan::getSoTien, BigDecimal::add)
                ));
    }

    public Map<String, BigDecimal> revenueByBranch() {
        Map<String, String> branchNames = chiNhanhRepository.findAll().stream()
                .collect(Collectors.toMap(ChiNhanh::getMaCN, ChiNhanh::getTenCN));

        return donHangRepository.findAll().stream()
                .filter(dh -> dh.getTrangThai() == TrangThaiDonHang.DA_HOAN_THANH)
                .collect(Collectors.groupingBy(
                        dh -> branchNames.getOrDefault(dh.getMaCN(), dh.getMaCN()),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, DonHang::getTongTien, BigDecimal::add)
                ));
    }

    public DashboardStatsDTO getDashboardStats() {
        long totalBranches = chiNhanhRepository.count();
        long totalEmployees = nhanVienRepository.count();
        long totalActiveAccounts = taiKhoanRepository.findAll().stream()
                .filter(tk -> tk.getTrangThai() == TrangThaiTaiKhoan.ACTIVE)
                .count();
        long totalCustomers = khachHangRepository.count();
        long totalCategories = danhMucRepository.count();
        long totalFoodItems = monAnRepository.count();
        long totalOrders = donHangRepository.count();
        long completedOrders = countCompletedOrders();
        BigDecimal totalRevenue = calculateTotalCompletedRevenue();
        double avgOrderValue = calculateAverageOrderValue();
        long lowStockCount = tonKhoRepository.findAll().stream()
                .filter(tk -> tk.getSoLuongTon() != null && tk.getSoLuongTon() < 10)
                .count();

        return DashboardStatsDTO.builder()
                .totalBranches(totalBranches)
                .totalEmployees(totalEmployees)
                .totalActiveAccounts(totalActiveAccounts)
                .totalCustomers(totalCustomers)
                .totalCategories(totalCategories)
                .totalFoodItems(totalFoodItems)
                .totalOrders(totalOrders)
                .totalCompletedOrders(completedOrders)
                .totalCompletedRevenue(totalRevenue)
                .averageOrderValue(avgOrderValue)
                .lowStockCount(lowStockCount)
                .ordersByStatus(countOrdersByStatus())
                .revenueByPaymentMethod(revenueByPaymentMethod())
                .revenueByBranch(revenueByBranch())
                .build();
    }
}
