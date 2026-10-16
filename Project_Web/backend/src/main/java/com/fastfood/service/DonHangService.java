package com.fastfood.service;

import com.fastfood.dto.OrderCreateRequest;
import com.fastfood.dto.OrderDetailResponseDTO;
import com.fastfood.dto.OrderItemRequest;
import com.fastfood.entity.*;
import com.fastfood.enums.*;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final MonAnRepository monAnRepository;
    private final ChiNhanhRepository chiNhanhRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final TonKhoRepository tonKhoRepository;
    private final KhuyenMaiService khuyenMaiService;

    public DonHangService(DonHangRepository donHangRepository,
                          ChiTietDonHangRepository chiTietDonHangRepository,
                          ThanhToanRepository thanhToanRepository,
                          MonAnRepository monAnRepository,
                          ChiNhanhRepository chiNhanhRepository,
                          NhanVienRepository nhanVienRepository,
                          KhachHangRepository khachHangRepository,
                          KhuyenMaiRepository khuyenMaiRepository,
                          TonKhoRepository tonKhoRepository,
                          KhuyenMaiService khuyenMaiService) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.monAnRepository = monAnRepository;
        this.chiNhanhRepository = chiNhanhRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khachHangRepository = khachHangRepository;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.khuyenMaiService = khuyenMaiService;
    }

    public List<DonHang> getAll() {
        return donHangRepository.findAll();
    }

    public DonHang getById(Long id) {
        return donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    public OrderDetailResponseDTO getOrderDetail(Long id) {
        DonHang order = getById(id);

        String branchName = chiNhanhRepository.findById(order.getMaCN())
                .map(ChiNhanh::getTenCN).orElse(order.getMaCN());
        String empName = nhanVienRepository.findById(order.getMaNV())
                .map(NhanVien::getHoTen).orElse(order.getMaNV());
        String custName = order.getMaKH() != null ?
                khachHangRepository.findById(order.getMaKH()).map(KhachHang::getHoTen).orElse(order.getMaKH())
                : "(Walk-in Customer)";
        String promoName = order.getMaKM() != null ?
                khuyenMaiRepository.findById(order.getMaKM()).map(KhuyenMai::getTenKM).orElse(order.getMaKM())
                : null;

        List<ChiTietDonHang> items = chiTietDonHangRepository.findByMaDH(id);
        List<OrderDetailResponseDTO.ChiTietItemDTO> itemDTOs = items.stream().map(item -> {
            String foodName = monAnRepository.findById(item.getMaMon())
                    .map(MonAn::getTenMon).orElse(item.getMaMon());
            return OrderDetailResponseDTO.ChiTietItemDTO.builder()
                    .maCTDH(item.getMaCTDH())
                    .maDH(item.getMaDH())
                    .maMon(item.getMaMon())
                    .tenMon(foodName)
                    .soLuong(item.getSoLuong())
                    .donGia(item.getDonGia())
                    .thanhTien(item.getThanhTien())
                    .build();
        }).toList();

        List<ThanhToan> payments = thanhToanRepository.findByMaDH(id);

        return OrderDetailResponseDTO.builder()
                .order(order)
                .tenCN(branchName)
                .tenNV(empName)
                .tenKH(custName)
                .tenKM(promoName)
                .items(itemDTOs)
                .payments(payments)
                .build();
    }

    @Transactional
    public OrderDetailResponseDTO createOrder(OrderCreateRequest request) {
        if (!chiNhanhRepository.existsById(request.getMaCN())) {
            throw new BadRequestException("Branch does not exist: " + request.getMaCN());
        }
        if (!nhanVienRepository.existsById(request.getMaNV())) {
            throw new BadRequestException("Employee does not exist: " + request.getMaNV());
        }
        if (request.getMaKH() != null && !request.getMaKH().trim().isEmpty()) {
            if (!khachHangRepository.existsById(request.getMaKH())) {
                throw new BadRequestException("Customer does not exist: " + request.getMaKH());
            }
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order must have at least one item");
        }

        // Determine next maDH
        long nextMaDH = 1001L;
        List<DonHang> allOrders = donHangRepository.findAll();
        if (!allOrders.isEmpty()) {
            long maxId = allOrders.stream().mapToLong(DonHang::getMaDH).max().orElse(1000L);
            nextMaDH = maxId + 1;
        }

        // Determine next maCTDH
        long nextMaCTDH = 1L;
        List<ChiTietDonHang> allDetails = chiTietDonHangRepository.findAll();
        if (!allDetails.isEmpty()) {
            long maxCtId = allDetails.stream().mapToLong(ChiTietDonHang::getMaCTDH).max().orElse(0L);
            nextMaCTDH = maxCtId + 1;
        }

        BigDecimal tienHang = BigDecimal.ZERO;
        List<ChiTietDonHang> chiTietList = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            MonAn food = monAnRepository.findById(itemReq.getMaMon())
                    .orElseThrow(() -> new BadRequestException("Food item not found: " + itemReq.getMaMon()));

            BigDecimal donGia = itemReq.getDonGia() != null ? itemReq.getDonGia() : food.getGiaBan();
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(itemReq.getSoLuong()));
            tienHang = tienHang.add(thanhTien);

            ChiTietDonHang ctdh = ChiTietDonHang.builder()
                    .maCTDH(nextMaCTDH++)
                    .maDH(nextMaDH)
                    .maMon(food.getMaMon())
                    .soLuong(itemReq.getSoLuong())
                    .donGia(donGia)
                    .thanhTien(thanhTien)
                    .build();
            chiTietList.add(ctdh);
        }

        // Calculate discount
        BigDecimal tienGiam = BigDecimal.ZERO;
        String validMaKM = null;
        if (request.getMaKM() != null && !request.getMaKM().trim().isEmpty()) {
            validMaKM = request.getMaKM().trim();
            tienGiam = khuyenMaiService.calculateDiscount(validMaKM, tienHang);
        }

        BigDecimal tongTien = tienHang.subtract(tienGiam);
        if (tongTien.compareTo(BigDecimal.ZERO) < 0) {
            tongTien = BigDecimal.ZERO;
        }

        boolean payNow = Boolean.TRUE.equals(request.getPayImmediately());
        TrangThaiDonHang initialStatus = payNow ? TrangThaiDonHang.DA_HOAN_THANH : TrangThaiDonHang.CHO_XAC_NHAN;

        DonHang order = DonHang.builder()
                .maDH(nextMaDH)
                .ngayDat(LocalDateTime.now())
                .trangThai(initialStatus)
                .tienHang(tienHang)
                .tienGiam(tienGiam)
                .tongTien(tongTien)
                .maKH(request.getMaKH())
                .maKM(validMaKM)
                .maNV(request.getMaNV())
                .maCN(request.getMaCN())
                .build();

        donHangRepository.save(order);
        chiTietDonHangRepository.saveAll(chiTietList);

        // Process payment if requested
        if (payNow) {
            PhuongThucThanhToan pt = request.getPhuongThuc() != null ? request.getPhuongThuc() : PhuongThucThanhToan.TIEN_MAT;
            ThanhToan payment = ThanhToan.builder()
                    .maTT("TT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .maDH(nextMaDH)
                    .soTien(tongTien)
                    .phuongThuc(pt)
                    .trangThai(TrangThaiThanhToan.DA_THANH_TOAN)
                    .ngayThanhToan(LocalDateTime.now())
                    .build();
            thanhToanRepository.save(payment);

            // Deduct inventory
            for (ChiTietDonHang item : chiTietList) {
                tonKhoRepository.findByMaMonAndMaCN(item.getMaMon(), request.getMaCN())
                        .ifPresent(stock -> {
                            int newQty = Math.max(0, stock.getSoLuongTon() - item.getSoLuong());
                            stock.setSoLuongTon(newQty);
                            stock.setNgayCapNhat(LocalDateTime.now());
                            tonKhoRepository.save(stock);
                        });
            }
        }

        return getOrderDetail(nextMaDH);
    }

    @Transactional
    public DonHang updateStatus(Long id, TrangThaiDonHang nextStatus) {
        DonHang order = getById(id);
        if (order.getTrangThai() == nextStatus) {
            return order;
        }
        if (!order.getTrangThai().coTheChuyenSang(nextStatus)) {
            throw new BadRequestException("Cannot transition status from " + order.getTrangThai() + " to " + nextStatus);
        }

        // If completing, ensure there's a successful payment
        if (nextStatus == TrangThaiDonHang.DA_HOAN_THANH) {
            List<ThanhToan> payments = thanhToanRepository.findByMaDH(id);
            boolean hasPaid = payments.stream()
                    .anyMatch(p -> p.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN);
            if (!hasPaid) {
                // Auto create payment so flow completes cleanly
                ThanhToan autoPay = ThanhToan.builder()
                        .maTT("TT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                        .maDH(id)
                        .soTien(order.getTongTien())
                        .phuongThuc(PhuongThucThanhToan.TIEN_MAT)
                        .trangThai(TrangThaiThanhToan.DA_THANH_TOAN)
                        .ngayThanhToan(LocalDateTime.now())
                        .build();
                thanhToanRepository.save(autoPay);
            }
        }

        order.setTrangThai(nextStatus);
        return donHangRepository.save(order);
    }

    @Transactional
    public void delete(Long id) {
        if (!donHangRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with ID: " + id);
        }
        chiTietDonHangRepository.deleteByMaDH(id);
        thanhToanRepository.deleteAll(thanhToanRepository.findByMaDH(id));
        donHangRepository.deleteById(id);
    }
}
