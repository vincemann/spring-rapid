package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.MessageSender;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
public class PasswordServiceImpl implements PasswordService {

    public static final String FORGOT_PASSWORD_AUDIENCE = "forgot-password";


    private UserService<AbstractUser<Serializable>,Serializable> userService;
    private AuthProperties properties;
    private JweTokenService jweTokenService;
    private MessageSender messageSender;
    private IdConverter idConverter;
    private PasswordEncoder passwordEncoder;




    @Transactional(readOnly = true)
    @Override
    public void forgotPassword(String contactInformation) throws EntityNotFoundException {
        // fetch the user record from database
        Optional<AbstractUser<Serializable>> byContactInformation = userService.findByContactInformation(contactInformation);
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
        userService.updatePassword(user.getId(),dto.getNewPassword());
        // dont return user instance or email - just create token for authenticated
        // if user is allowed to reset password of someone else, it does not make sense to give him the token of the updated user
        // which is not even his own -> just always return own token in header
    }



    @Transactional
    @Override
    public void changePassword(ChangePasswordDto dto) throws EntityNotFoundException, BadEntityException {
        AbstractUser<Serializable> user = VerifyEntity.isPresent(userService.findByContactInformation(dto.getContactInformation()),dto.getContactInformation(),userService.getEntityClass());
        VerifyEntity.isPresent(user, "User not found");
        String oldPassword = user.getPassword();


        // checks
        VerifyEntity.is(
                passwordEncoder.matches(dto.getOldPassword(),
                        oldPassword), "Wrong password");

        // sets the password
        userService.updatePassword(user.getId(),dto.getNewPassword());
        log.debug("changed pw of user: " + user.getContactInformation());
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


    protected AbstractUser extractUserFromClaims(JWTClaimsSet claims) throws EntityNotFoundException {
        Serializable id = idConverter.toId(claims.getSubject());
        // fetch the user
        Optional<AbstractUser<Serializable>> byId = userService.findById(id);
        VerifyEntity.isPresent(byId, "User with id: " + id + " not found");
        return byId.get();
    }


    @Autowired
    public void setUserService(UserService<AbstractUser<Serializable>, Serializable> userService) {
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
}
