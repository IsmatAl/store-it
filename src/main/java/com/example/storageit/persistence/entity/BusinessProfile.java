package com.example.storageit.persistence.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(UserRole.Roles.BUSINESS)
public class BusinessProfile extends User {

    @OneToOne
    @JoinColumn
    private PrivateProfile primaryContact;

    @Column(name = "business_name")
    private String businessName;

    public BusinessProfile(String email, String password, Integer storageLimit,
                           User primaryContact, String businessName) {

        super(email, password, UserRole.BUSINESS, storageLimit);
        this.primaryContact = (PrivateProfile) primaryContact;
        this.businessName = businessName;


    }

    @OneToMany(cascade={CascadeType.ALL})
    @ToString.Exclude
    private List<Bill> bills;
}
