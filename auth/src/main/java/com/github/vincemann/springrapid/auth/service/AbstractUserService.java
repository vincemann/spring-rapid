package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.*;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestEmailChangeDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.mail.MailData;
import com.github.vincemann.springrapid.auth.mail.MailSender;
import com.github.vincemann.springrapid.auth.security.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.service.validation.PasswordValidator;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.password.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.google.common.collect.Sets;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Validated
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Slf4j
public abstract class AbstractUserService
        <
                U extends AbstractUser<ID>,
                ID extends Serializable,
                R extends AbstractUserRepository<U, ID>
                >
        extends JPACrudService<U, ID, R>
        implements UserService<U, ID> {

    public static final String CHANGE_EMAIL_AUDIENCE = "change-email";
    public static final String VERIFY_EMAIL_AUDIENCE = "verify";
    public static final String FORGOT_PASSWORD_AUDIENCE = "forgot-password";

    private AuthorizationTokenService<RapidAuthAuthenticatedPrincipal> authorizationTokenService;
    private RapidSecurityContext<RapidAuthAuthenticatedPrincipal> securityContext;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private RapidPasswordEncoder passwordEncoder;
    private AuthProperties properties;
    private MailSender<MailData> mailSender;
    private JweTokenService jweTokenService;
    private IdConverter<ID> idIdConverter;
    private PasswordValidator passwordValidator;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new user object. Must be overridden in the
     * subclass, like this:
     * <p>
     * public User newUser() {
     * return new User();
     * }
     */
    public abstract U newUser();


    @Override
    public Map<String, Object> getContext() {

        // make the context
        Map<String, Object> context = new HashMap<String, Object>(3);
        context.put("reCaptchaSiteKey", properties.getRecaptcha().getSitekey());
        context.put("shared", properties.getShared());
        RapidAuthAuthenticatedPrincipal principal = securityContext.currentPrincipal();
        if (principal != null) {
            if (!principal.isAnon()) {
                RapidAuthAuthenticatedPrincipal withoutPw = new RapidAuthAuthenticatedPrincipal(principal);
                withoutPw.setPassword(null);
                context.put("user", withoutPw);
            }
        }

        return context;
    }

    protected void checkUniqueEmail(String email) throws AlreadyRegisteredException {
        Optional<U> byEmail = getRepository().findByEmail(email);
        if (byEmail.isPresent()) {
            throw new AlreadyRegisteredException("Email: " + email + " is already taken");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U signup(U user) throws BadEntityException, AlreadyRegisteredException {
        //admins get created with createAdminMethod
        user.setRoles(Sets.newHashSet(AuthRoles.USER));
        passwordValidator.validate(user.getPassword());
        checkUniqueEmail(user.getEmail());
        U saved = save(user);
        // is done in same transaction -> so applied directly
        makeUnverified(saved);

        log.debug("saved and send verification mail for unverified new user: " + saved);

        // NO
        //logout anon and login user, it is expected that signed up user is logged in after this method is called
//        securityContext.login(authenticatedPrincipalFactory.create(saved));

        return saved;
    }


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    @Override
    public U save(U user) throws BadEntityException {
        // no restrictions in save method, all restrictions and checks in more abstract methods such as signup and createAdmin
        encodePasswordIfNecessary(user);
        return super.save(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    @Override
    public void deleteById(ID id) throws EntityNotFoundException, BadEntityException {
        super.deleteById(id);
    }

    protected void makeUnverified(U user) {
        user.getRoles().add(AuthRoles.UNVERIFIED);
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
//        TransactionalUtils.afterCommit(() -> sendVerificationMail(user));
        sendVerificationMail(user);
    }

    /**
     * Only encrypts password, if it is not already encrypted.
     */
    protected void encodePasswordIfNecessary(U user) {
        String password = user.getPassword();
        if (password == null) {
            return;
        }
        if (!passwordEncoder.isEncrypted(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
    }

    /**
     * Resends verification mail to the user.
     */
    public void resendVerificationMail(U user) throws EntityNotFoundException, BadEntityException {

//        // The user must exist

        VerifyEntity.isPresent(user, "User not found");
        // must be unverified
        VerifyEntity.is(user.getRoles().contains(AuthRoles.UNVERIFIED), " Already verified");

        sendVerificationMail(user);
    }


    public Optional<U> findByEmail(String email) {
        return getRepository().findByEmail(email);
    }


    /**
     * Verifies the email id of current-user
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U verifyUser(String verificationCode) throws EntityNotFoundException, BadEntityException {
        try {
            JWTClaimsSet claims = jweTokenService.parseToken(verificationCode);
            U user = extractUserFromClaims(claims);
            RapidJwt.validate(claims, VERIFY_EMAIL_AUDIENCE, user.getCredentialsUpdatedMillis());


            // ensure that he is unverified
            // this makes sense to do here not in security plugin
            VerifyEntity.is(user.hasRole(AuthRoles.UNVERIFIED), "Already Verified");
            //verificationCode is jwtToken


//            VerifyAccess.condition(
//                    claims.getClaim("id").equals(user.getId().toString()),
//                    "Wrong user id in token");


            //no login needed bc token of user is appended in controller -> we avoid dynamic logins in a stateless env
            //also to be able to use read-only security test -> generic principal type does not need to be passed into this class
            return verifyUser(user);
        } catch (BadTokenException e) {
            throw new BadEntityException(Message.get("com.github.vincemann.wrong.verificationCode"), e);
        }

    }

    protected U verifyUser(U user) throws BadEntityException, EntityNotFoundException {
        user.getRoles().remove(AuthRoles.UNVERIFIED);
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        // todo changed to repo
//        U saved = update(user);
        U saved = softUpdate(user);
        log.debug("Verified user: " + user.getEmail());
        return saved;
    }


    /**
     * Forgot password.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void forgotPassword(String email) throws EntityNotFoundException {
        // fetch the user record from database
        Optional<U> byEmail = findByEmail(email);
        VerifyEntity.isPresent(byEmail, "User with email: " + email + " not found");
        U user = byEmail.get();
        sendForgotPasswordMail(user);
    }


    /**
     * Resets the password.
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U resetPassword(ResetPasswordDto dto, String code) throws EntityNotFoundException, BadEntityException {

        try {
            JWTClaimsSet claims = jweTokenService.parseToken(code);
            RapidJwt.validate(claims, FORGOT_PASSWORD_AUDIENCE);

            passwordValidator.validate(dto.getNewPassword());
            U user = extractUserFromClaims(claims);
            RapidJwt.validateIssuedAfter(claims, user.getCredentialsUpdatedMillis());


//            VerifyAccess.condition(
//                    claims.getClaim("id").equals(user.getId().toString()),
//                    "Wrong user id in token");


            // sets the password
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            user.setCredentialsUpdatedMillis(System.currentTimeMillis());
            //user.setForgotPasswordCode(null);
            try {
                // todo changed to repo
                return softUpdate(user);
//                return update(user);
            } catch (NonTransientDataAccessException e) {
                throw new RuntimeException("Could not reset users password", e);
            }
        } catch (BadTokenException e) {
            throw new BadEntityException(Message.get("com.github.vincemann.wrong.verificationCode"), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public U partialUpdate(U update, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        updateSpecialUserFields(update);
        return super.partialUpdate(update, fieldsToRemove);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public U fullUpdate(U update) throws BadEntityException, EntityNotFoundException {
        updateSpecialUserFields(update);
        return super.fullUpdate(update);
    }

    protected void updateSpecialUserFields(U update) throws BadEntityException, EntityNotFoundException {
        Optional<U> old = findById(update.getId());
        entityManager.merge(old.get());
        VerifyEntity.isPresent(old, "Entity to update with id: " + update.getId() + " not found");
        //update roles works in transaction -> changes are applied on the fly
        updateRoles(old.get(), update);
        update.setRoles(old.get().getRoles());
        String password = update.getPassword();
        if (password != null) {
            if (!getPasswordEncoder().isEncrypted(password)) {
                passwordValidator.validate(password);
            }
        }
        encodePasswordIfNecessary(update);
    }

    /**
     * Changes the password.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void changePassword(U user, ChangePasswordDto changePasswordDto) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.isPresent(user, "User not found");
        String oldPassword = user.getPassword();

        if (!changePasswordDto.getPassword().equals(changePasswordDto.getRetypePassword())) {
            throw new BadEntityException("Password does not match retype password");
        }
        passwordValidator.validate(changePasswordDto.getPassword());
        // checks
        VerifyEntity.is(
                passwordEncoder.matches(changePasswordDto.getOldPassword(),
                        oldPassword), "Wrong password");

        // sets the password
        user.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        // todo changed to repo
        log.debug("changed pw of user: " + user.getEmail());
        try {
//            update(user);
            softUpdate(user);
        } catch (NonTransientDataAccessException e) {
            throw new RuntimeException("Could not change users password", e);
        }

    }


    protected void updateRoles(U old, U newUser) {
        log.debug("Updating user fields for user: " + old);
        // update the roles

        if (old.getRoles().equals(newUser.getRoles())) // roles are same
            return;

        if (newUser.hasRole(AuthRoles.UNVERIFIED)) {

            if (!old.hasRole(AuthRoles.UNVERIFIED)) {
                makeUnverified(old); // make user unverified
            }
        } else {

            if (old.hasRole(AuthRoles.UNVERIFIED))
                old.getRoles().remove(AuthRoles.UNVERIFIED); // make user verified
        }

        old.setRoles(newUser.getRoles());
        old.setCredentialsUpdatedMillis(System.currentTimeMillis());
    }


    /**
     * Requests for email change.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void requestEmailChange(U user, RequestEmailChangeDto emailChangeDto) throws EntityNotFoundException, AlreadyRegisteredException {
        VerifyEntity.isPresent(user, "User not found");
        checkUniqueEmail(emailChangeDto.getNewEmail());

//        LexUtils.validateField("updatedUser.password",
//                passwordEncoder.matches(emailChangeDto.getPassword(),
//                        user.getPassword()),
//                "com.github.vincemann.wrong.password").go();

        // preserves the new email id
        user.setNewEmail(emailChangeDto.getNewEmail());
        //user.setChangeEmailCode(LemonValidationUtils.uid());
        U saved;
        try {
            // todo changed to repo
            saved = softUpdate(user);
            // after successful commit, mails a link to the user
//            TransactionalUtils.afterCommit(() -> mailChangeEmailLink(saved));
        } catch (NonTransientDataAccessException | BadEntityException e) {
            throw new RuntimeException("Email was malformed, although validation check was successful");
        }

        log.debug("Requested email change: " + user);
        mailChangeEmailLink(saved);
    }


    /**
     * Mails the change-email verification link to the user.
     */
    protected void mailChangeEmailLink(U user) {
        JWTClaimsSet claims = RapidJwt.create(
                CHANGE_EMAIL_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis(),
                MapUtils.mapOf("newEmail", user.getNewEmail()));
        String changeEmailCode = jweTokenService.createToken(claims);

        try {

            log.debug("Mailing change email link to user: " + user);
            String changeEmailLink = UriComponentsBuilder
                    .fromHttpUrl(
                            properties.getCoreProperties().getApplicationUrl()
                                    + properties.getController().getChangeEmailUrl())
//                    .queryParam("id", user.getId())
                    .queryParam("code", changeEmailCode)
                    .toUriString();
            log.info("change email link: " + changeEmailLink);


            // mail it
            MailData mailData = MailData.builder()
                    .to(user.getEmail())
//                    .topic( Message.get("com.github.vincemann.changeEmailSubject"))
                    .topic(CHANGE_EMAIL_AUDIENCE)
                    .body(Message.get("com.github.vincemann.changeEmailEmail", changeEmailLink))
                    .link(changeEmailLink)
                    .code(changeEmailCode)
                    .build();
            mailSender.send(mailData);

            log.debug("Change email link mail queued.");

        } catch (Throwable e) {
            // In case of exception, just log the error and keep silent, people can use resendVerification link endpoint
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * Change the email.
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U changeEmail(String code) throws EntityNotFoundException, BadEntityException {
        try {
            JWTClaimsSet claims = jweTokenService.parseToken(code);
            U user = extractUserFromClaims(claims);

            RapidJwt.validate(claims, CHANGE_EMAIL_AUDIENCE, user.getCredentialsUpdatedMillis());

//            VerifyAccess.condition(
//                    claims.getClaim("id").equals(user.getId().toString()),
//                    "Wrong user id in token");

            VerifyEntity.is(StringUtils.isNotBlank(user.getNewEmail()), "No new email found. Looks like you have already changed.");


            VerifyAccess.condition(
                    claims.getClaim("newEmail").equals(user.getNewEmail()),
                    Message.get("com.github.vincemann.wrong.changeEmailCode"));

            // Ensure that the email would be unique
            VerifyEntity.is(
                    !findByEmail(user.getNewEmail()).isPresent(), "Email Id already used");

            // update the fields
            user.setEmail(user.getNewEmail());
            user.setNewEmail(null);
            //user.setChangeEmailCode(null);
            user.setCredentialsUpdatedMillis(System.currentTimeMillis());

            // todo create method for that
            // make the user verified if he is not
            if (user.hasRole(AuthRoles.UNVERIFIED))
                user.getRoles().remove(AuthRoles.UNVERIFIED);
            // todo changed to repo
//          return update(user);
            return softUpdate(user);
        } catch (BadTokenException e) {
            throw new BadEntityException(Message.get("com.github.vincemann.wrong.verificationCode"), e);
        } catch (NonTransientDataAccessException e) {
            throw new RuntimeException("Could not update users email", e);
        }
    }

    protected U extractUserFromClaims(JWTClaimsSet claims) throws BadEntityException, EntityNotFoundException {
        ID id = idIdConverter.toId(claims.getSubject());
        // fetch the user
        Optional<U> byId = findById(id);
        VerifyEntity.isPresent(byId, "User with id: " + id + " not found");
        return byId.get();
    }


    /**
     * Fetches a new token
     *
     * @return
     */
    @Override
    public String createNewAuthToken(String email) throws EntityNotFoundException {
        Optional<U> byEmail = findByEmail(email);
        VerifyEntity.isPresent(byEmail, "user with email: " + email + " not found");
        return authorizationTokenService.createToken(authenticatedPrincipalFactory.create(byEmail.get()));
    }

    @Override
    public String createNewAuthToken() throws EntityNotFoundException {
        return createNewAuthToken(securityContext.currentPrincipal().getEmail());
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    //only called internally
    public U newAdmin(AuthProperties.Admin admin) {
        // create the adminUser
        U adminUser = newUser();
        adminUser.setEmail(admin.getEmail());
        adminUser.setPassword(admin.getPassword());
        adminUser.getRoles().add(AuthRoles.ADMIN);
        return adminUser;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U signupAdmin(U admin) throws AlreadyRegisteredException, BadEntityException {
        checkUniqueEmail(admin.getEmail());
        passwordValidator.validate(admin.getPassword());
        return save(admin);
    }

    /**
     * Sends verification mail to a unverified user.
     */
    protected void sendVerificationMail(final U user) {

        log.debug("Sending verification mail to: " + user);
        JWTClaimsSet claims = RapidJwt.create(VERIFY_EMAIL_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis(),
                //payload
                MapUtils.mapOf("email", user.getEmail()));
        String verificationCode = jweTokenService.createToken(claims);


        String verifyLink = UriComponentsBuilder
                .fromHttpUrl(
                        properties.getCoreProperties().getApplicationUrl()
                                + properties.getController().getVerifyUserUrl())
//                .queryParam("id", user.getId())
                .queryParam("code", verificationCode)
                .toUriString();
        log.info("change email link: " + verifyLink);


        // send the mail
        MailData mailData = MailData.builder()
                .to(user.getEmail())
//                .topic(Message.get("com.github.vincemann.verifySubject"))
                .topic(VERIFY_EMAIL_AUDIENCE)
                .body(Message.get("com.github.vincemann.verifyEmail", verifyLink))
                .link(verifyLink)
                .code(verificationCode)
                .build();
        mailSender.send(mailData);

        log.debug("Verification mail to " + user.getEmail() + " queued.");
    }


    /**
     * Mails the forgot password link.
     *
     * @param user
     */
    public void sendForgotPasswordMail(U user) {

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


    protected AuthorizationTokenService<RapidAuthAuthenticatedPrincipal> getAuthorizationTokenService() {
        return authorizationTokenService;
    }

    protected RapidSecurityContext<RapidAuthAuthenticatedPrincipal> getSecurityContext() {
        return securityContext;
    }

    protected AuthenticatedPrincipalFactory getAuthenticatedPrincipalFactory() {
        return authenticatedPrincipalFactory;
    }

    protected RapidPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    protected AuthProperties getProperties() {
        return properties;
    }

    protected MailSender<MailData> getMailSender() {
        return mailSender;
    }

    protected JweTokenService getJweTokenService() {
        return jweTokenService;
    }

    protected PasswordValidator getPasswordValidator() {
        return passwordValidator;
    }

    //    protected UserService<U, ID> getRootUserService() {
//        return rootUserService;
//    }

    @Autowired
    public void injectAuthorizationTokenService(AuthorizationTokenService<RapidAuthAuthenticatedPrincipal> authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<RapidAuthAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
    }

    @Autowired
    public void injectPasswordEncoder(RapidPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void injectProperties(AuthProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void injectMailSender(MailSender<MailData> mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    public void injectJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }

    @Lazy
    @Autowired
    public void injectIdIdConverter(IdConverter<ID> idIdConverter) {
        this.idIdConverter = idIdConverter;
    }

    @Autowired
    public void injectPrincipalUserConverter(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }

    @Autowired
    public void injectPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }
}
