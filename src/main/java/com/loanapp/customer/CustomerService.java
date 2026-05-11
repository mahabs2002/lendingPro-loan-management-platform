package com.loanapp.customer;

import com.loanapp.auth.UserRepository;
import com.loanapp.customer.dto.CustomerProfileDto;
import com.loanapp.entity.*;
import com.loanapp.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerProfile createOrUpdateProfile(Long userId, CustomerProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomerProfile profile = customerRepository.findByUserId(userId)
                .orElse(new CustomerProfile());

        profile.setUser(user);
        profile.setDob(dto.getDob());
        profile.setGender(dto.getGender());
        profile.setEmploymentType(dto.getEmploymentType());
        profile.setEmployerName(dto.getEmployerName());
        profile.setMonthlyIncome(dto.getMonthlyIncome());
        profile.setAddressLine1(dto.getAddressLine1());
        profile.setCity(dto.getCity());
        profile.setState(dto.getState());
        profile.setPincode(dto.getPincode());

        return customerRepository.save(profile);
    }

    public CustomerProfile getProfile(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
    }
}
