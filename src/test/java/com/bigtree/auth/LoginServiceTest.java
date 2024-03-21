package com.bigtree.auth;

import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.PasswordResetOtp;
import com.bigtree.auth.model.ApiResponse;
import com.bigtree.auth.model.TokenResponse;
import com.bigtree.auth.model.UserRegistrationRequest;
import com.bigtree.auth.service.LoginService;
import com.bigtree.auth.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Disabled
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
//        dummyUserRegReq = DummyData.createRegisterRequest();
    }

    @Test()
    @Order(1)
    public void signupCustomer() {
        final UserRegistrationRequest registerRequest = DummyData.createRegisterRequest(UserType.Customer);
        ApiResponse response  =  userService.registerUser(registerRequest);
        Assertions.assertNotNull(response);
    }

    @Test()
    @Order(2)
    public void signupSupplier() {
        final UserRegistrationRequest registerRequest = DummyData.createRegisterRequest(UserType.Supplier);
        ApiResponse response  =  userService.registerUser(registerRequest);
        Assertions.assertNotNull(response);
    }

    @Test()
    @Order(3)
    public void signupEmployee() {
        final UserRegistrationRequest registerRequest = DummyData.createRegisterRequest(UserType.Employee);
        ApiResponse response  =  userService.registerUser(registerRequest);
        Assertions.assertNotNull(response);
    }

    @Test()
    @Order(4)
    public void signupCustomerApp() {
        final UserRegistrationRequest registerRequest = DummyData.createRegisterRequest(UserType.CustomerApp);
        registerRequest.setEmail("TheCustomerApp@gmail.com");
        ApiResponse response  =  userService.registerUser(registerRequest);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getMessage());
    }

    @Test()
    @Order(5)
    public void signupSupplierApp() {
        final UserRegistrationRequest registerRequest = DummyData.createRegisterRequest(UserType.SupplierApp);
        registerRequest.setEmail("TheSupplierApp4@gmail.com");
        ApiResponse response  =  userService.registerUser(registerRequest);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    @Order(7)
    public void testCustomerLoginSuccess() {
        MultiValueMap map = new LinkedMultiValueMap();
        map.add("username",DummyData.CUSTOMER_EMAIL);
        map.add("password", "1234");
        map.add("grant_type", "password");
        map.add("client_type", UserType.Customer.getName());
        TokenResponse login = loginService.token(map);
        Assertions.assertTrue(login != null);
        Assertions.assertNotNull(login.getAccessToken());

    }

    @Test
    @Order(8)
    public void testLoginFail() {
        User byEmail = userService.findByEmailAndUserType(dummyUserRegReq.getEmail(), dummyUserRegReq.getUserType());
        MultiValueMap map = new LinkedMultiValueMap();
        TokenResponse login = loginService.token(map);
        Assertions.assertTrue(login != null);
        Assertions.assertNull(login.getAccessToken());

    }

    @Test
    @Order(9)
    public void resetInitiate() {
        User byEmail = userService.findByEmailAndUserType(dummyUserRegReq.getEmail(), dummyUserRegReq.getUserType());
        PasswordResetOtp passwordResetOtp = loginService.passwordResetInitiate(byEmail.getEmail());
        Assertions.assertNotNull(passwordResetOtp);
        Assertions.assertNotNull(passwordResetOtp.getOtp());
        Assertions.assertNotNull(passwordResetOtp.getUserId());
        Assertions.assertNotNull(passwordResetOtp.get_id());
    }


    @Test
    @Order(10)
    public void testSupplierAppLogin(){
        MultiValueMap map = new LinkedMultiValueMap();
        map.add("client_assertion_type", "private_key_jwt");
        map.add("client_type", UserType.SupplierApp.getName());
        map.add("grant_type", "client_credentials");
        map.add("client_assertion", "eyJhbGciOiJIUzUxMiJ9.eyJjbGllbnRfZW1haWwiOiJUaGVDdXN0b21lckFwcEBnbWFpbC5jb20iLCJjbGllbnRfc2VjcmV0IjoibmJtNDgyZWxtMmtiIiwiY2xpZW50X3R5cGUiOiJDdXN0b21lckFwcCIsImNsaWVudF9pZCI6ImhvYy1jYXBwLTMxNTQzMyIsImlzcyI6Ind3dy5hdXRoLmx1bmNoaWUtbXVuY2hpZS5jb20iLCJzdWIiOiJob2MtY2FwcC0zMTU0MzMiLCJpYXQiOjE3MDM3ODQ3MzYsImV4cCI6MTcwNTI1NTk2NX0.ZllDJ3yDO99Yf3zcVBzVmraUz3RUai-gg_35agegNDdSBLKHU21i0XKz8K8XGxu4vq1BHlbChf94gsdjR_whrg");
        TokenResponse login = loginService.token(map);
        Assertions.assertTrue(login != null);
        Assertions.assertNotNull(login.getAccessToken());
    }
}
