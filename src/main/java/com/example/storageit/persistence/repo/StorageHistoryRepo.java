package com.example.storageit.persistence.repo;

import com.example.storageit.persistence.entity.StorageHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface StorageHistoryRepo extends CrudRepository<StorageHistory, Long> {

    @Transactional
    @Modifying
    @Query(value = "SELECT   " +
            "    t.taxable_amount_per_day * DATEDIFF(CASE  " +
            "                WHEN sh.startDate >= t.start_date THEN sh.startDate  " +
            "                ELSE t.start_date  " +
            "            END,  " +
            "            CASE  " +
            "                WHEN sh.endDate <= t.end_date THEN sh.endDate  " +
            "                ELSE t.end_date  " +
            "            END) AS depth  " +
            "FROM  " +
            "    registration.taxable_amount t,  " +
            "    (SELECT   " +
            "        s.type,  " +
            "            CASE  " +
            "                WHEN ?2 >= sh.usage_start_date THEN ?2  " +
            "                ELSE sh.usage_start_date  " +
            "            END AS startDate,  " +
            "            CASE  " +
            "                WHEN ?3 <= sh.usage_end_date THEN ?3  " +
            "                ELSE sh.usage_end_date  " +
            "            END AS endDate  " +
            "    FROM  " +
            "        registration.storage_history sh  " +
            "    INNER JOIN registration.storage s ON sh.storage_id = s.id  " +
            "    WHERE  " +
            "        sh.id = 1  " +
            "            AND ?2 <= sh.usage_start_date  " +
            "            AND ?3 >= sh.usage_end_date) sh  " +
            "WHERE  " +
            "    sh.startDate <= t.start_date  " +
            "        AND sh.endDate >= t.end_date  " +
            "        AND sh.type = t.storage_type  " +
            ";", nativeQuery = true)
    BigDecimal getUsagePeriod(Long storageId, LocalDate startDate, LocalDate endDate);

}
