package com.github.vincemann.springrapid.authdemo.suite;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.authdemo.dto.ReadUserDto;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authtests.AuthTestAdapter;
import org.springframework.beans.factory.annotation.Autowired;


public class MyAuthTestAdapter extends AuthTestAdapter {

    UserControllerTestTemplate userController;


    @Override
    public AbstractUser<Long> createTestUser(String contactInformation, String password, String... roles) {
        return new User(contactInformation,password,contactInformation.split("@")[0],roles);
    }

    @Override
    public AbstractUser<?> signupUser() throws Exception {
        SignupDto dto = SignupDto.builder()
                .name("user")
                .contactInformation(USER_CONTACT_INFORMATION)
                .password(USER_PASSWORD)
                .build();

        userController.perform2xxAndDeserialize(userController.signup(dto), ReadUserDto.class);
        return fetchUser(dto.getContactInformation());
    }


    @Autowired
    public void setUserController(UserControllerTestTemplate userController) {
        this.userController = userController;
    }
}
