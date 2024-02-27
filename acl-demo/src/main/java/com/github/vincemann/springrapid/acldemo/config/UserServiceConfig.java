package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.auth.service.VerificationService;
import com.github.vincemann.springrapid.auth.service.VerificationServiceImpl;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {

    public VerificationService ownerVerificationService(OwnerService ownerService, JweTokenService jweTokenService){
        VerificationServiceImpl verificationService = new VerificationServiceImpl();
        verificationService.setUserService(ownerService);
        verificationService.setJweTokenService(jweTokenService);
        verificationService.setProperties(jweTokenService);
    }
}
