package com.fastfood.service;

import com.fastfood.entity.KhuyenMai;
import com.fastfood.enums.TrangThaiKhuyenMai;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.KhuyenMaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class KhuyenMaiService {

    private final KhuyenMaiRepository repository;

    public KhuyenMaiService(KhuyenMaiRepository repository) {
        this.repository = repository;
    }

    public List<KhuyenMai> getAll() {
        return repository.findAll();
    }

    public KhuyenMai getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + id));
    }

    public List<KhuyenMai> getActivePromotions() {
        return repository.findByTrangThai(TrangThaiKhuyenMai.DANG_AP_DUNG);
    }

    public BigDecimal calculateDiscount(String maKM, BigDecimal subtotal) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        KhuyenMai km = getById(maKM);
        if (km.getTrangThai() != TrangThaiKhuyenMai.DANG_AP_DUNG) {
            return BigDecimal.ZERO;
        }
        if (km.getDonToiThieu() != null && subtotal.compareTo(km.getDonToiThieu()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = subtotal.multiply(BigDecimal.valueOf(km.getPhanTramGiam()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (km.getGiamToiDa() != null && discount.compareTo(km.getGiamToiDa()) > 0) {
            discount = km.getGiamToiDa();
        }

        return discount;
    }

    @Transactional
    public KhuyenMai create(KhuyenMai km) {
        if (km.getMaKM() == null || km.getMaKM().trim().isEmpty()) {
            throw new BadRequestException("Promotion ID (maKM) is required");
        }
        if (repository.existsById(km.getMaKM())) {
            throw new BadRequestException("Promotion ID already exists: " + km.getMaKM());
        }
        if (km.getPhanTramGiam() == null || km.getPhanTramGiam() <= 0 || km.getPhanTramGiam() > 100) {
            throw new BadRequestException("Discount percentage must be between 1 and 100");
        }
        return repository.save(km);
    }

    @Transactional
    public KhuyenMai update(String id, KhuyenMai km) {
        KhuyenMai existing = getById(id);
        existing.setTenKM(km.getTenKM());
        existing.setPhanTramGiam(km.getPhanTramGiam());
        existing.setDonToiThieu(km.getDonToiThieu());
        existing.setGiamToiDa(km.getGiamToiDa());
        existing.setNgayBatDau(km.getNgayBatDau());
        existing.setNgayKetThuc(km.getNgayKetThuc());
        existing.setTrangThai(km.getTrangThai());
        return repository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
