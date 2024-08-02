package com.bigtree.auth;

import com.bigtree.auth.controller.MachineAuthController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(MachineAuthController.class)
@Disabled
public class MachineTokenControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void loginSupplierApp() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/token")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[*].employeeId").isNotEmpty());
    }
}
