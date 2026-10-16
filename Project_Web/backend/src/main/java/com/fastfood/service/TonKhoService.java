package com.fastfood.service;

import com.fastfood.entity.TonKho;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.ChiNhanhRepository;
import com.fastfood.repository.MonAnRepository;
import com.fastfood.repository.TonKhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TonKhoService {

    private final TonKhoRepository repository;
    private final MonAnRepository monAnRepository;
    private final ChiNhanhRepository chiNhanhRepository;

    public TonKhoService(TonKhoRepository repository,
                         MonAnRepository monAnRepository,
                         ChiNhanhRepository chiNhanhRepository) {
        this.repository = repository;
        this.monAnRepository = monAnRepository;
        this.chiNhanhRepository = chiNhanhRepository;
    }

    public List<TonKho> getAll() {
        return repository.findAll();
    }

    public TonKho getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found with ID: " + id));
    }

    public List<TonKho> getByBranch(String maCN) {
        return repository.findByMaCN(maCN);
    }

    public List<TonKho> getByFood(String maMon) {
        return repository.findByMaMon(maMon);
    }

    public List<TonKho> getLowStock(int threshold) {
        return repository.findAll().stream()
                .filter(tk -> tk.getSoLuongTon() != null && tk.getSoLuongTon() < threshold)
                .toList();
    }

    @Transactional
    public TonKho updateStock(String maTon, int soLuong) {
        TonKho existing = getById(maTon);
        if (soLuong < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }
        existing.setSoLuongTon(soLuong);
        existing.setNgayCapNhat(LocalDateTime.now());
        return repository.save(existing);
    }

    @Transactional
    public TonKho create(TonKho tk) {
        if (tk.getMaTon() == null || tk.getMaTon().trim().isEmpty()) {
            throw new BadRequestException("Inventory ID (maTon) is required");
        }
        if (repository.existsById(tk.getMaTon())) {
            throw new BadRequestException("Inventory ID already exists: " + tk.getMaTon());
        }
        if (!monAnRepository.existsById(tk.getMaMon())) {
            throw new BadRequestException("Food item does not exist: " + tk.getMaMon());
        }
        if (!chiNhanhRepository.existsById(tk.getMaCN())) {
            throw new BadRequestException("Branch does not exist: " + tk.getMaCN());
        }
        if (repository.findByMaMonAndMaCN(tk.getMaMon(), tk.getMaCN()).isPresent()) {
            throw new BadRequestException("Inventory record already exists for item " + tk.getMaMon() + " at branch " + tk.getMaCN());
        }
        if (tk.getNgayCapNhat() == null) {
            tk.setNgayCapNhat(LocalDateTime.now());
        }
        return repository.save(tk);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory record not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
