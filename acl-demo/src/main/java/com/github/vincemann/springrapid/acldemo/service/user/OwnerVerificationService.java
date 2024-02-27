package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.VerificationServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("owner")
public class OwnerVerificationService extends VerificationServiceImpl {

    @Qualifier("owner")
    @Override
    public void setUserService(UserService userService) {
        super.setUserService(userService);
    }
}
