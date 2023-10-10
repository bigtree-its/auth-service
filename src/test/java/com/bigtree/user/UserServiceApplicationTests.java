package com.bigtree.user;

import com.bigtree.user.entity.User;
import com.bigtree.user.entity.UserAccount;
import com.bigtree.user.model.UserRegistrationRequest;
import com.bigtree.user.repository.UserAccountRepository;
import com.bigtree.user.service.UserService;
import org.bson.assertions.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

	@Autowired
	UserService userService;

	@Autowired
	UserAccountRepository accountRepository;

	@Test
	void testSaveOrUpdateUser() {
		UserRegistrationRequest userRegReq = DummyData.createDummyUserRegReq();
		userService.registerUser(userRegReq);
		User saved = userService.findByEmailAndUserType(userRegReq.getEmail(),userRegReq.getUserType());
		Assertions.notNull("User not saved", saved);
		Assertions.notNull("User id is empty", saved.get_id());

		UserAccount account = accountRepository.getByUserId(saved.get_id());
		Assertions.notNull("User account not created ", account);
		Assertions.isTrue("User accountId not created ", account.getUserId().equals(saved.get_id()));
		Assertions.isTrue("User password not stored correctly ", account.getPassword().equals(userRegReq.getPassword()));

		saved.setEmail("updated@email.com");
		saved.setMobile("1111111111");
		saved.setFirstName("UpdatedFirstName");
		saved.setLastName("UpdatedLastName");
		User updated = userService.updateUser(saved.get_id(), saved);

		Assertions.notNull("Updated UserId is empty", updated.get_id());
		Assertions.isTrue("Updated UserId is changed", updated.get_id().equals(saved.get_id()));
		Assertions.notNull("Updated User Email is empty", updated.getEmail());
		Assertions.notNull("Updated User Mobile is empty", updated.getMobile());
		Assertions.notNull("Updated User FirstName is empty", updated.getFirstName());
		Assertions.notNull("Updated User LastName is empty", updated.getLastName());

		User getById= userService.getUser(updated.get_id());
		Assertions.isTrue("User not found", getById != null);
		userService.deleteUser(updated.get_id());
	}



}
