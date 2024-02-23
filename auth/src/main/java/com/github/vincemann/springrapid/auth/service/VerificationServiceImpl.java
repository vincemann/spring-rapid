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
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
public class VerificationServiceImpl implements VerificationService {

    public static final String VERIFY_CONTACT_INFORMATION_AUDIENCE = "verify";

    private JweTokenService jweTokenService;

    private AuthProperties properties;

    private MessageSender messageSender;

    private UserService<AbstractUser<Serializable>,Serializable> userService;

    private IdConverter idConverter;

    @Transactional
    @Override
    public AbstractUser makeUnverified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        VerifyEntity.isTrue(!user.getRoles().contains(AuthRoles.UNVERIFIED),"Already unverified");
        AbstractUser updated = userService.addRole(user.getId(), AuthRoles.UNVERIFIED);
        TransactionalUtils.afterCommit(() -> sendVerificationMessage(updated));
        return updated;
    }


    @Transactional
    @Override
    public AbstractUser makeVerified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        VerifyEntity.isTrue(!user.getRoles().contains(AuthRoles.UNVERIFIED),"Already unverified");
        return userService.removeRole(user.getId(),AuthRoles.UNVERIFIED);
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

    @Transactional
    @Override
    public AbstractUser resendVerificationMessage(String contactInformation) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.notEmpty(contactInformation,"contact-information");
        AbstractUser user = userService.findPresentByContactInformation(contactInformation);
        // must be unverified
        VerifyEntity.isTrue(user.getRoles().contains(AuthRoles.UNVERIFIED), " Already verified");

        TransactionalUtils.afterCommit(() -> sendVerificationMessage(user));
        return user;
    }




    @Transactional
    @Override
    public AbstractUser verifyUser(String code) throws EntityNotFoundException, BadTokenException, BadEntityException {
        JWTClaimsSet claims = jweTokenService.parseToken(code);
        AbstractUser user = extractUserFromClaims(claims);
        RapidJwt.validate(claims, VERIFY_CONTACT_INFORMATION_AUDIENCE, user.getCredentialsUpdatedMillis());


        // ensure that user is unverified
        // this makes sense to do here not in security plugin
        VerifyEntity.isTrue(user.hasRole(AuthRoles.UNVERIFIED), "Already Verified");
        //verificationCode is jwtToken

        //no login needed bc token of user is appended in controller -> we avoid dynamic logins in a stateless env
        //also to be able to use read-only security test -> generic principal type does not need to be passed into this class
        return makeVerified(user);
    }

    protected AbstractUser extractUserFromClaims(JWTClaimsSet claims) throws EntityNotFoundException {
        Serializable id = idConverter.toId(claims.getSubject());
        Assert.notNull(id);
        // fetch the user
        return userService.findPresentById(id);
    }

    @Autowired public void setJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }

    @Autowired public void setProperties(AuthProperties properties) {
        this.properties = properties;
    }

    @Autowired public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }
}
