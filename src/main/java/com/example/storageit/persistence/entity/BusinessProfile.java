package com.example.storageit.persistence.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
        BusinessProfile businessProfile
                 = new BusinessProfile();
        businessProfile.primaryContact = (PrivateProfile) primaryContact;
        businessProfile.businessName = businessName;


    }
}
