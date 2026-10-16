package com.fastfood.service;

import com.fastfood.entity.NhanVien;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.ChiNhanhRepository;
import com.fastfood.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NhanVienService {

    private final NhanVienRepository repository;
    private final ChiNhanhRepository chiNhanhRepository;

    public NhanVienService(NhanVienRepository repository, ChiNhanhRepository chiNhanhRepository) {
        this.repository = repository;
        this.chiNhanhRepository = chiNhanhRepository;
    }

    public List<NhanVien> getAll() {
        return repository.findAll();
    }

    public NhanVien getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    public List<NhanVien> getByBranch(String maCN) {
        return repository.findByMaCN(maCN);
    }

    public List<NhanVien> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        String lower = keyword.trim().toLowerCase();
        return repository.findAll().stream()
                .filter(nv -> nv.getHoTen() != null && nv.getHoTen().toLowerCase().contains(lower))
                .toList();
    }

    @Transactional
    public NhanVien create(NhanVien nv) {
        if (nv.getMaNV() == null || nv.getMaNV().trim().isEmpty()) {
            throw new BadRequestException("Employee ID (maNV) is required");
        }
        if (repository.existsById(nv.getMaNV())) {
            throw new BadRequestException("Employee ID already exists: " + nv.getMaNV());
        }
        if (!chiNhanhRepository.existsById(nv.getMaCN())) {
            throw new BadRequestException("Branch does not exist: " + nv.getMaCN());
        }
        return repository.save(nv);
    }

    @Transactional
    public NhanVien update(String id, NhanVien nv) {
        NhanVien existing = getById(id);
        if (nv.getMaCN() != null && !chiNhanhRepository.existsById(nv.getMaCN())) {
            throw new BadRequestException("Branch does not exist: " + nv.getMaCN());
        }
        existing.setHoTen(nv.getHoTen());
        existing.setSoDienThoai(nv.getSoDienThoai());
        existing.setChucVu(nv.getChucVu());
        existing.setMaCN(nv.getMaCN());
        return repository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
