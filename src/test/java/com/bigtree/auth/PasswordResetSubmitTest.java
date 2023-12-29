package com.bigtree.auth;

import com.bigtree.auth.model.PasswordResetSubmit;
import com.bigtree.auth.service.LoginService;
import com.bigtree.auth.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PasswordResetSubmitTest {
    @Autowired
    LoginService loginService;

    @Autowired
    UserService userService;


    @BeforeAll
    void setUpFixture() {

    }

    @Test
    public void testResetSubmit(){
        loginService.passwordResetSubmit(PasswordResetSubmit.builder()
                        .email("nava.arul@gmail.com")
                        .otp("134462")
                        .password("4321")
                .build());
    }
}
