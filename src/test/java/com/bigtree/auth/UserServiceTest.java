package com.bigtree.auth;

import com.bigtree.auth.model.ApiResponse;
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
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    @Order(1)
    public void testGetPrivateKeyJwt(){
        MultiValueMap map = new LinkedMultiValueMap();
        map.add("client_email", "TheCustomerApp@gmail.com");
        ApiResponse response = userService.getPrivateKeyJwt(map);
        Assertions.assertNull(response);
        Assertions.assertNotNull(response.getMessage());
    }
}
