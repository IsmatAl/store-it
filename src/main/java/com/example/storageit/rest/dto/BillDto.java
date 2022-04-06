package com.example.storageit.rest.dto;

import com.example.storageit.persistence.entity.User;
import com.example.storageit.service.OnUpdate;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class BillDto {

    @NotNull(groups = OnUpdate.class, message = "Bill id is missing")
    private Long id;

    private String description;

    @NotNull
    @NotEmpty(message = "Bill number missing")
    private String billNum;

    @NotNull(message = "user for the bill missing")
    private User user;

    @NotNull(message = "user for the bill missing")
    private BigDecimal totalAmount;

    @NotNull
    @NotEmpty(message = "password missing")
    private String password;

}
