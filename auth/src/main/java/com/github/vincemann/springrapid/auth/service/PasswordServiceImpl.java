package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.MessageSender;
import com.github.vincemann.springrapid.auth.controller.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
public class PasswordServiceImpl implements PasswordService {

    public static final String FORGOT_PASSWORD_AUDIENCE = "forgot-password";


    private UserService userService;
    private AuthProperties properties;
    private JweTokenService jweTokenService;

    private MessageSender messageSender;

    private IdConverter idConverter;

    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public void forgotPassword(String contactInformation) throws EntityNotFoundException {
        // fetch the user record from database
        Optional<AbstractUser> byContactInformation = userService.findByContactInformation(contactInformation);
        VerifyEntity.isPresent(byContactInformation, "User with contactInformation: " + contactInformation + " not found");
        AbstractUser user = byContactInformation.get();
        TransactionalUtils.afterCommit(() -> sendForgotPasswordMessage(user));
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordDto dto) throws EntityNotFoundException, BadEntityException, BadTokenException {

        JWTClaimsSet claims = jweTokenService.parseToken(dto.getCode());
        RapidJwt.validate(claims, FORGOT_PASSWORD_AUDIENCE);

        AbstractUser user = extractUserFromClaims(claims);
        RapidJwt.validateIssuedAfter(claims, user.getCredentialsUpdatedMillis());

        // sets the password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        userService.softUpdate(user);
        // dont return user instance or email - just create token for authenticated
        // if user is allowed to reset password of someone else, it does not make sense to give him the token of the updated user
        // which is not even his own -> just always return own token in header
    }

    protected AbstractUser extractUserFromClaims(JWTClaimsSet claims) throws EntityNotFoundException {
        Serializable id = idConverter.toId(claims.getSubject());
        // fetch the user
        Optional<AbstractUser> byId = userService.findById(id);
        VerifyEntity.isPresent(byId, "User with id: " + id + " not found");
        return byId.get();
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) throws EntityNotFoundException, BadEntityException {

    }

    /**
     * Sends the forgot password link.
     *
     * @param user
     */
    protected void sendForgotPasswordMessage(AbstractUser user) {

        log.debug("Sending forgot password link to user: " + user);
        JWTClaimsSet claims = RapidJwt.create(FORGOT_PASSWORD_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis());
        String forgotPasswordCode = jweTokenService.createToken(claims);

        // make the link
        String forgotPasswordLink = UriComponentsBuilder
                .fromHttpUrl(
                        properties.getCoreProperties().getApplicationUrl()
                                + properties.getController().getResetPasswordViewUrl())
                .queryParam("code", forgotPasswordCode)
                .toUriString();
        log.info("forgotPasswordLink: " + forgotPasswordLink);

        messageSender.sendMessage(forgotPasswordLink, FORGOT_PASSWORD_AUDIENCE, forgotPasswordCode, user.getContactInformation());


        log.debug("Forgot password link mail queued.");
    }



}
