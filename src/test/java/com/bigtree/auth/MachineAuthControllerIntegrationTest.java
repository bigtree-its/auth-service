package com.bigtree.auth;

import com.bigtree.auth.entity.ClientType;
import com.bigtree.auth.model.TokenResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Disabled
@SpringBootTest(classes = AuthApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MachineAuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void tokenSupplierApp(){
        MultiValueMap map = new LinkedMultiValueMap();
        map.add("client_id", "hoc-sapp-463363");
        map.add("secret", "6ii7ifn14nhj");
        map.add("grant_type", "client_credential");
        map.add("client_type", ClientType.SupplierApp.getName());
        ResponseEntity<String> responseEntity = this.restTemplate
                .postForEntity("http://localhost:" + port + "/oauth/token", map, String.class);
        Assertions.assertEquals(201, responseEntity.getStatusCodeValue());
    }
}
