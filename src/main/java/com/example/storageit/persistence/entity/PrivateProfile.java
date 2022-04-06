package com.example.storageit.persistence.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(UserRole.Roles.INDIVIDUAL)
public class PrivateProfile extends User {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dob;


    public PrivateProfile(String email, String password, Integer storageLimit,
                          String firstName, String lastName, LocalDate dob) {
        super(email, password, UserRole.INDIVIDUAL, storageLimit);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }
}
