package com.ecommerce.project.service;

import com.ecommerce.project.dto.UserRegisterDTO;
import com.ecommerce.project.dto.UserLoginDTO;
import com.ecommerce.project.entity.User;

public interface UserService {

    User register(UserRegisterDTO dto);

    User login(UserLoginDTO dto);

    User getUserById(String userId);

    User addAddress(String userId, User.Address address);

    void deleteAddress(String userId, String addressId);
}
