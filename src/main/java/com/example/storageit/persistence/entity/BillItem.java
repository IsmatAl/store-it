package com.example.storageit.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private StorageType storageType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public static BillItem of(StorageType storageType, BigDecimal amount) {
        BillItem item = new BillItem();
        item.storageType = storageType;
        item.amount = amount;
        return item;
    }

}
