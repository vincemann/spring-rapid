package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.MessageSender;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class VerificationServiceImpl implements VerificationService {

    public static final String VERIFY_CONTACT_INFORMATION_AUDIENCE = "verify";

    private JweTokenService jweTokenService;

    private AuthProperties properties;

    private MessageSender messageSender;

    private UserService userService;

    private VerificationService verificationService;

    @Transactional
    @Override
    public AbstractUser makeUnverified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        if (user.getRoles().contains(AuthRoles.UNVERIFIED)){
            throw new BadEntityException("Already unverified");
        }
        AbstractUser updated = userService.addRole(user, AuthRoles.UNVERIFIED);
        TransactionalUtils.afterCommit(() -> verificationService.sendVerificationMessage(updated));
        return updated;
    }


    @Override
    public AbstractUser makeVerified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        if (!user.getRoles().contains(AuthRoles.UNVERIFIED)){
            throw new BadEntityException("Already verified");
        }

        return userService.removeRole(user,AuthRoles.UNVERIFIED);
    }

    protected void sendVerificationMessage(AbstractUser user) {
        log.debug("Sending verification mail to: " + user);
        JWTClaimsSet claims = RapidJwt.create(VERIFY_CONTACT_INFORMATION_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis(),
                //payload
                MapUtils.mapOf("contactInformation", user.getContactInformation()));
        String verificationCode = jweTokenService.createToken(claims);


        String verifyLink = UriComponentsBuilder
                .fromHttpUrl(
                        properties.getCoreProperties().getApplicationUrl()
                                + properties.getController().getVerifyUserUrl())
                .queryParam("code", verificationCode)
                .toUriString();
        log.info("verify link: " + verifyLink);
        messageSender.sendMessage(verifyLink,VERIFY_CONTACT_INFORMATION_AUDIENCE,verificationCode, user.getContactInformation());


        log.debug("Verification mail to " + user.getContactInformation() + " queued.");
    }

    @Override
    public void resendVerificationMessage(AbstractUser user) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.isPresent(user, "User not found");
        // must be unverified
        VerifyEntity.is(user.getRoles().contains(AuthRoles.UNVERIFIED), " Already verified");

        TransactionalUtils.afterCommit(() -> sendVerificationMessage(user));
    }




    @Transactional
    @Override
    public AbstractUser verifyUser(String code) throws EntityNotFoundException, BadTokenException, BadEntityException {
        JWTClaimsSet claims = jweTokenService.parseToken(code);
        AbstractUser user = userService.extractUserFromClaims(claims);
        RapidJwt.validate(claims, VERIFY_CONTACT_INFORMATION_AUDIENCE, user.getCredentialsUpdatedMillis());


        // ensure that user is unverified
        // this makes sense to do here not in security plugin
        VerifyEntity.is(user.hasRole(AuthRoles.UNVERIFIED), "Already Verified");
        //verificationCode is jwtToken

        //no login needed bc token of user is appended in controller -> we avoid dynamic logins in a stateless env
        //also to be able to use read-only security test -> generic principal type does not need to be passed into this class
        AbstractUser updated = verificationService.makeVerified(user);
        log.debug("Verified user: " + updated.getContactInformation());
        return updated;
    }
}
