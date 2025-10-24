package com.example.cinema_booking.service;

import com.example.cinema_booking.dto.AccountDTO;
import com.example.cinema_booking.model.Account;
import com.example.cinema_booking.repository.AccountRepository;
import com.example.cinema_booking.dto.request.UserUpdateRequest;
import com.example.cinema_booking.utils.MaskingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private AccountRepository accountRepository;

    public Account updateUser(int userId, UserUpdateRequest userUpdateRequest) {
        Optional<Account> account = accountRepository.findById(userId);
        if(account.isPresent()) {
            Account us = account.get();
            us.setSex(userUpdateRequest.getSex());
            us.setDateOfBirth(userUpdateRequest.getDateOfBirth());
//            if(userUpdateRequest.getRoleId() != 0) {
//                try {
//                    Role role = roleRepository.findById(userUpdateRequest.getRoleId()).orElseThrow(() -> new RoleNotFoundException("Role not found"));
//                    us.setRole(role);
//                } catch (RoleNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//            }
            accountRepository.save(us);
            return us;
        }else {
            throw new RuntimeException("User with ID " + userId + " not found");
        }
    }

    public void deleteUser(int userId) {
        Optional<Account> account = accountRepository.findById(userId);
        if(account.isPresent()) {
            accountRepository.delete(account.get());
        }
    }

    public AccountDTO getUserById(int userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        String maskedEmail = MaskingUtil.maskEmail(account.getEmail());
        String maskedPhoneNumber = MaskingUtil.maskPhoneNumber(account.getPhone());
        return new AccountDTO(account.getId(), maskedEmail, maskedPhoneNumber, account.getFullName(),
                account.getSex(), account.getDateOfBirth());
    }
}
