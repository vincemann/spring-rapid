package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.Owner;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.VerificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Owner
@Service
public class OwnerVerificationService extends VerificationServiceImpl {

    @Owner
    @Root
    @Autowired
    @Override
    public void setUserService(UserService userService) {
        super.setUserService(userService);
    }
}
