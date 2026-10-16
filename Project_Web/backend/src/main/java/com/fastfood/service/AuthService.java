package com.fastfood.service;

import com.fastfood.dto.LoginRequest;
import com.fastfood.dto.LoginResponse;
import com.fastfood.entity.KhachHang;
import com.fastfood.entity.NhanVien;
import com.fastfood.entity.TaiKhoan;
import com.fastfood.enums.TrangThaiTaiKhoan;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.KhachHangRepository;
import com.fastfood.repository.NhanVienRepository;
import com.fastfood.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;

    public AuthService(TaiKhoanRepository taiKhoanRepository,
                       NhanVienRepository nhanVienRepository,
                       KhachHangRepository khachHangRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        String loginKey = req.getUsername().trim();

        // 1. Check if employee account exists
        Optional<TaiKhoan> tkOpt = taiKhoanRepository.findByTenDangNhap(loginKey);
        if (tkOpt.isPresent()) {
            TaiKhoan tk = tkOpt.get();
            if (tk.getTrangThai() == TrangThaiTaiKhoan.LOCKED) {
                throw new BadRequestException("Account is locked. Please contact your administrator.");
            }

            // Demo password checking: allows demo hash fallback, exact match, username match, or default password
            boolean pwMatches = verifyPassword(req.getPassword(), tk.getMatKhauHash(), tk.getTenDangNhap());
            if (!pwMatches) {
                throw new BadRequestException("Invalid username or password");
            }

            // Update last login
            tk.setLanDangNhapCuoi(LocalDateTime.now());
            taiKhoanRepository.save(tk);

            // Fetch employee details
            NhanVien nv = nhanVienRepository.findById(tk.getMaNV())
                    .orElseThrow(() -> new ResourceNotFoundException("Associated employee record not found: " + tk.getMaNV()));

            String token = "TOKEN-" + UUID.randomUUID();

            return LoginResponse.builder()
                    .token(token)
                    .maTK(tk.getMaTK())
                    .maNV(nv.getMaNV())
                    .tenDangNhap(tk.getTenDangNhap())
                    .chucVu(nv.getChucVu().name())
                    .maCN(nv.getMaCN())
                    .hoTen(nv.getHoTen())
                    .role(nv.getChucVu().name())
                    .build();
        }

        // 2. Check if customer login (by phone or email or maKH)
        Optional<KhachHang> khOpt = khachHangRepository.findById(loginKey);
        if (khOpt.isEmpty()) {
            khOpt = khachHangRepository.findAll().stream()
                    .filter(k -> loginKey.equalsIgnoreCase(k.getSoDienThoai()) ||
                                 (k.getEmail() != null && loginKey.equalsIgnoreCase(k.getEmail())))
                    .findFirst();
        }

        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            String token = "CUST-" + UUID.randomUUID();
            return LoginResponse.builder()
                    .token(token)
                    .maKH(kh.getMaKH())
                    .tenDangNhap(kh.getSoDienThoai())
                    .hoTen(kh.getHoTen())
                    .role("CUSTOMER")
                    .build();
        }

        throw new BadRequestException("Invalid credentials. Account not found.");
    }

    private boolean verifyPassword(String inputPassword, String storedHash, String username) {
        if (inputPassword == null || inputPassword.isEmpty()) return false;
        if (inputPassword.equals(storedHash)) return true;
        if (inputPassword.equalsIgnoreCase(username)) return true;
        if ("123456".equals(inputPassword) || "admin".equalsIgnoreCase(inputPassword) || "password".equalsIgnoreCase(inputPassword)) return true;
        if (storedHash != null && storedHash.startsWith("$2a$10$DEMO_HASH_")) return true;
        return false;
    }
}
