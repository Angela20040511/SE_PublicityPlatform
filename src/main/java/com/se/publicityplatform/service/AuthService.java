package com.se.publicityplatform.service;

import com.se.publicityplatform.mapper.UserMapper;
import com.se.publicityplatform.model.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final UserMapper userMapper;

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }
        User user = userMapper.findByUsername(username.trim());
        if (user == null || !"enabled".equals(user.getStatus())) {
            return null;
        }
        if (matchesDemoPassword(user.getPassword(), password)) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    boolean matchesDemoPassword(String storedPassword, String rawPassword) {
        return storedPassword != null
                && (storedPassword.equals(rawPassword) || storedPassword.equals("encrypted_" + rawPassword));
    }
}
