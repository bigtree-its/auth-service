package com.bigtree.auth;

import com.bigtree.auth.entity.Address;
import com.bigtree.auth.entity.Identity;
import com.bigtree.auth.entity.ClientType;
import com.bigtree.auth.model.UserRegistrationRequest;
import org.apache.commons.lang3.RandomStringUtils;

public class DummyData {

    public static final String CUSTOMER_EMAIL = "customer@gmail.com";
    public static final String SUPPLIER_EMAIL = "supplier@gmail.com";
    public static final String EMPLOYEE_EMAIL = "employee@gmail.com";
    public static final String CUSTOMER_APP_EMAIL = "customerApp@gmail.com";
    public static final String SUPPLIER_APP_EMAIL = "supplierApp@gmail.com";

    public static Address createDummyAddress() {
        return Address.builder()
                .addressLine1("359, My Road")
                .addressLine2("MyArea")
                .city("MyCity")
                .country("MyCountry")
                .longitude("MyLongitude")
                .latitude("MyLatitude")
                .postcode("MyPostcode")
                .build();
    }

    public static UserRegistrationRequest createRegisterRequest(ClientType clientType) {
        String clientEmail = "";
        switch (clientType){
            case CUSTOMER -> clientEmail = CUSTOMER_EMAIL;
            case SUPPLIER -> clientEmail = SUPPLIER_EMAIL;
            case EMPLOYEE -> clientEmail = EMPLOYEE_EMAIL;
            case CUSTOMER_APP -> clientEmail = CUSTOMER_APP_EMAIL;
            case SUPPLIER_APP -> clientEmail = SUPPLIER_APP_EMAIL;
        }
        return UserRegistrationRequest.builder()
                .clientType(clientType)
                .password("1234")
                .mobile("0987654321")
                .email(clientEmail)
                .firstName("firstName")
                .lastName("lastName")
                .build();
    }
}
