package com.bigtree.user;

import com.bigtree.user.entity.PasswordResetOtp;
import com.bigtree.user.entity.User;
import com.bigtree.user.model.LoginRequest;
import com.bigtree.user.model.LoginResponse;
import com.bigtree.user.model.UserRegistrationRequest;
import com.bigtree.user.service.LoginService;
import com.bigtree.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginServiceTest {

    @Autowired
    LoginService loginService;

    @Autowired
    UserService userService;
    private UserRegistrationRequest dummyUserRegReq;

    @BeforeAll
    void setUpFixture() {
        dummyUserRegReq = DummyData.createDummyUserRegReq();
    }

    @Test()
    @Order(1)
    public void signup() {
        boolean result = userService.registerUser(dummyUserRegReq);
        Assertions.assertTrue(result);
    }

    @Test
    @Order(2)
    public void testLoginSuccess() {
        User byEmail = userService.findByEmailAndUserType(dummyUserRegReq.getEmail(), dummyUserRegReq.getUserType());
        LoginResponse login = loginService.login(LoginRequest.builder()
                .email(byEmail.getEmail())
                .password(dummyUserRegReq.getPassword())
                .userType(dummyUserRegReq.getUserType())
                .build());
        Assertions.assertTrue(login != null);
        Assertions.assertTrue(login.getSuccess());
        Assertions.assertNotNull(login.getAccessToken());

    }

    @Test
    @Order(3)
    public void testLoginFail() {
        User byEmail = userService.findByEmailAndUserType(dummyUserRegReq.getEmail(), dummyUserRegReq.getUserType());
        LoginResponse login = loginService.login(LoginRequest.builder()
                .email(byEmail.getEmail())
                .password("wrongpassword")
                .userType(byEmail.getUserType())
                .build());
        Assertions.assertTrue(login != null);
        Assertions.assertFalse(login.getSuccess());
        Assertions.assertNull(login.getAccessToken());

    }

    @Test
    @Order(4)
    public void resetInitiate() {
        User byEmail = userService.findByEmailAndUserType(dummyUserRegReq.getEmail(), dummyUserRegReq.getUserType());
        PasswordResetOtp passwordResetOtp = loginService.passwordResetInitiate(byEmail.getEmail());
        Assertions.assertNotNull(passwordResetOtp);
        Assertions.assertNotNull(passwordResetOtp.getOtp());
        Assertions.assertNotNull(passwordResetOtp.getUserId());
        Assertions.assertNotNull(passwordResetOtp.get_id());
    }
}
