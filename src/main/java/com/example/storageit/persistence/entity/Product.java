package com.example.storageit.persistence.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "serial_number")
    private String serialNum;

    @Column
    private BigDecimal price;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "storage_location_id")
    @ToString.Exclude
    private Storage storage;

    @Column
    private Double size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product product = (Product) o;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static Product of(String name, String serialNum, Double price, String categoryName, Double size) {
        Product product = new Product();
        product.name = name;
        product.serialNum = serialNum;
        product.price = BigDecimal.valueOf(price);
        product.categoryName= categoryName;
        product.size= size;
        return product;
    }
}
