package com.bigtree.auth;

import com.bigtree.auth.entity.User;
import com.bigtree.auth.entity.UserType;
import com.bigtree.auth.entity.Account;
import com.bigtree.auth.model.UserRegistrationRequest;
import com.bigtree.auth.repository.AccountRepository;
import com.bigtree.auth.service.UserService;
import org.bson.assertions.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class AuthApplicationTests {

	@Autowired
	UserService userService;

	@Autowired
    AccountRepository accountRepository;

	@Test
	void testSaveOrUpdateUser() {
		UserRegistrationRequest userRegReq = DummyData.createRegisterRequest(UserType.Customer);
		userService.registerUser(userRegReq);
		User saved = userService.findByEmailAndUserType(userRegReq.getEmail(),userRegReq.getUserType());
		Assertions.notNull("Identity not saved", saved);
		Assertions.notNull("Identity id is empty", saved.get_id());

		Account account = accountRepository.findByUserId(saved.get_id());
		Assertions.notNull("Identity account not created ", account);
		Assertions.isTrue("Identity accountId not created ", account.getUserId().equals(saved.get_id()));
		Assertions.isTrue("Identity password not stored correctly ", account.getPassword().equals(userRegReq.getPassword()));

		saved.setEmail("updated@email.com");
		saved.setMobile("1111111111");
		saved.setName("UpdatedName");
		User updated = userService.updateUser(saved.get_id(), saved);

		Assertions.notNull("Updated UserId is empty", updated.get_id());
		Assertions.isTrue("Updated UserId is changed", updated.get_id().equals(saved.get_id()));
		Assertions.notNull("Updated Identity Email is empty", updated.getEmail());
		Assertions.notNull("Updated Identity Mobile is empty", updated.getMobile());
		Assertions.notNull("Updated Identity FirstName is empty", updated.getName());

		User getById= userService.getUser(updated.get_id());
		Assertions.isTrue("Identity not found", getById != null);
		userService.deleteUser(updated.get_id());
	}

	@Test
	public void testDeleteAllUsers(){
		userService.deleteAll();
	}



}
