package com.fastfood.service;

import com.fastfood.entity.DonHang;
import com.fastfood.entity.ThanhToan;
import com.fastfood.enums.PhuongThucThanhToan;
import com.fastfood.enums.TrangThaiDonHang;
import com.fastfood.enums.TrangThaiThanhToan;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.DonHangRepository;
import com.fastfood.repository.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ThanhToanService {

    private final ThanhToanRepository repository;
    private final DonHangRepository donHangRepository;

    public ThanhToanService(ThanhToanRepository repository, DonHangRepository donHangRepository) {
        this.repository = repository;
        this.donHangRepository = donHangRepository;
    }

    public List<ThanhToan> getAll() {
        return repository.findAll();
    }

    public ThanhToan getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
    }

    public List<ThanhToan> getByOrderId(Long maDH) {
        return repository.findByMaDH(maDH);
    }

    @Transactional
    public ThanhToan create(ThanhToan payment) {
        if (payment.getMaTT() == null || payment.getMaTT().trim().isEmpty()) {
            payment.setMaTT("TT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (!donHangRepository.existsById(payment.getMaDH())) {
            throw new BadRequestException("Order not found with ID: " + payment.getMaDH());
        }
        if (payment.getNgayThanhToan() == null) {
            payment.setNgayThanhToan(LocalDateTime.now());
        }
        return repository.save(payment);
    }

    @Transactional
    public ThanhToan updateStatus(String id, TrangThaiThanhToan newStatus) {
        ThanhToan payment = getById(id);
        payment.setTrangThai(newStatus);
        if (newStatus == TrangThaiThanhToan.DA_THANH_TOAN && payment.getNgayThanhToan() == null) {
            payment.setNgayThanhToan(LocalDateTime.now());
        }
        return repository.save(payment);
    }
}
