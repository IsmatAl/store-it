package com.example.storageit.rest.dto;

import com.example.storageit.persistence.entity.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final Integer storageLimit;
    private final LocalDate dob;

    private final String primaryContactEmail;
    private final String businessName;
    private final String businessPhoneNumber;
}

