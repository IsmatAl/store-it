package com.example.storageit.persistence.entity;


import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "bill_number", nullable = false)
    private String billNum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private BusinessProfile user;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "due_to", nullable = false)
    private LocalDate dueTo;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(cascade={CascadeType.ALL})
    @ToString.Exclude
    private List<BillItem> billItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Bill bill = (Bill) o;
        return id != null && Objects.equals(id, bill.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static Bill of(User user) {
        Bill bill = new Bill();
        bill.user = (BusinessProfile) user;
        bill.createdAt = LocalDate.now();
        LocalDate currentDate = LocalDate.now();
        bill.dueTo = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 5);
        return bill;
    }
}
