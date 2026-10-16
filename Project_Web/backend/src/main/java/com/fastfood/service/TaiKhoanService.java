package com.fastfood.service;

import com.fastfood.entity.TaiKhoan;
import com.fastfood.enums.TrangThaiTaiKhoan;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.NhanVienRepository;
import com.fastfood.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaiKhoanService {

    private final TaiKhoanRepository repository;
    private final NhanVienRepository nhanVienRepository;

    public TaiKhoanService(TaiKhoanRepository repository, NhanVienRepository nhanVienRepository) {
        this.repository = repository;
        this.nhanVienRepository = nhanVienRepository;
    }

    public List<TaiKhoan> getAll() {
        return repository.findAll();
    }

    public TaiKhoan getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
    }

    public TaiKhoan getByEmployeeId(String maNV) {
        return repository.findByMaNV(maNV)
                .orElseThrow(() -> new ResourceNotFoundException("No account assigned for employee: " + maNV));
    }

    public boolean usernameExists(String username) {
        return repository.findByTenDangNhap(username).isPresent();
    }

    @Transactional
    public TaiKhoan create(TaiKhoan tk) {
        if (tk.getMaTK() == null || tk.getMaTK().trim().isEmpty()) {
            throw new BadRequestException("Account ID (maTK) is required");
        }
        if (repository.existsById(tk.getMaTK())) {
            throw new BadRequestException("Account ID already exists: " + tk.getMaTK());
        }
        if (!nhanVienRepository.existsById(tk.getMaNV())) {
            throw new BadRequestException("Employee does not exist: " + tk.getMaNV());
        }
        if (repository.findByMaNV(tk.getMaNV()).isPresent()) {
            throw new BadRequestException("Employee already has an account: " + tk.getMaNV());
        }
        if (usernameExists(tk.getTenDangNhap())) {
            throw new BadRequestException("Username already taken: " + tk.getTenDangNhap());
        }
        if (tk.getNgayTao() == null) {
            tk.setNgayTao(LocalDateTime.now());
        }
        if (tk.getTrangThai() == null) {
            tk.setTrangThai(TrangThaiTaiKhoan.ACTIVE);
        }
        return repository.save(tk);
    }

    @Transactional
    public TaiKhoan lockAccount(String id) {
        TaiKhoan tk = getById(id);
        tk.setTrangThai(TrangThaiTaiKhoan.LOCKED);
        return repository.save(tk);
    }

    @Transactional
    public TaiKhoan unlockAccount(String id) {
        TaiKhoan tk = getById(id);
        tk.setTrangThai(TrangThaiTaiKhoan.ACTIVE);
        return repository.save(tk);
    }

    @Transactional
    public TaiKhoan recordLogin(String id) {
        TaiKhoan tk = getById(id);
        tk.setLanDangNhapCuoi(LocalDateTime.now());
        return repository.save(tk);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
