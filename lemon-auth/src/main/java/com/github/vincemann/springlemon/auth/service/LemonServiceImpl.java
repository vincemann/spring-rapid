package com.github.vincemann.springlemon.auth.service;


import com.nimbusds.jwt.JWTClaimsSet;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.mail.LemonMailData;
import com.github.vincemann.springlemon.auth.mail.MailSender;
import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.LemonRole;
import com.github.vincemann.springlemon.auth.util.LecUtils;
import com.github.vincemann.springlemon.auth.util.LecjUtils;
import com.github.vincemann.springlemon.auth.util.LecwUtils;
import com.github.vincemann.springlemon.auth.util.LemonUtils;
import com.github.vincemann.springlemon.exceptions.util.LexUtils;
import com.github.vincemann.springrapid.acl.Role;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.MapperUtils;
import com.github.vincemann.springrapid.core.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;


/**
 * The Lemon Service class
 *
 * @author Sanjay Patel
 */
@Validated
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public abstract class LemonServiceImpl<U extends AbstractUser<ID>, ID extends Serializable, R extends AbstractUserRepository<U, ID>>
            extends AbstractLemonService<U, ID, R>
                    implements LemonService<U, ID, R> {

    private static final Log log = LogFactory.getLog(LemonServiceImpl.class);


    @Autowired
    public void createLemonService(LemonProperties properties,
                                   PasswordEncoder passwordEncoder,
                                   MailSender<?> mailSender,
                                   AuthorizationTokenService authorizationTokenService,
                                   VerificationTokenService verificationTokenService) {

        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.authorizationTokenService = authorizationTokenService;
        this.verificationTokenService = verificationTokenService;
        log.info("Created");
    }

    /**
     * Creates a new user object. Must be overridden in the
     * subclass, like this:
     *
     * <pre>
     * public User newUser() {
     *    return new User();
     * }
     * </pre>
     */
    public abstract U newUser();


    /**
     * Returns the context data to be sent to the client,
     * i.e. <code>reCaptchaSiteKey</code> and all the properties
     * prefixed with <code>lemon.shared</code>.
     * <p>
     * To send custom properties, put those in your application
     * properties in the format <em>lemon.shared.fooBar</em>.
     * <p>
     * If a user is logged in, it also returns the user data
     * and a new authorization token. If expirationMillis is not provided,
     * the expiration of the new token is set to the default.
     * <p>
     * Override this method if needed.
     */
    public Map<String, Object> getContext(Optional<Long> expirationMillis, HttpServletResponse response) {

//        log.debug("Getting context ...");

        Map<String, Object> context = buildContext();

        LemonUserDto currentUser = LecwUtils.currentUser();
        if (currentUser != null) {
            addAuthHeader(response, currentUser.getEmail(),
                    expirationMillis.orElse(properties.getJwt().getExpirationMillis()));
            context.put("user", currentUser);
        }

        return context;
    }

    /**
     * Signs up a user.
     */
    //todo welcher validator kommt hier zum einsatz?
    //todo wo findet captcha statt, hier sollte captcha stattfinden, Aop Solution: https://medium.com/@cristi.rosu4/protecting-your-spring-boot-rest-endpoints-with-google-recaptcha-and-aop-31328a3f56b7
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U signup(U user) throws BadEntityException {
//        log.debug("Signing up user: " + user);
        U initialized = initUser(user);// sets right all fields of the user
        log.debug("initialized user: " + initialized);
        U saved = save(initialized);
        makeUnverified(saved); // make the user unverified
        log.debug("saved and unverified user: " + saved);
        return saved;
    }


    /**
     * Initializes the user based on the input data,
     * e.g. encrypts the password
     */
    protected U initUser(U user) throws BadEntityException {
        log.debug("Initializing user: " + user);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // encode the password
        return user;
    }


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    @Override
    public U save(U entity) throws BadEntityException {
        return super.save(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
    @Override
    public void deleteById(ID id) throws EntityNotFoundException, BadEntityException {
        super.deleteById(id);
    }

    /**
     * Makes a user unverified
     */
    protected void makeUnverified(U user) {
        super.makeUnverified(user);
        /*LecjUtils.afterCommit(() -> */
        sendVerificationMail(user);/*);*/// send a verification mail to the user
    }


    /**
     * Resends verification mail to the user.
     */
    public void resendVerificationMail(U user) throws EntityNotFoundException {

//        // The user must exist
//        LexUtils.ensureFound(user);

        EntityUtils.checkPresent(user,"User not found");
        // must be unverified
        LexUtils.validate(user.getRoles().contains(LemonRole.UNVERIFIED),
                "com.naturalprogrammer.spring.alreadyVerified").go();

        // send the verification mail
        sendVerificationMail(user);
    }


    /**
     * Fetches a user by email
     */
    public U findByEmail(String email) throws EntityNotFoundException {
//        log.debug("Fetching user by email: " + email);
        Optional<U> byEmail = getRepository().findByEmail(email);
        EntityUtils.checkPresent(byEmail,"Entity with email: " + email + " not found");
        return byEmail.get();
    }


    /**
     * Verifies the email id of current-user
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U verifyUser(U user, String verificationCode) throws EntityNotFoundException {

//        log.debug("Verifying user ...");

//        U user = getRepository().findById(userId).orElseThrow(LexUtils.notFoundSupplier());

        EntityUtils.checkPresent(user,"User not found");
        // ensure that he is unverified
        LexUtils.validate(user.hasRole(LemonRole.UNVERIFIED),
                "com.naturalprogrammer.spring.alreadyVerified").go();
        //verificationCode is jwtToken
        JWTClaimsSet claims = verificationTokenService.parseToken(verificationCode,
                VerificationTokenService.VERIFY_AUDIENCE, user.getCredentialsUpdatedMillis());

        LecUtils.ensureAuthority(
                claims.getSubject().equals(user.getId().toString()) &&
                        claims.getClaim("email").equals(user.getEmail()),
                "com.naturalprogrammer.spring.wrong.verificationCode");

        user.getRoles().remove(LemonRole.UNVERIFIED); // make him verified
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        U saved = getRepository().save(user);

        // Re-login the user, so that the UNVERIFIED role is removed
//		LemonUtils.login(saved);
        log.debug("Re-logged-in the user for removing UNVERIFIED role.");
//		// after successful commit,
//		LecjUtils.afterCommit(() -> {
//
//
//		});

        log.debug("Verified user: " + saved);
        return saved;
    }


    /**
     * Forgot password.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void forgotPassword(String email) throws EntityNotFoundException {

//        log.debug("Processing forgot password for email: " + email);

        // fetch the user record from database
        Optional<U> byId = getRepository().findByEmail(email);
        EntityUtils.checkPresent(byId,"User with email: "+email+" not found");
        U user = byId.get();
        mailForgotPasswordLink(user);
    }


    /**
     * Resets the password.
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U resetPassword(ResetPasswordForm form) throws EntityNotFoundException {
//        log.debug("Resetting password ...");

        JWTClaimsSet claims = verificationTokenService.parseToken(form.getCode(),
                VerificationTokenService.FORGOT_PASSWORD_AUDIENCE);

        String email = claims.getSubject();

        // fetch the user
        Optional<U> byId = getRepository().findByEmail(email);
        EntityUtils.checkPresent(byId,"User with email: "+email+" not found");
        U user = byId.get();
        LemonUtils.ensureCredentialsUpToDate(claims, user);

        // sets the password
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        //user.setForgotPasswordCode(null);

        U saved = getRepository().save(user);

        // Login the user
//		LemonUtils.login(saved);
//		// after successful commit,
//		LecjUtils.afterCommit(() -> {
//
//
//		});

        log.debug("Password reset.");
        return saved;
    }


//	@Override
//	public U update(U updatedUser, Boolean full) throws EntityNotFoundException, BadEntityException, BadEntityException {
////		// checks
////		Optional<U> byId = getRepository().findById(updatedUser.getId());
////		LexUtils.ensureFound(byId);
////		U old = byId.get();
////
////		return super.update(updatedUser, full);
//		throw new IllegalArgumentException("Call updateUser instead");
//	}

//	/**
//	 * Updates a user with the given data.
//	 */
//	@Override
//	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
//	public U updateUser(U old, U newUser) {
//
//		log.debug("Updating user: " + old);
//
//		// checks
////		LecjUtils.ensureCorrectVersion(user, updatedUser);
//
//		// delegates to updateUserFields
//		updateRoles(old, newUser, LecwUtils.currentUser());
//		U updated = getRepository().save(old);
//		log.debug("Updated user: " + old);
//
////		LemonUserDto userDto = user.toUserDto();
////		userDto.setPassword(null);
//		return updated;
//	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public U update(U update, Boolean full) throws EntityNotFoundException, BadEntityException, BadEntityException {
        Optional<U> old = getRepository().findById(update.getId());
        EntityUtils.checkPresent(old, "Entity to update with id: " + update.getId() + " not found");
        //update roles works in transaction -> changes are applied on the fly
        updateRoles(old.get(), update);
        update.setRoles(old.get().getRoles());
        return super.update(update, full);
    }

    /**
     * Changes the password.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public String changePassword(U user, ChangePasswordForm changePasswordForm) throws EntityNotFoundException {

//        log.debug("Changing password for user: " + user);

        // Get the old password of the logged in user (logged in user may be an ADMIN)
//        LemonUserDto currentUser = LecwUtils.currentUser();
//        U loggedIn = getRepository().findById(toId(currentUser.getId())).get();
        EntityUtils.checkPresent(user,"User not found");
        String oldPassword = user.getPassword();

        // checks
        LexUtils.validateField("changePasswordForm.oldPassword",
                passwordEncoder.matches(changePasswordForm.getOldPassword(),
                        oldPassword),
                "com.naturalprogrammer.spring.wrong.password").go();

        // sets the password
        user.setPassword(passwordEncoder.encode(changePasswordForm.getPassword()));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        getRepository().save(user);

        log.debug("Changed password for user: " + user);
        return user.toUserDto().getEmail();
    }


    /**
     * Updates the fields of the users. Override this if you have more fields.
     */
    protected void updateRoles(U old, U newUser) {
        log.debug("Updating user fields for user: " + old);
        // update the roles

        if (old.getRoles().equals(newUser.getRoles())) // roles are same
            return;

        if (newUser.hasRole(LemonRole.UNVERIFIED)) {

            if (!old.hasRole(LemonRole.UNVERIFIED)) {
                makeUnverified(old); // make user unverified
            }
        } else {

            if (old.hasRole(LemonRole.UNVERIFIED))
                old.getRoles().remove(LemonRole.UNVERIFIED); // make user verified
        }

        old.setRoles(newUser.getRoles());
        old.setCredentialsUpdatedMillis(System.currentTimeMillis());
    }


    /**
     * Requests for email change.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void requestEmailChange(U user, /*@Valid*/ RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException {
//        log.debug("Requesting email change for user" + user);
        // checks
//        Optional<U> byId = getRepository().findById(userId);
//        LexUtils.ensureFound(byId);
//        U user = byId.get();
        EntityUtils.checkPresent(user,"User not found");
        LexUtils.validateField("updatedUser.password",
                passwordEncoder.matches(emailChangeForm.getPassword(),
                        user.getPassword()),
                "com.naturalprogrammer.spring.wrong.password").go();

        // preserves the new email id
        user.setNewEmail(emailChangeForm.getNewEmail());
        //user.setChangeEmailCode(LemonUtils.uid());
        U saved = getRepository().save(user);

        // after successful commit, mails a link to the user
        LecjUtils.afterCommit(() -> mailChangeEmailLink(saved));

        log.debug("Requested email change: " + user);
    }


    /**
     * Mails the change-email verification link to the user.
     */
    protected void mailChangeEmailLink(U user) {

        String changeEmailCode = verificationTokenService.createToken(
                VerificationTokenService.CHANGE_EMAIL_AUDIENCE,
                user.getId().toString(), properties.getJwt().getExpirationMillis(),
                LecUtils.mapOf("newEmail", user.getNewEmail()));

        try {

            log.debug("Mailing change email link to user: " + user);

            // make the link
            String changeEmailLink = properties.getApplicationUrl()
                    + "/users/" + user.getId()
                    + "/change-email?code=" + changeEmailCode;

            // mail it
            mailChangeEmailLink(user, changeEmailLink);

            log.debug("Change email link mail queued.");

        } catch (Throwable e) {
            // In case of exception, just log the error and keep silent
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * Mails the change-email verification link to the user.
     * <p>
     * Override this method if you're using a different MailData
     */
    protected void mailChangeEmailLink(U user, String changeEmailLink) {

        mailSender.send(LemonMailData.of(user.getNewEmail(),
                LexUtils.getMessage(
                        "com.naturalprogrammer.spring.changeEmailSubject"),
                LexUtils.getMessage(
                        "com.naturalprogrammer.spring.changeEmailEmail",
                        changeEmailLink)));
    }


    /**
     * Change the email.
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U changeEmail(U user, /*@Valid @NotBlank*/ String changeEmailCode) throws EntityNotFoundException {

//        log.debug("Changing email of current user ...");

//        // fetch the current-user
//        LemonUserDto currentUser = LecwUtils.currentUser();
//
//        LexUtils.validate(userId.equals(toId(currentUser.getId())),
//                "com.naturalprogrammer.spring.wrong.login").go();
//
//        U user = getRepository().findById(userId).orElseThrow(LexUtils.notFoundSupplier());

        EntityUtils.checkPresent(user,"User not found");
        LexUtils.validate(StringUtils.isNotBlank(user.getNewEmail()),
                "com.naturalprogrammer.spring.blank.newEmail").go();

        JWTClaimsSet claims = verificationTokenService.parseToken(changeEmailCode,
                VerificationTokenService.CHANGE_EMAIL_AUDIENCE,
                user.getCredentialsUpdatedMillis());

        LecUtils.ensureAuthority(
                claims.getSubject().equals(user.getId().toString()) &&
                        claims.getClaim("newEmail").equals(user.getNewEmail()),
                "com.naturalprogrammer.spring.wrong.changeEmailCode");

        // Ensure that the email would be unique
        LexUtils.validate(
                !getRepository().findByEmail(user.getNewEmail()).isPresent(),
                "com.naturalprogrammer.spring.duplicate.email").go();

        // update the fields
        user.setEmail(user.getNewEmail());
        user.setNewEmail(null);
        //user.setChangeEmailCode(null);
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());

        // make the user verified if he is not
        if (user.hasRole(LemonRole.UNVERIFIED))
            user.getRoles().remove(LemonRole.UNVERIFIED);

        U saved = getRepository().save(user);

        // Login the user
//		LemonUtils.login(saved);
//		// after successful commit,
//		LecjUtils.afterCommit(() -> {
//
//
//		});

        log.debug("Changed email of user: " + user);
        return saved;
    }


    /**
     * Fetches a new token - for session scrolling etc.
     *
     * @return
     */
    public String fetchNewToken(Optional<Long> expirationMillis,
                                Optional<String> optionalUsername) {

        LemonUserDto currentUser = LecwUtils.currentUser();
        String username = optionalUsername.orElse(currentUser.getEmail());

        LecUtils.ensureAuthority(currentUser.getEmail().equals(username) ||
                currentUser.isGoodAdmin(), "com.naturalprogrammer.spring.notGoodAdminOrSameUser");

        //todo kann sich hier jeder user nen token mit beliebiger expiration ausstellen lassen?
        //ist das ein problem?
        return LecUtils.TOKEN_PREFIX +
                authorizationTokenService.createToken(AuthorizationTokenService.AUTH_AUDIENCE, username,
                        expirationMillis.orElse(properties.getJwt().getExpirationMillis()));
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    //only called internally
    public void createAdminUser(LemonProperties.Admin admin) throws BadEntityException {
//        log.info("Creating admin user: " + admin.getEmail());

        // create the user
        U user = newUser();
        user.setEmail(admin.getEmail());
        user.setPassword(passwordEncoder.encode(
                admin.getPassword()));
        user.getRoles().add(Role.ADMIN);
        getRepository().save(user);
        log.debug("admin saved.");
    }


//	/**
//	 * Hides the confidential fields before sending to client
//	 */
//	protected void hideConfidentialFields(U user) {
//
//		user.setPassword(null); // JsonIgnore didn't work
//		try {
//			securityChecker.checkPermission(user.getId(),user.getClass(),"WRITE");
//		}catch (AccessDeniedException e){
//			user.setEmail(null);
//		}
//
////		if (!user.hasPermission(LecwUtils.currentUser(), BasePermission.WRITE.toString()))
//
//
//		log.debug("Hid confidential fields for user: " + user);
//	}


    public Map<String, String> fetchFullToken(String authHeader) {

        LecUtils.ensureCredentials(authorizationTokenService.parseClaim(authHeader.substring(LecUtils.TOKEN_PREFIX_LENGTH),
                AuthorizationTokenService.USER_CLAIM) == null, "com.naturalprogrammer.spring.fullTokenNotAllowed");

        LemonUserDto currentUser = LecwUtils.currentUser();

        Map<String, Object> claimMap = Collections.singletonMap(AuthorizationTokenService.USER_CLAIM,
                MapperUtils.serialize(currentUser)); // Not serializing converts it to a JsonNode

        Map<String, String> tokenMap = Collections.singletonMap("token", LecUtils.TOKEN_PREFIX +
                authorizationTokenService.createToken(AuthorizationTokenService.AUTH_AUDIENCE, currentUser.getEmail(),
                        Long.valueOf(properties.getJwt().getShortLivedMillis()),
                        claimMap));

        return tokenMap;
    }


    /**
     * Adds a Lemon-Authorization header to the response
     */
    public void addAuthHeader(HttpServletResponse response, String username, Long expirationMillis) {

        response.addHeader(LecUtils.TOKEN_RESPONSE_HEADER_NAME, LecUtils.TOKEN_PREFIX +
                authorizationTokenService.createToken(AuthorizationTokenService.AUTH_AUDIENCE, username, expirationMillis));
    }


    public Optional<U> findUserById(String id) {
        return getRepository().findById(toId(id));
    }
}
