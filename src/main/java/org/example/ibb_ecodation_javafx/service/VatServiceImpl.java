package org.example.ibb_ecodation_javafx.service;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.core.db.EntityFilter;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.repository.VatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VatServiceImpl implements VatService {

    private final VatRepository vatRepository;

    @Override
    public Vat save(Vat entity) {
        return vatRepository.save(entity);
    }

    @Override
    public void update(Vat entity) {
        vatRepository.update(entity);
    }

    /**
     * Dikkat! User id göre işlem yapar.
     * @return
     */
    @Override
    public Optional<Vat> findById(Integer integer) {
        return vatRepository.findById(integer);
    }

    @Override
    public List<Vat> findAll() {
        return vatRepository.findAll();
    }
    /**
     * Dikkat! User id göre işlem yapar.
     * @return
     */
    @Override
    public List<Vat> findAllById(Integer id) {
        return vatRepository.findAllById(id);
    }

    @Override
    public List<Vat> findAllByFilter(List<EntityFilter> filters) {
        return List.of();
    }

    @Override
    public void delete(Integer integer) {
        vatRepository.delete(integer);
    }

    @Override
    public Optional<Vat> findFirstByFilter(List<EntityFilter> filters) {
        return vatRepository.findFirstByFilter(filters);
    }
}
