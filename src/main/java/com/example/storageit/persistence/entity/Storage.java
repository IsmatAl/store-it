package com.example.storageit.persistence.entity;


import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Storage {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageType type;

    @Column
    private String location;

    @Column
    private Integer capacity;

    @Column
    @OneToMany
    @ToString.Exclude
    private List<Product> products;

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

