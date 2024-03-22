package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.msg.AuthMessage;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.msg.MessageSender;
import com.github.vincemann.springrapid.auth.msg.mail.SmtpMailSender;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.service.val.ContactInformationValidator;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.JwtUtils;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.*;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;

import static com.github.vincemann.springrapid.auth.util.UserUtils.extractUserFromClaims;
import static com.github.vincemann.springrapid.auth.util.UserUtils.findPresentByContactInformation;
import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;


public class ContactInformationServiceImpl implements ContactInformationService {

    private final Log log = LogFactory.getLog(ContactInformationServiceImpl.class);

    public static final String CHANGE_CONTACT_INFORMATION_AUDIENCE = "change-contactInformation";

    private UserService userService;

    private JweTokenService jweTokenService;

    private AuthProperties properties;

    private MessageSender messageSender;

    private VerificationService verificationService;

    private ContactInformationValidator contactInformationValidator;

    private IdConverter idConverter;

    private AbstractUserRepository userRepository;

    @Transactional
    @Override
    public AbstractUser changeContactInformation(String code) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException, BadTokenException {

        VerifyEntity.notEmpty(code,"code");

        JWTClaimsSet claims = jweTokenService.parseToken(code);
        AbstractUser user = extractUserFromClaims(idConverter,userRepository,claims);

        JwtUtils.validate(claims, CHANGE_CONTACT_INFORMATION_AUDIENCE, user.getCredentialsUpdatedMillis());

        VerifyEntity.notEmpty(user.getNewContactInformation(), "new contact-information");

        VerifyEntity.isTrue(
                claims.getClaim("newContactInformation").equals(user.getNewContactInformation()),
                Message.get("com.github.vincemann.wrong.changeContactInformationCode"));

        contactInformationValidator.validate(user.getNewContactInformation());

        checkUniqueContactInformation(user.getNewContactInformation());

        // update the fields
        userService.updateContactInformation(user.getId(), user.getNewContactInformation());
        user.setNewContactInformation(null);

        // make the user verified if he is not
        if (user.hasRole(AuthRoles.UNVERIFIED))
            verificationService.makeVerified(user);


        return user;
    }


    @Transactional
    @Override
    public AbstractUser requestContactInformationChange(RequestContactInformationChangeDto dto) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException {
        String newContactInformation = dto.getNewContactInformation();
        VerifyEntity.notEmpty(dto.getOldContactInformation(),"old contact-information");
        VerifyEntity.notEmpty(dto.getNewContactInformation(),"new contact-information");

        contactInformationValidator.validate(newContactInformation);

        AbstractUser user = findPresentByContactInformation(userRepository,dto.getOldContactInformation());


        checkUniqueContactInformation(newContactInformation);

//        // preserves the new contactInformation id
        user.setNewContactInformation(newContactInformation);

        // needs to be done bc validation exceptions are thrown after transaction ends, otherwise validation fails but
        // message is still sent
        TransactionalUtils.afterCommit(() -> sendChangeContactInformationMessage(user));
        return user;
    }

    protected void checkUniqueContactInformation(String contactInformation) throws AlreadyRegisteredException {
        if (userRepository.findByContactInformation(contactInformation).isPresent())
            throw new AlreadyRegisteredException("contact information already present");
    }

    /**
     * Mails the change-contactInformation verification link to the user.
     */
    protected void sendChangeContactInformationMessage(AbstractUser user) {
        JWTClaimsSet claims = JwtUtils.create(
                CHANGE_CONTACT_INFORMATION_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis(),
                MapUtils.mapOf("newContactInformation", user.getNewContactInformation()));
        String changeContactInformationCode = jweTokenService.createToken(claims);


        log.debug("Mailing change contactInformation link to user: " + user);
        String changeContactInformationLink = UriComponentsBuilder
                .fromHttpUrl(
                        properties.getApplicationUrl()
                                + properties.getController().getChangeContactInformationUrl())
//                    .queryParam("id", user.getId())
                .queryParam("code", changeContactInformationCode)
                .toUriString();
        log.info("change contactInformation link: " + changeContactInformationLink);
        AuthMessage message = AuthMessage.Builder.builder()
                .link(changeContactInformationLink)
                .topic(CHANGE_CONTACT_INFORMATION_AUDIENCE)
                .code(changeContactInformationCode)
                .recipient(user.getContactInformation())
                .build();
        messageSender.send(message);

        log.debug("Change contactInformation link mail queued.");
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
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
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setContactInformationValidator(ContactInformationValidator contactInformationValidator) {
        this.contactInformationValidator = contactInformationValidator;
    }

    @Autowired
    public void setIdConverter(IdConverter idConverter) {
        this.idConverter = idConverter;
    }
}
