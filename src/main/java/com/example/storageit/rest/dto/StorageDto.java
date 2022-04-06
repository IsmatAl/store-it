package com.example.storageit.rest.dto;

import com.example.storageit.CustomValidator.ValueOfEnum;
import com.example.storageit.persistence.entity.StorageType;
import com.example.storageit.service.OnUpdate;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class StorageDto {

    @NotNull(groups = OnUpdate.class, message = "storage id missing")
    private Long id;

    @ValueOfEnum(enumClass = StorageType.class, message = "storage type missing")
    private String type;

    @NotNull
    @NotEmpty(message = "location missing")
    private String location;
}
