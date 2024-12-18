package com.bigtree.auth;

import com.bigtree.auth.entity.Address;
import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.model.UserRegistrationRequest;

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

    public static UserRegistrationRequest createRegisterRequest(UserType userType) {
        String clientEmail = "";
        switch (userType){
                    case Customer -> clientEmail = CUSTOMER_EMAIL;
                    case Business -> clientEmail = SUPPLIER_EMAIL;
                    case Employee -> clientEmail = EMPLOYEE_EMAIL;
                    case CustomerApp -> clientEmail = CUSTOMER_APP_EMAIL;
                    case SupplierApp -> clientEmail = SUPPLIER_APP_EMAIL;
                    case Admin -> throw new UnsupportedOperationException("Unimplemented case: " + userType);
                    default -> throw new IllegalArgumentException("Unexpected value: " + userType);
        }
        return UserRegistrationRequest.builder()
                .userType(userType)
                .password("1234")
                .mobile("0987654321")
                .email(clientEmail)
                .name("Name")
                .build();
    }
}
