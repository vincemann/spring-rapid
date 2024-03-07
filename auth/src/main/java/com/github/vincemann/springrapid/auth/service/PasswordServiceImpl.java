package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.service.val.PasswordValidator;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.Optional;

public class PasswordServiceImpl implements PasswordService {

    private final Log log = LogFactory.getLog(PasswordServiceImpl.class);

    public static final String FORGOT_PASSWORD_AUDIENCE = "forgot-password";


    private UserService<AbstractUser<Serializable>,Serializable> userService;
    private AuthProperties properties;
    private JweTokenService jweTokenService;
    private MessageSender messageSender;
    private IdConverter idConverter;
    private PasswordEncoder passwordEncoder;
    private PasswordValidator passwordValidator;




    @Transactional(readOnly = true)
    @Override
    public AbstractUser forgotPassword(String contactInformation) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.notEmpty(contactInformation,"need non emtpy contact information");
        // fetch the user record from database
        Optional<AbstractUser<Serializable>> user = userService.findByContactInformation(contactInformation);
        VerifyEntity.isPresent(user, "User with contactInformation: " + contactInformation + " not found");
        TransactionalUtils.afterCommit(() -> sendForgotPasswordMessage(user.get()));
        return user.get();
    }

    @Transactional
    @Override
    public AbstractUser resetPassword(ResetPasswordDto dto) throws EntityNotFoundException, BadEntityException, BadTokenException {
        VerifyEntity.notEmpty(dto.getNewPassword(),"newPassword");
        VerifyEntity.notEmpty(dto.getCode(),"code");

        JWTClaimsSet claims = jweTokenService.parseToken(dto.getCode());
        JwtUtils.validate(claims, FORGOT_PASSWORD_AUDIENCE);

        AbstractUser user = extractUserFromClaims(claims);
        JwtUtils.validateIssuedAfter(claims, user.getCredentialsUpdatedMillis());

        passwordValidator.validate(dto.getNewPassword()); // fail fast
        return userService.updatePassword(user.getId(),dto.getNewPassword());
    }



    @Transactional
    @Override
    public AbstractUser changePassword(ChangePasswordDto dto) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.notEmpty(dto.getNewPassword(),"new password");
        VerifyEntity.notEmpty(dto.getOldPassword(),"old password");
        VerifyEntity.notEmpty(dto.getContactInformation(),"contact-information");

        AbstractUser<Serializable> user = userService.findPresentByContactInformation(dto.getContactInformation());
        String oldPassword = user.getPassword();

        VerifyEntity.isTrue(
                passwordEncoder.matches(dto.getOldPassword(),
                        oldPassword), "Wrong password");

        passwordValidator.validate(dto.getNewPassword()); // fail fast
        return userService.updatePassword(user.getId(),dto.getNewPassword());
    }

    /**
     * Sends the forgot password link.
     *
     * @param user
     */
    protected void sendForgotPasswordMessage(AbstractUser user) {
        Assert.notNull(user.getId(),"user id must not be null");
        Assert.notNull(user.getContactInformation(), "users contact information must not be null");

        log.debug("Sending forgot password link to user: " + user);
        JWTClaimsSet claims = JwtUtils.create(FORGOT_PASSWORD_AUDIENCE,
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
        AuthMessage message = AuthMessage.Builder.builder()
                .link(forgotPasswordLink)
                .topic(FORGOT_PASSWORD_AUDIENCE)
                .code(forgotPasswordCode)
                .recipient(user.getContactInformation())
                .build();

        messageSender.send(message);


        log.debug("Forgot password link mail queued.");
    }


    protected AbstractUser extractUserFromClaims(JWTClaimsSet claims) throws EntityNotFoundException {
        Serializable id = idConverter.toId(claims.getSubject());
        Assert.notNull(id);
        // fetch the user
        return userService.findPresentById(id);
    }


    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProperties(AuthProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

}
