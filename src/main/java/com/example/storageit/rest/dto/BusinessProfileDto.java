package com.example.storageit.rest.dto;

import com.example.storageit.service.OnUpdate;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class BusinessProfileDto {

    @NotNull(groups = OnUpdate.class, message = "id is missing")
    private Long id;

    @NotNull
    private Long primaryContactUserId;

    @NotNull
    @NotEmpty(message = "Business name missing")
    private String businessName;
}
