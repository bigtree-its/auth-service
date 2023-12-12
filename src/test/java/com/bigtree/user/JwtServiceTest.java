package com.bigtree.user;

import com.bigtree.user.entity.User;
import com.bigtree.user.entity.UserType;
import com.bigtree.user.security.JwtService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                ._id("user-id")
                .userType(UserType.SUPPLIER)
                .firstName("firstName")
                .lastName("lastName")
                .mobile("9897545454545")
                .build());
        final String accessToken = jwtService.generateAccessToken("user@mail.com");
        Assertions.assertNotNull(idToken);
        Assertions.assertNotNull(accessToken);
        Assertions.assertTrue(jwtService.validateAccessToken(accessToken));

    }
}
