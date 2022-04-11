package com.example.storageit.service;

import com.example.storageit.persistence.entity.*;
import com.example.storageit.persistence.repo.StorageRepo;
import com.example.storageit.rest.exception.NoVacantPlace;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class StorageService {

    private final static String STORAGE_NOT_FOUND =
            "storage with id %d not found";
    private final static String NO_VACANT_PLACE =
            "no empty room in storage with id %d";

    private final StorageRepo storageRepo;


    public Storage getStorageById(Long storageId)
            throws EntityNotFoundException {
        return storageRepo.findById(storageId).orElseThrow(() ->
                new EntityNotFoundException(String.format(STORAGE_NOT_FOUND, storageId)));
    }


    public Integer getEmptyPlaces(Long id)
            throws EntityNotFoundException {
        return storageRepo.getEmptySlots(id);

    }

    private Integer getCapacity(Long id)
            throws EntityNotFoundException {
        Storage storage = getStorageById(id);
        return storage.getCapacity();
    }

    public BigDecimal getDepthFor(Long id, Long storageId, LocalDate startDate, LocalDate endDate) {
        return storageRepo.getUsageFeeForPeriod(id, storageId, startDate, endDate);
    }


    public Storage addStorage(Storage storage) {
        if (storage.getId() == null || storageRepo.findById(storage.getId()).isEmpty())
            return storageRepo.save(storage);
        return storage;
    }

    public Storage move(Long oldStorageId, Long newStorageId, Integer size) {
        if (oldStorageId.equals(newStorageId))
            return getStorageById(oldStorageId);
        Storage storage = getStorageById(newStorageId);
        int capacity = getCapacity(storage.getId());
        int emptySlots = capacity - size;
        if (emptySlots <= 0) {
            throw new NoVacantPlace(String.format(NO_VACANT_PLACE, storage.getId()));
        }
        return storageRepo.save(storage);
    }
}
