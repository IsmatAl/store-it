package com.example.storageit.rest.controller;


import com.example.storageit.persistence.entity.Storage;
import com.example.storageit.rest.dto.MessageResponse;
import com.example.storageit.rest.dto.StorageDto;
import com.example.storageit.service.StorageService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@AllArgsConstructor
@RequestMapping("/api/")
public class StorageController {

    private final StorageService storageService;
    private final ModelMapper mapper;


    @PostMapping("/storage")
    @PreAuthorize("hasRole('INDIVIDUAL') or hasRole('BUSINESS')")
    public ResponseEntity<?> createStorage(@RequestBody StorageDto storage) {
        Long id = storageService.addStorage(mapper.map(storage, Storage.class)).getId();
        return ResponseEntity.ok(new MessageResponse(String.format("Storage created with id %d successfully", id)));
    }

}
