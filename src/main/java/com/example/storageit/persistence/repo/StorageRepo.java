package com.example.storageit.persistence.repo;

import com.example.storageit.persistence.entity.Storage;
import com.example.storageit.persistence.entity.StorageHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StorageRepo extends CrudRepository<Storage, Long> {
    @Transactional
    @Modifying
    @Query("select count(p) from Product p where p.storage.id = ?1")
    Integer countOccupiedSlots(Long storageId);

}
