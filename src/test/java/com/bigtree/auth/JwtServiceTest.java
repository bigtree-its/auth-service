package com.bigtree.auth;

import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.entity.ClientType;
import com.bigtree.auth.security.JwtService;
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
        final String idToken = jwtService.generateIdToken(Identity.builder()
                .email("test@gmail.com")
                ._id("auth-id")
                .clientType(ClientType.SUPPLIER)
                .firstName("firstName")
                .lastName("lastName")
                .mobile("9897545454545")
                .build());
        final String accessToken = jwtService.generateAccessToken(Identity.builder().build());
        Assertions.assertNotNull(idToken);
        Assertions.assertNotNull(accessToken);
        Assertions.assertNotNull(jwtService.validateAccessToken(accessToken));

    }
}
