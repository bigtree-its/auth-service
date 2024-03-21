package com.bigtree.auth;

import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.entity.PasswordResetOtp;
import com.bigtree.auth.model.UserRegistrationRequest;
import com.bigtree.auth.service.LoginService;
import com.bigtree.auth.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PasswordResetInitiateTest {

    @Autowired
    LoginService loginService;

    @Autowired
    UserService userService;
    private UserRegistrationRequest dummyUserRegReq;

    @BeforeAll
    void setUpFixture() {
        dummyUserRegReq = DummyData.createRegisterRequest(UserType.Customer);
    }

    @Test
    public void testGenerateOtp(){
        final PasswordResetOtp otp = loginService.passwordResetInitiate("nava.arul@gmail.com");
        Assertions.assertNotNull(otp);

    }
}
