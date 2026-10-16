package com.fastfood.service;

import com.fastfood.entity.MonAn;
import com.fastfood.enums.TrangThaiMon;
import com.fastfood.exception.BadRequestException;
import com.fastfood.exception.ResourceNotFoundException;
import com.fastfood.repository.DanhMucRepository;
import com.fastfood.repository.MonAnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Service
public class MonAnService {

    private final MonAnRepository repository;
    private final DanhMucRepository danhMucRepository;

    public MonAnService(MonAnRepository repository, DanhMucRepository danhMucRepository) {
        this.repository = repository;
        this.danhMucRepository = danhMucRepository;
    }

    public List<MonAn> getAll() {
        return repository.findAll();
    }

    public MonAn getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + id));
    }

    public List<MonAn> getByCategory(String maDM) {
        return repository.findByMaDM(maDM);
    }

    public List<MonAn> getByStatus(TrangThaiMon status) {
        return repository.findByTrangThai(status);
    }

    public List<MonAn> filter(Predicate<MonAn> predicate) {
        return repository.findAll().stream().filter(predicate).toList();
    }

    public List<MonAn> sortPriceAsc() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(MonAn::getGiaBan))
                .toList();
    }

    public List<MonAn> sortPriceDescThenName() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(MonAn::getGiaBan).reversed().thenComparing(MonAn::getTenMon))
                .toList();
    }

    public Optional<MonAn> findMostExpensive() {
        return repository.findAll().stream()
                .max(Comparator.comparing(MonAn::getGiaBan));
    }

    @Transactional
    public MonAn create(MonAn mon) {
        if (mon.getMaMon() == null || mon.getMaMon().trim().isEmpty()) {
            throw new BadRequestException("Food item code (maMon) is required");
        }
        if (repository.existsById(mon.getMaMon())) {
            throw new BadRequestException("Food item code already exists: " + mon.getMaMon());
        }
        if (mon.getGiaBan() == null || mon.getGiaBan().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price must be greater than 0");
        }
        if (!danhMucRepository.existsById(mon.getMaDM())) {
            throw new BadRequestException("Category does not exist: " + mon.getMaDM());
        }
        if (mon.getTrangThai() == null) {
            mon.setTrangThai(TrangThaiMon.DANG_BAN);
        }
        return repository.save(mon);
    }

    @Transactional
    public MonAn update(String id, MonAn mon) {
        MonAn existing = getById(id);
        if (mon.getGiaBan() != null && mon.getGiaBan().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Price must be greater than 0");
        }
        if (mon.getMaDM() != null && !danhMucRepository.existsById(mon.getMaDM())) {
            throw new BadRequestException("Category does not exist: " + mon.getMaDM());
        }
        existing.setTenMon(mon.getTenMon());
        existing.setGiaBan(mon.getGiaBan());
        existing.setMoTa(mon.getMoTa());
        existing.setTrangThai(mon.getTrangThai());
        existing.setMaDM(mon.getMaDM());
        return repository.save(existing);
    }

    @Transactional
    public boolean updateField(String id, String field, String newValue) {
        MonAn existing = getById(id);
        Map<String, BiConsumer<MonAn, String>> updaters = Map.of(
                "ten", MonAn::setTenMon,
                "gia", (m, v) -> m.setGiaBan(new BigDecimal(v)),
                "mota", MonAn::setMoTa,
                "trangthai", (m, v) -> m.setTrangThai(TrangThaiMon.valueOf(v.toUpperCase())),
                "danhmuc", MonAn::setMaDM
        );
        BiConsumer<MonAn, String> updater = updaters.get(field.toLowerCase().trim());
        if (updater != null) {
            try {
                updater.accept(existing, newValue);
                repository.save(existing);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Transactional
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Food item not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
