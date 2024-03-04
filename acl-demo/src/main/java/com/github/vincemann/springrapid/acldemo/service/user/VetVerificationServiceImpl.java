package com.github.vincemann.springrapid.acldemo.service.user;

import com.github.vincemann.springrapid.acldemo.Owner;
import com.github.vincemann.springrapid.auth.Root;
import com.github.vincemann.springrapid.acldemo.Vet;
import com.github.vincemann.springrapid.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Vet
@Service
public class VetVerificationServiceImpl extends OwnerVerificationService {

    @Autowired
    @Vet
    @Root
    @Override
    public void setUserService(UserService userService) {
        super.setUserService(userService);
    }
}
