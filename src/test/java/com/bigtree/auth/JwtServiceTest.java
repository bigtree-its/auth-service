package com.bigtree.auth;

import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.security.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Test
    public void testGenerateAndValidateToken(){
        final String idToken = jwtService.generateIdToken(User.builder()
                .email("test@gmail.com")
                ._id("auth-id")
                .userType(UserType.Supplier)
                .firstName("firstName")
                .lastName("lastName")
                .mobile("9897545454545")
                .build());
        final String accessToken = jwtService.generateAccessToken(User.builder().build());
        Assertions.assertNotNull(idToken);
        Assertions.assertNotNull(accessToken);
        Assertions.assertNotNull(jwtService.validateAccessToken(accessToken));

    }
}
