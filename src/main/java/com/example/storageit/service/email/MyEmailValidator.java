package com.example.storageit.service.email;

import org.apache.commons.validator.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class MyEmailValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        return EmailValidator.getInstance()
                .isValid(s);
    }
}
