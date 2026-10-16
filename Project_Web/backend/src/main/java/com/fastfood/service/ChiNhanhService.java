package com.fastfood.service;

import com.fastfood.entity.ChiNhanh;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.ChiNhanhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChiNhanhService {

    private final ChiNhanhRepository repository;

    public ChiNhanhService(ChiNhanhRepository repository) {
        this.repository = repository;
    }

    public List<ChiNhanh> getAll() {
        return repository.findAll();
    }

    public ChiNhanh getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));
    }

    @Transactional
    public ChiNhanh create(ChiNhanh branch) {
        if (branch.getMaCN() == null || branch.getMaCN().trim().isEmpty()) {
            throw new BadRequestException("Branch ID (maCN) is required");
        }
        if (repository.existsById(branch.getMaCN())) {
            throw new BadRequestException("Branch ID already exists: " + branch.getMaCN());
        }
        return repository.save(branch);
    }

    @Transactional
    public ChiNhanh update(String id, ChiNhanh branch) {
        ChiNhanh existing = getById(id);
        existing.setTenCN(branch.getTenCN());
        existing.setDiaChi(branch.getDiaChi());
        existing.setSoDienThoai(branch.getSoDienThoai());
        return repository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Branch not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
