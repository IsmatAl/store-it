package com.example.storageit.service;

import com.example.storageit.persistence.entity.*;
import com.example.storageit.persistence.repo.StorageHistoryRepo;
import com.example.storageit.persistence.repo.StorageRepo;
import com.example.storageit.persistence.repo.TaxableAmountRepo;
import com.example.storageit.rest.exception.NoVacantPlace;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class StorageService {

    private final static String STORAGE_NOT_FOUND =
            "storage with id %d not found";
    private final static String NO_VACANT_PLACE =
            "no empty room in storage with id %d";

    private final StorageRepo storageRepo;
    private final StorageHistoryRepo storageHistoryRepo;
    private final TaxableAmountRepo taxableAmountRepo;

    public Storage getStorageById(Long storageId)
            throws EntityNotFoundException {
        return storageRepo.findById(storageId).orElseThrow(() ->
                new EntityNotFoundException(String.format(STORAGE_NOT_FOUND, storageId)));
    }


    public Integer getVacantPlaces(Long id)
            throws EntityNotFoundException {
        Storage storage = getStorageById(id);
        return storage.getCapacity() - storage.getProducts().size();
    }

    public BigDecimal getDepthFor(Long id, LocalDate startDate, LocalDate endDate) {
        return storageHistoryRepo.getUsagePeriod(id, startDate, endDate);
    }

//
//    public BigDecimal getPriceByType(StorageType storageType) {
//        taxableAmountRepo.fin
//    }

    public Storage addStorage(Storage storage) {
        if (storage.getId() == null || storageRepo.findById(storage.getId()).isEmpty())
            return storageRepo.save(storage);
        return storage;
    }

    public void addProduct(Product product, Storage storage) {
        int vacantPlaces = getVacantPlaces(storage.getId());
        if (vacantPlaces == 0) {
            throw new NoVacantPlace(String.format(NO_VACANT_PLACE, storage.getId()));
        }
        storage.getProducts().add(product);
        storageRepo.save(storage);
    }
}
