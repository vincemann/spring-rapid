package com.github.vincemann.springrapid.auth;

import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestMediumChangeDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.mail.MailSender;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.Message;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.web.util.UriComponentsBuilder;

public class EmailVerificationMedium<U extends AbstractUser> implements MessageSender<U> {

    private JweTokenService jweTokenService;
    private AuthProperties properties;
    private MailSender<MailData> mailSender;


    /**
     * Mails the forgot password link.
     *
     * @param user
     */
    public void sendForgotPasswordMessage(U user) {

        log.debug("Mailing forgot password link to user: " + user);
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


        MailData mailData = MailData.builder()
                .to(user.getEmail())
//                .topic( Message.get("com.github.vincemann.forgotPasswordSubject"))
                .topic(FORGOT_PASSWORD_AUDIENCE)
                .body(Message.get("com.github.vincemann.forgotPasswordEmail", forgotPasswordLink))
                .link(forgotPasswordLink)
                .code(forgotPasswordCode)
                .build();
        mailSender.send(mailData);


        log.debug("Forgot password link mail queued.");
    }

    @Override
    public void resendVerification(U user) throws EntityNotFoundException, BadEntityException {


    }

    @Override
    public U sendResetPasswordMessage(ResetPasswordDto dto, String code) throws EntityNotFoundException, BadEntityException {
        return null;
    }

    @Override
    public void changePassword(U user, ChangePasswordDto changePasswordForm) throws EntityNotFoundException, BadEntityException {

    }

    @Override
    public void requestMediumChange(U user, RequestMediumChangeDto emailChangeForm) throws EntityNotFoundException, AlreadyRegisteredException {

    }

    @Override
    public U changeMedium(String changeMediumCode) throws EntityNotFoundException, BadEntityException {
        return null;
    }
}
