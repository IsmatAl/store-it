package com.example.storageit.persistence.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@Audited
@RequiredArgsConstructor
public class Storage {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "parent_id")
    private Long parentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageType type;

    @Column
    private String location;

    @Column(nullable = false, name = "taxable_amount_per_day")
    private BigDecimal taxableAmountPerDay;

    @Column(nullable = false)
    private Integer capacity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Storage storage = (Storage) o;
        return id != null && Objects.equals(id, storage.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}

