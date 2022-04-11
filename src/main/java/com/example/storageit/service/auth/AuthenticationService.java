package com.example.storageit.service.auth;

import com.example.storageit.persistence.entity.BusinessProfile;
import com.example.storageit.persistence.entity.PrivateProfile;
import com.example.storageit.persistence.entity.User;
import com.example.storageit.service.UserService;
import com.example.storageit.rest.dto.LoginRequest;
import com.example.storageit.rest.dto.RegistrationRequest;
import com.example.storageit.rest.dto.JwtResponse;
import com.example.storageit.service.email.MyEmailValidator;
import com.example.storageit.util.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserService<PrivateProfile> privateProfileUserService;
    private final UserService<BusinessProfile> businessProfileUserService;
    private final MyEmailValidator myEmailValidator;
    private final DaoAuthenticationProvider authenticationProvider;
    private final JwtUtils jwtUtils;


    public User register(RegistrationRequest request) {
        boolean isValidEmail = myEmailValidator.
                test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        if (request.getPrimaryContactEmail() != null) {
            BusinessProfile businessProfile = extractBusinessProfile(request);
            return businessProfileUserService.signUpUser(businessProfile);
        }
        PrivateProfile privateProfile = extractPersonProfile(request);
        return privateProfileUserService.signUpUser(privateProfile);
    }

    private PrivateProfile extractPersonProfile(RegistrationRequest request) {
        PrivateProfile userToRegister = new PrivateProfile(
                request.getEmail(),
                request.getPassword(),
                request.getStorageLimit(),
                request.getFirstName(),
                request.getLastName(),
                request.getDob()
        );

        return userToRegister;
    }

    private BusinessProfile extractBusinessProfile(RegistrationRequest request) {
        User primaryContact = (User) businessProfileUserService.loadUserByUsername(request.getPrimaryContactEmail());
        BusinessProfile userToRegister = new BusinessProfile(
                request.getEmail(),
                request.getPassword(),
                request.getStorageLimit(),
                primaryContact,
                request.getBusinessName()
        );
        return userToRegister;
    }

    public JwtResponse loginUser(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }
}
