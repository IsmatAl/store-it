package com.example.storageit.persistence.repo;

import com.example.storageit.persistence.entity.StorageType;
import com.example.storageit.persistence.entity.TaxableAmount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface TaxableAmountRepo extends CrudRepository<TaxableAmount, Long> {

    @Transactional
    @Modifying
    @Query("select count(sh) " +
            "from StorageHistory sh " +
            "where sh.user.id = ?1 " +
            "  and ?2 >= sh.startDate " +
            "  and (?2 < sh.endDate " +
            "         or sh.endDate is null)")
    Long getTaxableAmountBy(StorageType type, LocalDate month);
}
