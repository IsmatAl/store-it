package com.example.storageit.service;

import com.example.storageit.persistence.entity.*;
import com.example.storageit.persistence.repo.UserRepository;
import com.example.storageit.rest.exception.NoVacantPlace;
import com.example.storageit.rest.exception.NotAllowedException;
import com.example.storageit.rest.exception.StorageLimitExceeded;
import com.example.storageit.service.email.MyEmailValidator;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class UserService<T extends User> implements UserDetailsService {

    private final static String USER_EMAIL_NOT_FOUND_MSG =
            "user with email %s not found";
    private final static String USER_ID_NOT_FOUND_MSG =
            "user with email %s not found";
    private final static String PRODUCT_NOT_FOUND_MSG =
            "product with id %d not found";

    private final static String EMAIL_NOT_VALID_MSG =
            "invalid email format: %s";

    private final static String USER_STORAGE_LIMIT_EXCEEDED_MSG =
            "storage limit for user %s exceeded";

    private final static String NO_EMPTY_STORAGE_SLOT_MSG =
            "no slot available in storage with id %d";

    private final static String INVALID_USER_TYPE_MSG =
            "operation not supported for user with type %s";

    private final UserRepository<T> userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MyEmailValidator myEmailValidator;
    private final StorageService storageService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
    }

    public UserDetails loadUserById(Long id)
            throws UsernameNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_ID_NOT_FOUND_MSG, id)));
    }


    public void addProduct(Long customerId, Product product) {

        T user = (T) loadUserById(customerId);

        if (user.getStorageLimit() - user.getProducts().size() == 0) {
            if (user.getUserRole().equals(UserRole.INDIVIDUAL))
                throw new StorageLimitExceeded(
                        String.format(USER_STORAGE_LIMIT_EXCEEDED_MSG, user.getEmail()));
        }

        Storage storage = product.getStorage();
        if (storage.getCapacity() - storageService.getEmptyPlaces(storage.getId()) <= 0)
            throw new StorageLimitExceeded(
                    String.format(NO_EMPTY_STORAGE_SLOT_MSG, storage.getId()));

        List<Product> productList = user.getProducts();
        productList.add(product);
        userRepository.save(user);

    }


    public Bill generateBill(Long customerId) throws NotAllowedException {

        T user = (T) loadUserById(customerId);

        if (!user.getUserRole().equals(UserRole.BUSINESS))
            throw new NotAllowedException(String.format(INVALID_USER_TYPE_MSG, user.getUserRole()));

        List<Product> products = user.getProducts();

        List<BillItem> items = getBillItems(user, products);
        BigDecimal totalAmount = items
                .stream()
                .map(BillItem::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
        Bill bill = Bill.of(user, totalAmount);
        bill.setBillItems(items);
        List<Bill> bills = user.getBills();
        bills.add(bill);
        userRepository.save(user);

        return bill;
    }

    private List<BillItem> getBillItems(T user, List<Product> products) {
        int storageLimit = user.getStorageLimit();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(currentDate.getDayOfMonth());
        LocalDate endDate = startDate.plusMonths(1);
        List<BillItem> items = products
                .stream()
                .skip(storageLimit)
                .map(x -> BillItem.of(x.getStorage().getType(), storageService.getDepthFor(user.getId(), x.getStorage().getId(), startDate, endDate)))
                .collect(Collectors.toList());
        return items;
    }

    public void updateProduct(Long customerId, Product product) {
        T user = (T) loadUserById(customerId);
        List<Product> productList = user.getProducts();
        productList.removeIf(x -> x.getId().equals(product.getId()));
        productList.add(product);
        userRepository.save(user);
    }

    private void validateEmail(String email)
            throws IllegalStateException {
        boolean isValidEmail = myEmailValidator.
                test(email);
        if (!isValidEmail) {
            throw new IllegalStateException(String.format(EMAIL_NOT_VALID_MSG, email));
        }
    }

    public List<Product> getProductsBy(String email, String name, String categoryName, Double size, Long storageId, StorageType storageType) {
        validateEmail(email);
        loadUserByUsername(email);
        List<Product> products = userRepository.findProductsBy(email, name, categoryName, size, storageId, storageType);
        return products;
    }

    public Long removeProduct(Long customerId, Long productId) {
        T user = (T) loadUserById(customerId);
        List<Product> productList = user.getProducts();
        productList
                .stream()
                .filter(x -> x.getId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format(PRODUCT_NOT_FOUND_MSG, productId)));
        productList.remove(productId);
        userRepository.save(user);
        return productId;

    }

    public void changeProductStorageLocation(Long customerId, Long productId, Long storageId)
            throws NoVacantPlace {
        T user = (T) loadUserById(customerId);
        Product product = loadProductById(productId, user);
        int size = userRepository
                .findProductsBy(null, null, null, null, storageId, null)
                .size();
        product.setStorage(storageService.move(product.getStorage().getId(), storageId, size));
        userRepository.save(user);
    }

    private Product loadProductById(Long productId, T user) {
        return user.getProducts().stream()
                .filter(x -> x.getId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                String.format(PRODUCT_NOT_FOUND_MSG, productId)));
    }


    public T signUpUser(T user) {
        boolean userExists = userRepository
                .findByEmail(user.getEmail())
                .isPresent();

        if (userExists)
            throw new IllegalStateException("email already taken");

        String encodedPassword = bCryptPasswordEncoder
                .encode(user.getPassword());

        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }


    public User setFreeStorageLimit(Long userId, Integer storageLimit) {
        T user = (T) loadUserById(userId);
        user.setStorageLimit(storageLimit);
        return userRepository.save(user);
    }

    public List<T> findAll() {
//    public List<T> findAll(UserRole userRole) {
        return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
//                .filter(x->x.getUserRole().equals(userRole))
                .collect(Collectors.toList());

    }

    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }
}
