package com.example.storageit.persistence.repo;

import com.example.storageit.persistence.entity.Storage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface StorageRepo extends CrudRepository<Storage, Long> {
    @Transactional
    @Modifying
    @Query("select count(p) from Product p where p.storage.id = ?1")
    Integer countOccupiedSlots(Long storageId);

    @Transactional
    @Query(value = "select   " +
            "    sum(cast(b.taxable_amount_per_day AS DECIMAL(30, 20)) / (24*3600) *  " +
            " TIMESTAMPDIFF(SECOND,  " +
            "case   " +
            "when a.p_start_date >= b.s_start_date then a.p_start_date  " +
            "        else b.s_start_date  " +
            "end,  " +
            "case   " +
            "when a.p_end_date >= b.s_end_date then b.s_end_date  " +
            "        else a.p_end_date  " +
            "end   " +
            " )) amount_to_pay  " +
            "  " +
            " from  " +
            "(select pa.id,  " +
            "   pa.storage_id,  " +
            "   FROM_UNIXTIME(par.revtstmp/1000) p_start_date,   " +
            "   coalesce(lead(FROM_UNIXTIME(par.revtstmp/1000)) over (partition by id order by id), CURRENT_TIMESTAMP) p_end_date  " +
            "from registration.product_aud pa  " +
            "inner join registration.revinfo par  " +
            "on pa.rev = par.rev  " +
            "    and pa.revtype in (0, 1)  " +
            ")  a inner join   " +
            "(  " +
            "select sa.id,  " +
            "   sa.taxable_amount_per_day,  " +
            "FROM_UNIXTIME(sar.revtstmp/1000) s_start_date,   " +
            "coalesce(lead(FROM_UNIXTIME(sar.revtstmp/1000)) over (partition by id order by id), CURRENT_TIMESTAMP) s_end_date  " +
            "from registration.storage_aud sa  " +
            "inner join registration.revinfo sar  " +
            "on sa.rev = sar.rev  " +
            "and sa.revtype in (0, 1)  " +
            ") b  " +
            "on a.storage_id = b.id  " +
            "inner join registration.user_products up  " +
            "on up.products_id = a.id         " +
            "where (1=1)  " +
            " and up.user_id = ?1  " +
            " and a.storage_id = ?2  " +
            " and a.p_start_date <= ?4   " +
            "     and a.p_end_date > ?3  " +
            "     and b.s_start_date <= ?4   " +
            "     and b.s_end_date > ?3  " +
            ";  ", nativeQuery = true)
    BigDecimal getUsageFeeForPeriod(Long id, Long storageId, LocalDate startDate, LocalDate endDate);

    @Transactional
    @Query("select " +
            "   count(p.id) " +
            "from Product p inner join p.storage s " +
            "where p.storage.id = ?1")
    Integer getEmptySlots(Long id);

}
