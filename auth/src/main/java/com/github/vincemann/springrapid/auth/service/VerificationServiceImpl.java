package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.*;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.auth.jwt.JweTokenService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.auth.util.Message;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import static com.github.vincemann.springrapid.auth.util.UserUtils.extractUserFromClaims;
import static com.github.vincemann.springrapid.auth.util.UserUtils.findPresentByContactInformation;

public class VerificationServiceImpl implements VerificationService {

    private final Log log = LogFactory.getLog(VerificationServiceImpl.class);

    public static final String VERIFY_CONTACT_INFORMATION_AUDIENCE = "verify";

    private JweTokenService jweTokenService;

    private AuthProperties properties;

    private MessageSender messageSender;

    private UserService userService;

    private AbstractUserRepository userRepository;

    private IdConverter idConverter;

    @Transactional
    @Override
    public AbstractUser makeUnverified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        VerifyEntity.isTrue(!user.getRoles().contains(Roles.UNVERIFIED),"Already unverified");
        AbstractUser updated = userService.addRole(user.getId(), Roles.UNVERIFIED);
        TransactionalUtils.afterCommit(() -> sendVerificationMessage(updated));
        return updated;
    }


    @Transactional
    @Override
    public AbstractUser makeVerified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        VerifyEntity.isTrue(user.getRoles().contains(Roles.UNVERIFIED),"Already verified");
        return userService.removeRole(user.getId(),Roles.UNVERIFIED);
    }

    protected void sendVerificationMessage(AbstractUser user) {
        log.debug("Sending verification mail to: " + user);
        JWTClaimsSet claims = JwtUtils.create(VERIFY_CONTACT_INFORMATION_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis(),
                //payload
                MapUtils.mapOf("contactInformation", user.getContactInformation()));
        String verificationCode = jweTokenService.createToken(claims);


        String verifyLink = UriComponentsBuilder
                .fromHttpUrl(
                        properties.getApplicationUrl()
                                + properties.getController().getVerifyUserUrl())
                .queryParam("code", verificationCode)
                .toUriString();
        log.info("verify link: " + verifyLink);

        String body = Message.get("com.github.vincemann.verifyContactInformationMessage", verifyLink);

        AuthMessage message = AuthMessage.Builder.builder()
                .link(verifyLink)
                .topic(VERIFY_CONTACT_INFORMATION_AUDIENCE)
                .code(verificationCode)
                .recipient(user.getContactInformation())
                .body(body)
                .build();
        messageSender.send(message);


        log.debug("Verification mail to " + user.getContactInformation() + " queued.");
    }

    @Transactional
    @Override
    public AbstractUser resendVerificationMessage(String contactInformation) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.notEmpty(contactInformation,"contact-information");
        AbstractUser user = findPresentByContactInformation(userRepository,contactInformation);
        // must be unverified
        VerifyEntity.isTrue(user.getRoles().contains(Roles.UNVERIFIED), " Already verified");

        TransactionalUtils.afterCommit(() -> sendVerificationMessage(user));
        return user;
    }




    @Transactional
    @Override
    public AbstractUser verifyUser(String code) throws EntityNotFoundException, BadTokenException, BadEntityException {
        JWTClaimsSet claims = jweTokenService.parseToken(code);
        AbstractUser user = extractUserFromClaims(idConverter,userRepository,claims);
        JwtUtils.validate(claims, VERIFY_CONTACT_INFORMATION_AUDIENCE, user.getCredentialsUpdatedMillis());


        // ensure that user is unverified
        // this makes sense to do here not in security plugin
        VerifyEntity.isTrue(user.hasRole(Roles.UNVERIFIED), "Already Verified");
        //verificationCode is jwtToken

        //no login needed bc token of user is appended in controller -> we avoid dynamic logins in a stateless env
        //also to be able to use read-only security test -> generic principal type does not need to be passed into this class
        return makeVerified(user);
    }


    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }

    @Autowired
    public void setProperties(AuthProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }
}
