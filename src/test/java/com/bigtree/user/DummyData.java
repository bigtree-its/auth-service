package com.bigtree.user;

import com.bigtree.user.entity.Address;
import com.bigtree.user.entity.User;
import com.bigtree.user.entity.UserType;
import com.bigtree.user.model.UserRegistrationRequest;
import org.apache.commons.lang3.RandomStringUtils;

public class DummyData {

    public static User createDummyUser(){
        String salt = RandomStringUtils.random(4, "1234");
        return User.builder()
                .email(salt+ "_fakeuser@gmail.com")
                .mobile("00000000000")
                .firstName("firstname")
                .lastName("lastname")
                .build();
    }

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

    public static UserRegistrationRequest createDummyUserRegReq() {
        String randomUserEmail = RandomStringUtils.random(5, "12345abcdef");
        return UserRegistrationRequest.builder()
                .userType(UserType.SUPPLIER)
                .password("1234")
                .mobile("0987654321")
                .email(randomUserEmail + "user@mail.com")
                .firstName("user")
                .lastName("user")
                .build();
    }
}
