package com.ecommerce.project.service;

import com.ecommerce.project.dto.UserRegisterDTO;
import com.ecommerce.project.dto.UserLoginDTO;
import com.ecommerce.project.dto.UserUpdateDTO;
import com.ecommerce.project.entity.User;

import java.util.List;

public interface UserService {

    User register(UserRegisterDTO dto);

    User login(UserLoginDTO dto);

    User getUserById(String userId);

    List<User> getAllUsers();

    User getUserWithAddresses(String userId);

    User addAddress(String userId, User.Address address);

    void deleteAddress(String userId, String addressId);

    User updateUser(String userId, UserUpdateDTO dto);
}
