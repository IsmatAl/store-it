package com.example.storageit.rest.dto;

import com.example.storageit.CustomValidator.ValueOfEnum;
import com.example.storageit.persistence.entity.UserRole;
import com.example.storageit.service.OnUpdate;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Accessors(chain = true)
public class UserDto {

    @NotNull(groups = OnUpdate.class, message = "user id is missing")
    private Long id;

    @NotNull
    @NotEmpty(message = "First name missing")
    private String firstName;

    @NotNull
    @NotEmpty(message = "First name missing")
    private String lastName;

    @NotNull
    @NotEmpty(message = "email code must be specified")
    @Email(message = "email is not in correct format")
    private String email;

    @NotNull
    @NotEmpty(message = "password missing")
    private String password;

    @ValueOfEnum(enumClass = UserRole.class, message = "user role missing")
    private UserRole userRole;

    @NotNull(message = "storage limit missing")
    private Integer storageLimit;

    private List<ProductDto> products;
    private List<BillDto> bills;
}
