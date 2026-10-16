package com.fastfood.service;

import com.fastfood.entity.KhachHang;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

@Service
public class KhachHangService {

    private final KhachHangRepository repository;

    public KhachHangService(KhachHangRepository repository) {
        this.repository = repository;
    }

    public List<KhachHang> getAll() {
        return repository.findAll();
    }

    public KhachHang getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }

    public boolean phoneExists(String phone) {
        return repository.findBySoDienThoai(phone).isPresent();
    }

    public List<KhachHang> filter(Predicate<KhachHang> predicate) {
        return repository.findAll().stream().filter(predicate).toList();
    }

    @Transactional
    public KhachHang create(KhachHang kh) {
        if (kh.getMaKH() == null || kh.getMaKH().trim().isEmpty()) {
            throw new BadRequestException("Customer ID (maKH) is required");
        }
        if (repository.existsById(kh.getMaKH())) {
            throw new BadRequestException("Customer ID already exists: " + kh.getMaKH());
        }
        if (kh.getSoDienThoai() != null && phoneExists(kh.getSoDienThoai())) {
            throw new BadRequestException("Phone number already exists: " + kh.getSoDienThoai());
        }
        if (kh.getEmail() != null && repository.findByEmail(kh.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists: " + kh.getEmail());
        }
        if (kh.getNgayDangKy() == null) {
            kh.setNgayDangKy(LocalDate.now());
        }
        return repository.save(kh);
    }

    @Transactional
    public KhachHang update(String id, KhachHang kh) {
        KhachHang existing = getById(id);
        existing.setHoTen(kh.getHoTen());
        existing.setSoDienThoai(kh.getSoDienThoai());
        existing.setEmail(kh.getEmail());
        existing.setDiaChi(kh.getDiaChi());
        return repository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
