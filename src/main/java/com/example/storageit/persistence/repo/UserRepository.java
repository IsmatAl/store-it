package com.example.storageit.persistence.repo;

import com.example.storageit.persistence.entity.Product;
import com.example.storageit.persistence.entity.StorageType;
import com.example.storageit.persistence.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository<T extends User>
        extends CrudRepository<T, Long> {

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableUser(String email);

    @Transactional
    @Modifying
    @Query("select p from User u inner join u.products p " +
            "where (?1 is null or u.email = ?1) " +
            "  and (?2 is null or p.name = ?2)" +
            "  and (?3 is null or p.categoryName = ?3)" +
            "  and (?4 is null or p.size = ?4)" +
            "  and (?5 is null or p.storage.id = ?5)" +
            "  and (?6 is null or p.storage.type = ?6)")
    List<Product> findProductsBy(String email, String name, String categoryName, Double size, Long storageId, StorageType storageType);
}
