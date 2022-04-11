package com.example.storageit.rest.controller;

import com.example.storageit.persistence.entity.*;
import com.example.storageit.rest.dto.BillDto;
import com.example.storageit.rest.dto.MessageResponse;
import com.example.storageit.rest.dto.ProductDto;
import com.example.storageit.service.StorageService;
import com.example.storageit.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@AllArgsConstructor
@RequestMapping("/api/")
public class UserController {

    private final UserService userService;
    private final StorageService storageService;
    private final ModelMapper mapper;


    @GetMapping("/admin/products")
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<?> getProductsBy(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Double size,
            @RequestParam(required = false) StorageType storageType
    ) {
        List<Product> products = userService.getProductsBy(email, customerName, categoryName, size, null, storageType);
        List<ProductDto> productDtoList = products
                .stream().map(x -> mapper.map(x, ProductDto.class)).collect(Collectors.toList());
        return ResponseEntity.ok(productDtoList);
    }

    @GetMapping("/customers/products")
    @PreAuthorize("hasRole('BUSINESS') or hasRole('ADMIN')")
    public ResponseEntity<?> getProductsBy(
            @AuthenticationPrincipal User user
    ) {
        List<Product> products = userService.getProductsBy(user.getEmail(), null, null, null, null, null);
        List<ProductDto> productDtoList = products
                .stream().map(x -> mapper.map(x, ProductDto.class)).collect(Collectors.toList());
        return ResponseEntity.ok(productDtoList);
    }


    @GetMapping("/customers/bill")
    @PreAuthorize("hasRole('BUSINESS') or hasRole('ADMIN')")
    public ResponseEntity<?> getBills(
            @AuthenticationPrincipal User user
    ) {
        Bill bill = userService.generateBill(user.getId());
        return ResponseEntity.ok(mapper.map(bill, BillDto.class));
    }

    @PostMapping("/customers/products")
    @PreAuthorize("hasRole('INDIVIDUAL') or hasRole('BUSINESS')")
    public ResponseEntity<?> createProduct(
            @RequestBody ProductDto productDto,
            @AuthenticationPrincipal User user

    ) {
        Storage storage = storageService.getStorageById(productDto.getStorageId());
        Product product = mapper.map(productDto, Product.class);
        product.setStorage(storage);
        userService.addProduct(user.getId(), product);
        return ResponseEntity.ok(new MessageResponse("Product added successfully!"));
    }

    @PutMapping("/customers/products")
    @PreAuthorize("hasRole('INDIVIDUAL') or hasRole('BUSINESS')")
    public ResponseEntity<?> updateProduct(
            @RequestBody ProductDto productDto,
            @AuthenticationPrincipal User user

    ) {
        Product product = mapper.map(productDto, Product.class);
        userService.updateProduct(user.getId(), product);
        return ResponseEntity.ok(new MessageResponse("Product updated successfully!"));
    }

    @DeleteMapping("/customers/products/{productId}")
    @PreAuthorize("hasRole('INDIVIDUAL') or hasRole('BUSINESS')")
    public ResponseEntity<?> removeProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user
    ) {
        productId = userService.removeProduct(user.getId(), productId);
        return ResponseEntity.ok(new MessageResponse(
                String.format("Product with %d deleted successfully!", productId)));
    }

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/business")
    @PreAuthorize("hasRole('BUSINESS')")
    public String businessUserAccess() {
        return "Business user Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/admin/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setFreeStorageLimit(
            @RequestParam Integer storageLimit,
            @PathVariable Long customerId) {
        Long id = userService.setFreeStorageLimit(customerId, storageLimit).getId();
        return ResponseEntity.ok(new MessageResponse(
                String.format("Customer %d: storage limit set to %d successfully!", id, storageLimit)));
    }

    @GetMapping("/admin/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers() {
        List businessProfiles = userService.findAll();
        return ResponseEntity.ok(businessProfiles);
    }
}
