package com.example.storageit.service;

import com.example.storageit.persistence.entity.*;
import com.example.storageit.persistence.repo.UserRepository;
import com.example.storageit.rest.exception.NoVacantPlace;
import com.example.storageit.rest.exception.NotAllowedException;
import com.example.storageit.rest.exception.StorageLimitExceeded;
import com.example.storageit.service.email.MyEmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService<T extends User> implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";
    private final static String PRODUCT_NOT_FOUND_MSG =
            "product with id %d not found";

    private final static String EMAIL_NOT_VALID_MSG =
            "invalid email format: %s";

    private final static String STORAGE_LIMIT_EXCEEDED_MSG =
            "storage limit exceeded";

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
                                String.format(USER_NOT_FOUND_MSG, email)));
    }


    public void addProduct(String email, Product product) {

        validateEmail(email);
        T user = (T) loadUserByUsername(email);

        if (user.getStorageLimit() - user.getProducts().size() == 0) {
            if (user.getUserRole().equals(UserRole.INDIVIDUAL))
                throw new StorageLimitExceeded(STORAGE_LIMIT_EXCEEDED_MSG);
        }

        List<Product> productList = user.getProducts();
        productList.add(product);
        userRepository.save(user);

    }


    //business
    public Bill generateBill(String email) throws NotAllowedException {

        T user = (T) loadUserByUsername(email);

        if (!user.getUserRole().equals(UserRole.BUSINESS))
            throw new NotAllowedException(String.format(INVALID_USER_TYPE_MSG, user.getUserRole()));

        List<Product> products = user.getProducts();

        List<BillItem> items = getBillItems(user, products);

        Bill bill = Bill.of(user);
        bill.setBillItems(items);
        List<Bill> bills = user.getBills();
        bills.add(bill);
        userRepository.save(user);
        return bill;
    }

    //businessProfile
    private List<BillItem> getBillItems(T user, List<Product> products) {
        int numOfNonFreeStorageUsed = user.getStorageLimit() - products.size();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(currentDate.getDayOfMonth() - 1);
        LocalDate endDate = startDate.plusMonths(1);
        List<BillItem> items = products
                .stream()
                .skip(Math.max(products.size() - numOfNonFreeStorageUsed, 0))
                .map(x -> BillItem.of(x.getStorage().getType(), storageService.getDepthFor(x.getStorage().getId(), startDate, endDate)))
                .collect(Collectors.toList());
        return items;
    }

    public void updateProduct(String email, Product product) {
        validateEmail(email);

        T user = (T) loadUserByUsername(email);
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

    public void removeProduct(String email, Product product) {
        validateEmail(email);

        T user = (T) loadUserByUsername(email);
        List<Product> productList = user.getProducts();

        productList.removeIf(x -> x.getId().equals(product.getId()));
        userRepository.save(user);

    }

    public void changeProductStorageLocation(String email, Long productId, Storage storage)
            throws NoVacantPlace {
        T user = (T) loadUserByUsername(email);

        Product product = user.getProducts().stream()
                .filter(x -> x.getId().equals(productId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                String.format(PRODUCT_NOT_FOUND_MSG, productId)));

        storage = storageService.addStorage(storage);
        storageService.addProduct(product, storage);
        product.setStorage(storage);
        userRepository.save(user);
    }


    public void signUpUser(T user) {
        boolean userExists = userRepository
                .findByEmail(user.getEmail())
                .isPresent();

        if (userExists)
            throw new IllegalStateException("email already taken");

        String encodedPassword = bCryptPasswordEncoder
                .encode(user.getPassword());

        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }
}
