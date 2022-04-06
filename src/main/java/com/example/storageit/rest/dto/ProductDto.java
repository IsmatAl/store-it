package com.example.storageit.rest.dto;


import com.example.storageit.persistence.entity.Storage;
import com.example.storageit.service.OnUpdate;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ProductDto {

    @NotNull(groups = OnUpdate.class, message = "product id missing")
    private Long id;

    @NotNull(message = "product name missing")
    @NotEmpty(message = "product name cannot be empty")
    private String name;

    private String serialNum;

    private BigDecimal price;

    @NotNull(message = "category missing")
    @NotEmpty(message = "category name cannot be empty")
    private String categoryName;

    @NotNull(message = "Storage missing")
    private StorageDto storage;

    @Max(value = 25, message = "product size may not be bigger than 25 m2")
    private double size;
}
