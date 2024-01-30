package com.github.vincemann.springrapid.auth.service;


import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.MessageSender;
import com.github.vincemann.springrapid.auth.model.*;
import com.github.vincemann.springrapid.auth.sec.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.RapidJwt;
import com.github.vincemann.springrapid.auth.util.TransactionalUtils;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.pass.RapidPasswordEncoder;
import com.github.vincemann.springrapid.core.util.Message;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.*;

import static com.github.vincemann.springrapid.auth.util.PrincipalUtils.isAnon;

/**
 * Note:
 * If you extend from this class and annotate with @{@link org.springframework.stereotype.Service} or @{@link org.springframework.stereotype.Component}
 * make sure to not also add @{@link org.springframework.context.annotation.Primary}.
 *
 */
@Validated
@Slf4j
public abstract class JpaUserService
        <
                U extends AbstractUser<ID>,
                ID extends Serializable,
                R extends AbstractUserRepository<U, ID>
                >
        extends JPACrudService<U, ID, R>
            implements UserService<U, ID>, ApplicationContextAware {


    public static final String CHANGE_CONTACT_INFORMATION_AUDIENCE = "change-contactInformation";
    public static final String VERIFY_CONTACT_INFORMATION_AUDIENCE = "verify";
    public static final String FORGOT_PASSWORD_AUDIENCE = "forgot-password";

    private AuthorizationTokenService authorizationTokenService;
    private RapidSecurityContext securityContext;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;
    private RapidPasswordEncoder passwordEncoder;
    private AuthProperties properties;
//    private MailSender<MailData> mailSender;
    private JweTokenService jweTokenService;
    private IdConverter<ID> idConverter;
    private PasswordValidator passwordValidator;
    @PersistenceContext
    private EntityManager entityManager;
    private MessageSender messageSender;

    public JpaUserService<U,ID,R> getService(){
        return (JpaUserService<U, ID, R>) service;
    }

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
        Map<String, Object> context = new HashMap<String, Object>(2);
        context.put("shared", properties.getShared());
        RapidPrincipal principal = securityContext.currentPrincipal();
        if (principal != null) {
            if (!isAnon(principal)) {
                RapidPrincipal withoutPw = new RapidPrincipal(principal);
                withoutPw.setPassword(null);
                context.put("user", withoutPw);
            }
        }

        return context;
    }

    protected void checkUniqueContactInformation(String contactInformation) throws AlreadyRegisteredException {
        Optional<U> byContactInformation = findByContactInformation(contactInformation);
        if (byContactInformation.isPresent()) {
            throw new AlreadyRegisteredException("ContactInformation: " + contactInformation + " is already taken");
        }
    }

    @Transactional
    public U signup(U user) throws BadEntityException, AlreadyRegisteredException {
        //admins get created with createAdminMethod
        if (user.getRoles() == null){
            user.setRoles(new HashSet<>());
        }else user.getRoles().add(AuthRoles.USER);
        passwordValidator.validate(user.getPassword());
        checkUniqueContactInformation(user.getContactInformation());
        U saved = service.save(user);
        // is done in same transaction -> so applied directly, but message sent after transaction to make sure it
        // is not sent when transaction fails
        makeUnverified(saved);

        log.debug("saved and send verification mail for unverified new user: " + saved);

        // NO
        //logout anon and login user, it is expected that signed up user is logged in after this method is called
//        securityContext.login(authenticatedPrincipalFactory.create(saved));

        return saved;
    }


    @Transactional
    @Override
    public U save(U user) throws BadEntityException {
        // no restrictions in save method, all restrictions and checks in more abstract methods such as signup and createAdmin
        encodePasswordIfNecessary(user);
        return super.save(user);
    }

    @Transactional
    @Override
    public void deleteById(ID id) throws EntityNotFoundException {
        super.deleteById(id);
    }

    protected void makeUnverified(U user) {
        user.getRoles().add(AuthRoles.UNVERIFIED);
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
//        TransactionalUtils.afterCommit(() -> sendVerificationMail(user));
        TransactionalUtils.afterCommit(() -> sendVerificationMessage(user));
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
    public void resendVerificationMessage(U user) throws EntityNotFoundException, BadEntityException {

//        // The user must exist

        VerifyEntity.isPresent(user, "User not found");
        // must be unverified
        VerifyEntity.is(user.getRoles().contains(AuthRoles.UNVERIFIED), " Already verified");

        TransactionalUtils.afterCommit(() -> sendVerificationMessage(user));
    }


    public Optional<U> findByContactInformation(String contactInformation) {
        return getRepository().findByContactInformation(contactInformation);
    }


    /**
     * Verifies the contactInformation id of current-user
     *
     * @return
     */
    @Transactional
    public U verifyUser(String verificationCode) throws EntityNotFoundException, BadEntityException {
        try {
            JWTClaimsSet claims = jweTokenService.parseToken(verificationCode);
            U user = extractUserFromClaims(claims);
            RapidJwt.validate(claims, VERIFY_CONTACT_INFORMATION_AUDIENCE, user.getCredentialsUpdatedMillis());


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
        U saved = service.softUpdate(user);
        log.debug("Verified user: " + user.getContactInformation());
        return saved;
    }


    /**
     * Forgot password.
     */
    @Transactional
    public void forgotPassword(String contactInformation) throws EntityNotFoundException {
        // fetch the user record from database
        Optional<U> byContactInformation = findByContactInformation(contactInformation);
        VerifyEntity.isPresent(byContactInformation, "User with contactInformation: " + contactInformation + " not found");
        U user = byContactInformation.get();
        TransactionalUtils.afterCommit(() -> sendForgotPasswordMessage(user));
    }




        /**
     * Sends the forgot password link.
     *
     * @param user
     */
    public void sendForgotPasswordMessage(U user) {

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


//        MailData mailData = MailData.builder()
//                .to(user.getContactInformation())
////                .topic( Message.get("com.github.vincemann.forgotPasswordSubject"))
//                .topic(FORGOT_PASSWORD_AUDIENCE)
//                .body(Message.get("com.github.vincemann.forgotPasswordContactInformation", forgotPasswordLink))
//                .link(forgotPasswordLink)
//                .code(forgotPasswordCode)
//                .build();
//        mailSender.send(mailData);
        messageSender.sendMessage(forgotPasswordLink,FORGOT_PASSWORD_AUDIENCE,forgotPasswordCode,user.getContactInformation());


        log.debug("Forgot password link mail queued.");
    }

    /**
     * Resets the password.
     *
     * @return
     */
    @Transactional
    public U resetPassword(String newPassword, String code) throws EntityNotFoundException, BadEntityException {

        try {
            JWTClaimsSet claims = jweTokenService.parseToken(code);
            RapidJwt.validate(claims, FORGOT_PASSWORD_AUDIENCE);

            passwordValidator.validate(newPassword);
            U user = extractUserFromClaims(claims);
            RapidJwt.validateIssuedAfter(claims, user.getCredentialsUpdatedMillis());


//            VerifyAccess.condition(
//                    claims.getClaim("id").equals(user.getId().toString()),
//                    "Wrong user id in token");


            // sets the password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setCredentialsUpdatedMillis(System.currentTimeMillis());
            //user.setForgotPasswordCode(null);
            try {
                // todo changed to softupdate
                return service.softUpdate(user);
//                return update(user);
            } catch (NonTransientDataAccessException e) {
                throw new RuntimeException("Could not reset users password", e);
            }
        } catch (BadTokenException e) {
            throw new BadEntityException(Message.get("com.github.vincemann.wrong.verificationCode"), e);
        }
    }

    @Transactional
    @Override
    public U partialUpdate(U update, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        updateSpecialUserFields(update);
        return super.partialUpdate(update, fieldsToUpdate);
    }

    @Transactional
    @Override
    public U fullUpdate(U update) throws BadEntityException, EntityNotFoundException {
        updateSpecialUserFields(update);
        return super.fullUpdate(update);
    }

    @Transactional
    @Override
    public U softUpdate(U update) throws EntityNotFoundException, BadEntityException {
        updateSpecialUserFields(update);
        return super.softUpdate(update);
    }

    protected void updateSpecialUserFields(U update) throws BadEntityException, EntityNotFoundException {
        Optional<U> old = findById(update.getId());
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
    @Transactional
    public void changePassword(U user, String givenOldPassword, String newPassword, String retypeNewPassword) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.isPresent(user, "User not found");
        String oldPassword = user.getPassword();

        if (!newPassword.equals(retypeNewPassword)) {
            throw new BadEntityException("Password does not match retype password");
        }
        passwordValidator.validate(newPassword);
        // checks
        VerifyEntity.is(
                passwordEncoder.matches(givenOldPassword,
                        oldPassword), "Wrong password");

        // sets the password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        log.debug("changed pw of user: " + user.getContactInformation());
        try {
            service.softUpdate(user);
        } catch (NonTransientDataAccessException e) {
            throw new RuntimeException("Could not change users password", e);
        }

    }


    protected void updateRoles(U old, U newUser) {
        log.debug("Updating user fields for user: " + old);

        // no role updates has been made, keep old roles
        if (newUser.getRoles() == null) {
            return;
        }

        // after this if statement passed it is obvious that roles have changed / will change
        // just a matter of how
        if (old.getRoles().equals(newUser.getRoles())) // roles are same
            return;


        if (newUser.hasRole(AuthRoles.UNVERIFIED)) {

            if (!old.hasRole(AuthRoles.UNVERIFIED)) {
                makeUnverified(old); // make user unverified
            }
        } else {

            if (old.hasRole(AuthRoles.UNVERIFIED)) {
                old.getRoles().remove(AuthRoles.UNVERIFIED); // make user verified
            }
        }


        old.setRoles(newUser.getRoles());
        old.setCredentialsUpdatedMillis(System.currentTimeMillis());

    }


    /**
     * Requests for contactInformation change.
     *
     */
    @Transactional
    public void requestContactInformationChange(U user, String newContactInformation) throws EntityNotFoundException, AlreadyRegisteredException, BadEntityException {
        VerifyEntity.isPresent(user, "User not found");
        checkUniqueContactInformation(newContactInformation);
        // todo dont detach and see if setter triggers javax validation annotations
        entityManager.detach(user);

//        LexUtils.validateField("updatedUser.password",
//                passwordEncoder.matches(newContactInformation.getPassword(),
//                        user.getPassword()),
//                "com.github.vincemann.wrong.password").go();

//        // preserves the new contactInformation id
        user.setNewContactInformation(newContactInformation);
//        //user.setChangeContactInformationCode(LemonValidationUtils.uid());
        U saved;
        try {
            // todo changed to softupdate
            saved = service.softUpdate(user);
            // after successful commit, mails a link to the user
//            TransactionalUtils.afterCommit(() -> mailChangeContactInformationLink(saved));
        } catch (NonTransientDataAccessException | BadEntityException e) {
            throw new RuntimeException("ContactInformation was malformed, although validation check was successful");
        }

        log.debug("Requested contactInformation change: " + user);
        // needs to be done bc validation exceptions are thrown after transaction ends, otherwise validation fails but
        // message is still sent
        TransactionalUtils.afterCommit( () -> sendChangeContactInformationMessage(saved));
    }



    /**
     * Mails the change-contactInformation verification link to the user.
     */
    protected void sendChangeContactInformationMessage(U user) {
        JWTClaimsSet claims = RapidJwt.create(
                CHANGE_CONTACT_INFORMATION_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis(),
                MapUtils.mapOf("newContactInformation", user.getNewContactInformation()));
        String changeContactInformationCode = jweTokenService.createToken(claims);

        try {

            log.debug("Mailing change contactInformation link to user: " + user);
            String changeContactInformationLink = UriComponentsBuilder
                    .fromHttpUrl(
                            properties.getCoreProperties().getApplicationUrl()
                                    + properties.getController().getChangeContactInformationUrl())
//                    .queryParam("id", user.getId())
                    .queryParam("code", changeContactInformationCode)
                    .toUriString();
            log.info("change contactInformation link: " + changeContactInformationLink);


            // mail it
//            MailData mailData = MailData.builder()
//                    .to(user.getContactInformation())
////                    .topic( Message.get("com.github.vincemann.changeContactInformationSubject"))
//                    .topic(CHANGE_CONTACT_INFORMATION_AUDIENCE)
//                    .body(Message.get("com.github.vincemann.changeContactInformationContactInformation", changeContactInformationLink))
//                    .link(changeContactInformationLink)
//                    .code(changeContactInformationCode)
//                    .build();
//            mailSender.send(mailData);
            messageSender.sendMessage(changeContactInformationLink,CHANGE_CONTACT_INFORMATION_AUDIENCE,changeContactInformationCode,user.getContactInformation());

            log.debug("Change contactInformation link mail queued.");

        } catch (Throwable e) {
            // In case of exception, just log the error and keep silent, people can use resendVerification link endpoint
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * Change the contactInformation.
     *
     * @return
     */
    @Transactional
    public U changeContactInformation(String code) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException {
        try {
            JWTClaimsSet claims = jweTokenService.parseToken(code);
            U user = extractUserFromClaims(claims);
            entityManager.detach(user);

            RapidJwt.validate(claims, CHANGE_CONTACT_INFORMATION_AUDIENCE, user.getCredentialsUpdatedMillis());

//            VerifyAccess.condition(
//                    claims.getClaim("id").equals(user.getId().toString()),
//                    "Wrong user id in token");

            VerifyEntity.is(StringUtils.isNotBlank(user.getNewContactInformation()), "No new contactInformation found. Looks like you have already changed.");


            VerifyAccess.condition(
                    claims.getClaim("newContactInformation").equals(user.getNewContactInformation()),
                    Message.get("com.github.vincemann.wrong.changeContactInformationCode"));

            // Ensure that the contactInformation would be unique
            checkUniqueContactInformation(user.getNewContactInformation());
//            VerifyEntity.is(
//                    !findByContactInformation(user.getNewContactInformation()).isPresent(), "ContactInformation Id already used");

            // update the fields
            user.setContactInformation(user.getNewContactInformation());
            user.setNewContactInformation(null);
            //user.setChangeContactInformationCode(null);
            user.setCredentialsUpdatedMillis(System.currentTimeMillis());

            // todo create method for that
            // make the user verified if he is not
            if (user.hasRole(AuthRoles.UNVERIFIED))
                user.getRoles().remove(AuthRoles.UNVERIFIED);
            // todo changed to repo
//          return update(user);
            return service.softUpdate(user);
        } catch (BadTokenException e) {
            throw new BadEntityException(Message.get("com.github.vincemann.wrong.verificationCode"), e);
        } catch (NonTransientDataAccessException e) {
            throw new RuntimeException("Could not update users contactInformation", e);
        }
    }

    protected U extractUserFromClaims(JWTClaimsSet claims) throws BadEntityException, EntityNotFoundException {
        ID id = idConverter.toId(claims.getSubject());
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
    public String createNewAuthToken(String contactInformation) throws EntityNotFoundException {
        Optional<U> byContactInformation = findByContactInformation(contactInformation);
        VerifyEntity.isPresent(byContactInformation, "user with contactInformation: " + contactInformation + " not found");
        return authorizationTokenService.createToken(authenticatedPrincipalFactory.create(byContactInformation.get()));
    }

    @Override
    public String createNewAuthToken() throws EntityNotFoundException {
        return createNewAuthToken(securityContext.currentPrincipal().getName());
    }

    @Transactional
    //only called internally
    public U newAdmin(AuthProperties.Admin admin) {
        // create the adminUser
        U adminUser = newUser();
        adminUser.setContactInformation(admin.getContactInformation());
        adminUser.setPassword(admin.getPassword());
        adminUser.getRoles().add(AuthRoles.ADMIN);
        return adminUser;
    }

    @Transactional
    public U signupAdmin(U admin) throws AlreadyRegisteredException, BadEntityException {
        checkUniqueContactInformation(admin.getContactInformation());
        passwordValidator.validate(admin.getPassword());
        return service.save(admin);
    }

    /**
     * Sends verification mail to a unverified user.
     */
    protected void sendVerificationMessage(final U user) {

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
//                .queryParam("id", user.getId())
                .queryParam("code", verificationCode)
                .toUriString();
        log.info("verify link: " + verifyLink);


//        // send the mail
//        MailData mailData = MailData.builder()
//                .to(user.getContactInformation())
////                .topic(Message.get("com.github.vincemann.verifySubject"))
//                .topic(VERIFY_CONTACT_INFORMATION_AUDIENCE)
//                .body(Message.get("com.github.vincemann.verifyContactInformation", verifyLink))
//                .link(verifyLink)
//                .code(verificationCode)
//                .build();
//        mailSender.send(mailData);
        messageSender.sendMessage(verifyLink,VERIFY_CONTACT_INFORMATION_AUDIENCE,verificationCode, user.getContactInformation());


        log.debug("Verification mail to " + user.getContactInformation() + " queued.");
    }





    protected AuthorizationTokenService getAuthorizationTokenService() {
        return authorizationTokenService;
    }

    protected RapidSecurityContext getSecurityContext() {
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
    public void injectAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext securityContext) {
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
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    public void injectJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }

    @Lazy
    @Autowired
    public void injectIdIdConverter(IdConverter<ID> idIdConverter) {
        this.idConverter = idIdConverter;
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

