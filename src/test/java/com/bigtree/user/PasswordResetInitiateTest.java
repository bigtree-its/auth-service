package com.bigtree.user;

import com.bigtree.user.entity.PasswordResetOtp;
import com.bigtree.user.model.UserRegistrationRequest;
import com.bigtree.user.service.LoginService;
import com.bigtree.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        dummyUserRegReq = DummyData.createDummyUserRegReq();
    }

    @Test
    public void testGenerateOtp(){
        final PasswordResetOtp otp = loginService.passwordResetInitiate("nava.arul@gmail.com");
        Assertions.assertNotNull(otp);

    }
}
