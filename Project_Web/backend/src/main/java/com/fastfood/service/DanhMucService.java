package com.fastfood.service;

import com.fastfood.entity.DanhMuc;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.DanhMucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DanhMucService {

    private final DanhMucRepository repository;

    public DanhMucService(DanhMucRepository repository) {
        this.repository = repository;
    }

    public List<DanhMuc> getAll() {
        return repository.findAll();
    }

    public DanhMuc getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Transactional
    public DanhMuc create(DanhMuc category) {
        if (category.getMaDM() == null || category.getMaDM().trim().isEmpty()) {
            throw new BadRequestException("Category ID (maDM) is required");
        }
        if (repository.existsById(category.getMaDM())) {
            throw new BadRequestException("Category ID already exists: " + category.getMaDM());
        }
        return repository.save(category);
    }

    @Transactional
    public DanhMuc update(String id, DanhMuc category) {
        DanhMuc existing = getById(id);
        existing.setTenDM(category.getTenDM());
        existing.setMoTa(category.getMoTa());
        return repository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
