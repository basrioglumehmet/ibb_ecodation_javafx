package org.example.ibb_ecodation_javafx.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.ibb_ecodation_javafx.model.Vat;
import org.example.ibb_ecodation_javafx.repository.VatRepository;
import org.example.ibb_ecodation_javafx.repository.query.VatQuery;
import org.example.ibb_ecodation_javafx.service.VatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class VatServiceImpl implements VatService {

    private final VatRepository vatRepository;

    @Override
    public Vat create(Vat entity) {
        return vatRepository.create(entity, VatQuery.CREATE_VAT, List.of(
                entity.getUserId(),
                entity.getBaseAmount(),
                entity.getRate(),
                entity.getAmount(),
                entity.getTotalAmount(),
                entity.getReceiptNumber(),
                entity.getTransactionDate(),
                entity.getDescription(),
                entity.getExportFormat(),
                entity.isDeleted(),
                entity.getVersion()
        ));
    }

    @Override
    public void delete(int id) {
        vatRepository.delete(VatQuery.DELETE_VAT_BY_ID, List.of(id));
    }

    @Override
    public void read(int id, Consumer<Vat> callback) {
        Vat vat = vatRepository.read(Vat.class, VatQuery.READ_VAT_BY_ID, List.of(id));
        callback.accept(vat);
    }

    @Override
    public List<Vat> readAll(int userId) {
        return vatRepository.readAll(Vat.class, VatQuery.READ_ALL_VATS_BY_USER_ID, List.of(userId));
    }

    @Override
    public void update(Vat entity, Consumer<Vat> callback) {
        Vat updated = vatRepository.update(entity, VatQuery.UPDATE_VAT_BY_ID, List.of(
                entity.getUserId(),
                entity.getBaseAmount(),
                entity.getRate(),
                entity.getAmount(),
                entity.getTotalAmount(),
                entity.getReceiptNumber(),
                entity.getTransactionDate(),
                entity.getDescription(),
                entity.getExportFormat(),
                entity.isDeleted(),
                entity.getId(),
                entity.getVersion()
        ));
        callback.accept(updated);
    }

}
