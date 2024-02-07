package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.MessageSender;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Entity;
import com.google.common.collect.Sets;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class VerificationServiceImpl<U extends AbstractUser> implements VerificationService<U> {

    public static final String VERIFY_CONTACT_INFORMATION_AUDIENCE = "verify";

    private JweTokenService jweTokenService;

    private AuthProperties properties;

    private MessageSender messageSender;

    private UserService userService;

    @Transactional
    @Override
    public void makeUnverified(U user) throws BadEntityException, EntityNotFoundException {
        if (user.getRoles().contains(AuthRoles.UNVERIFIED)){
            throw new BadEntityException("Already unverified");
        }
        addRole(user,AuthRoles.UNVERIFIED);
        TransactionalUtils.afterCommit(() -> sendVerificationMessage(user));
    }

    protected void addRole(U user, String role) throws EntityNotFoundException, BadEntityException {
        Set<String> newRoles = new HashSet<>(user.getRoles());
        newRoles.add(role);
        U updateRole = Entity.createUpdate(user);
        updateRole.setRoles(newRoles);
        userService.partialUpdate(updateRole);
    }

    protected void removeRole(U user, String role) throws EntityNotFoundException, BadEntityException {
        Set<String> newRoles = new HashSet<>(user.getRoles());
        newRoles.add(role);
        U updateRole = Entity.createUpdate(user);
        updateRole.setRoles(newRoles);
        userService.partialUpdate(updateRole);
    }

    @Override
    public void makeVerified(U user) throws BadEntityException, EntityNotFoundException {
        if (!user.getRoles().contains(AuthRoles.UNVERIFIED)){
            throw new BadEntityException("Already verified");
        }

        removeRole(user,AuthRoles.UNVERIFIED);
        TransactionalUtils.afterCommit(() -> sendVerificationMessage(user));
    }

    @Override
    public void sendVerificationMessage(U user) {
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
    public void resendVerificationMessage() {

    }

    @Override
    public U verifyUser(String code) {
        return null;
    }
}
