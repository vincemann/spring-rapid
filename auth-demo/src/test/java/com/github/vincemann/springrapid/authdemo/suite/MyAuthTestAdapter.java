package com.github.vincemann.springrapid.authdemo.suite;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.authdemo.dto.ReadUserDto;
import com.github.vincemann.springrapid.authdemo.dto.SignupDto;
import com.github.vincemann.springrapid.authdemo.model.User;
import com.github.vincemann.springrapid.authtests.AuthTestAdapter;
import org.springframework.beans.factory.annotation.Autowired;


public class MyAuthTestAdapter extends AuthTestAdapter {

    MyUserControllerTestTemplate userController;


    @Override
    public AbstractUser<Long> createTestUser(String contactInformation, String password, String... roles) {
        return new User(contactInformation,password,contactInformation.split("@")[0],roles);
    }

    @Override
    public void signup(String contactInformation) throws Exception {
        SignupDto dto = SignupDto.builder()
                .name(contactInformation.split("@")[0])
                .contactInformation(contactInformation)
                .password(USER_PASSWORD)
                .build();

        userController.perform2xxAndDeserialize(userController.signup(dto), ReadUserDto.class);
    }

    @Autowired
    public void setUserController(MyUserControllerTestTemplate userController) {
        this.userController = userController;
    }
}
