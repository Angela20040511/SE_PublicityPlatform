package com.se.publicityplatform.service;

import com.se.publicityplatform.mapper.UserMapper;
import com.se.publicityplatform.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    void acceptsSeedDataDemoPassword() {
        UserMapper mapper = mock(UserMapper.class);
        User user = new User();
        user.setUsername("applicant01");
        user.setPassword("encrypted_123456");
        user.setStatus("enabled");
        when(mapper.findByUsername("applicant01")).thenReturn(user);

        AuthService service = new AuthService(mapper);

        assertSame(user, service.login("applicant01", "123456"));
    }

    @Test
    void rejectsDisabledUser() {
        UserMapper mapper = mock(UserMapper.class);
        User user = new User();
        user.setUsername("disabled");
        user.setPassword("encrypted_123456");
        user.setStatus("disabled");
        when(mapper.findByUsername("disabled")).thenReturn(user);

        AuthService service = new AuthService(mapper);

        assertNull(service.login("disabled", "123456"));
    }
}
