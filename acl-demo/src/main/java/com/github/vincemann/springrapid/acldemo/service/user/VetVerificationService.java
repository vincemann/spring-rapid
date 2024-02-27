package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.VerificationServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("vet")
public class VetVerificationService extends VerificationServiceImpl {

    @Qualifier("vet")
    @Override
    public void setUserService(UserService userService) {
        super.setUserService(userService);
    }
}
