package com.github.vincemann.springlemon.auth.service;


import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springlemon.auth.util.*;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.mail.LemonMailData;
import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.LemonRole;
import com.github.vincemann.springlemon.exceptions.util.LexUtils;
import com.github.vincemann.springrapid.core.security.RapidRole;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityAssert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * The Lemon Service class
 *
 * @author Sanjay Patel
 */
@Validated
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
@Slf4j
public abstract class LemonServiceImpl<U extends AbstractUser<ID>, ID extends Serializable, R extends AbstractUserRepository<U, ID>>
            extends AbstractLemonService<U, ID, R>
                    implements LemonService<U, ID, R> {

    protected static final String CHANGE_EMAIL_AUDIENCE = "change-email";

    private AuthorizationTokenService<LemonAuthenticatedPrincipal> authorizationTokenService;
    private RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext;




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


    @Override
    public Map<String, Object> getSharedProperties() {

        // make the context
        Map<String, Object> sharedProperties = new HashMap<String, Object>(2);
        sharedProperties.put("reCaptchaSiteKey", properties.getRecaptcha().getSitekey());
        sharedProperties.put("shared", properties.getShared());

        Map<String, Object> context = new HashMap<>();
        context.put("context", sharedProperties);

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

        EntityAssert.isPresent(user,"User not found");
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
        EntityAssert.isPresent(byEmail,"Entity with email: " + email + " not found");
        return byEmail.get();
    }


    /**
     * Verifies the email id of current-user
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U verifyUser(U user, String verificationCode) throws EntityNotFoundException, BadTokenException {

//        log.debug("Verifying user ...");

//        U user = getRepository().findById(userId).orElseThrow(LexUtils.notFoundSupplier());

        EntityAssert.isPresent(user,"User not found");
        // ensure that he is unverified
        LexUtils.validate(user.hasRole(LemonRole.UNVERIFIED),
                "com.naturalprogrammer.spring.alreadyVerified").go();
        //verificationCode is jwtToken
        JWTClaimsSet claims = parseToken(verificationCode,
                VERIFY_AUDIENCE, user.getCredentialsUpdatedMillis());

        LemonValidationUtils.ensureAuthority(
                claims.getSubject().equals(user.getId().toString()) &&
                        claims.getClaim("email").equals(user.getEmail()),
                "com.naturalprogrammer.spring.wrong.verificationCode");

        user.getRoles().remove(LemonRole.UNVERIFIED); // make him verified
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        U saved = getRepository().save(user);

        // Re-login the user, so that the UNVERIFIED role is removed
//		LemonValidationUtils.login(saved);
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
        EntityAssert.isPresent(byId,"User with email: "+email+" not found");
        U user = byId.get();
        mailForgotPasswordLink(user);
    }


    /**
     * Resets the password.
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public U resetPassword(ResetPasswordForm form) throws EntityNotFoundException, BadTokenException {
//        log.debug("Resetting password ...");

        JWTClaimsSet claims = parseToken(form.getCode(), FORGOT_PASSWORD_AUDIENCE);

        String email = claims.getSubject();

        // fetch the user
        Optional<U> byId = getRepository().findByEmail(email);
        EntityAssert.isPresent(byId,"User with email: "+email+" not found");
        U user = byId.get();
        LemonValidationUtils.ensureCredentialsUpToDate(claims, user);

        // sets the password
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        user.setCredentialsUpdatedMillis(System.currentTimeMillis());
        //user.setForgotPasswordCode(null);

        U saved = getRepository().save(user);

        // Login the user
//		LemonValidationUtils.login(saved);
//		// after successful commit,
//		LecjUtils.afterCommit(() -> {
//
//
//		});

        log.debug("Password reset.");
        return saved;
    }


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public U update(U update, Boolean full) throws EntityNotFoundException, BadEntityException, BadEntityException {
        Optional<U> old = getRepository().findById(update.getId());
        EntityAssert.isPresent(old, "Entity to update with id: " + update.getId() + " not found");
        //update roles works in transaction -> changes are applied on the fly
        updateRoles(old.get(), update);
        update.setRoles(old.get().getRoles());
        return super.update(update, full);
    }

    /**
     * Changes the password.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void changePassword(U user, ChangePasswordForm changePasswordForm) throws EntityNotFoundException {

//        log.debug("Changing password for user: " + user);

        // Get the old password of the logged in user (logged in user may be an ADMIN)
//        LemonUserDto currentUser = LecwUtils.currentUser();
//        U loggedIn = getRepository().findById(toId(currentUser.getId())).get();
        EntityAssert.isPresent(user,"User not found");
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
//        return user.toUserDto().getEmail();
    }


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
        EntityAssert.isPresent(user,"User not found");
        LexUtils.validateField("updatedUser.password",
                passwordEncoder.matches(emailChangeForm.getPassword(),
                        user.getPassword()),
                "com.naturalprogrammer.spring.wrong.password").go();

        // preserves the new email id
        user.setNewEmail(emailChangeForm.getNewEmail());
        //user.setChangeEmailCode(LemonValidationUtils.uid());
        U saved = getRepository().save(user);

        // after successful commit, mails a link to the user
        TransactionalUtils.afterCommit(() -> mailChangeEmailLink(saved));

        log.debug("Requested email change: " + user);
    }


    /**
     * Mails the change-email verification link to the user.
     */
    protected void mailChangeEmailLink(U user) {

        String changeEmailCode = createToken(
                CHANGE_EMAIL_AUDIENCE,
                user.getId().toString(),
                properties.getJwt().getExpirationMillis(),
                LemonValidationUtils.mapOf("newEmail", user.getNewEmail()));

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
    public U changeEmail(U user, /*@Valid @NotBlank*/ String changeEmailCode) throws EntityNotFoundException, BadTokenException {

//        log.debug("Changing email of current user ...");

//        // fetch the current-user
//        LemonUserDto currentUser = LecwUtils.currentUser();
//
//        LexUtils.validate(userId.equals(toId(currentUser.getId())),
//                "com.naturalprogrammer.spring.wrong.login").go();
//
//        U user = getRepository().findById(userId).orElseThrow(LexUtils.notFoundSupplier());

        EntityAssert.isPresent(user,"User not found");
        LexUtils.validate(StringUtils.isNotBlank(user.getNewEmail()),
                "com.naturalprogrammer.spring.blank.newEmail").go();

        JWTClaimsSet claims = parseToken(changeEmailCode,
                CHANGE_EMAIL_AUDIENCE,
                user.getCredentialsUpdatedMillis());

        LemonValidationUtils.ensureAuthority(
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
//		LemonValidationUtils.login(saved);
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
    @Override
    public String fetchNewAuthToken(Optional<String> optionalEmail) {

        LemonAuthenticatedPrincipal currentUser = securityContext.currentPrincipal();
//        LemonUserDto currentUser = LecwUtils.currentUser();
        String email = optionalEmail.orElse(currentUser.getEmail());

        //todo den check durch nen acl check ersetzen maybe
        LemonValidationUtils.ensureAuthority(currentUser.getEmail().equals(email) ||
                currentUser.isGoodAdmin(), "com.naturalprogrammer.spring.notGoodAdminOrSameUser");

        //todo kann sich hier jeder user nen token mit beliebiger expiration ausstellen lassen?
        //ist das ein problem?
        return authorizationTokenService.createToken(currentUser);
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
        user.getRoles().add(RapidRole.ADMIN);
        getRepository().save(user);
        log.debug("admin saved.");
    }



    @Autowired
    public void injectAuthorizationTokenService(AuthorizationTokenService<LemonAuthenticatedPrincipal> authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }

    public Optional<U> findUserById(String id) {
        return getRepository().findById(toId(id));
    }

    @Autowired

    public void injectSecurityContext(RapidSecurityContext<LemonAuthenticatedPrincipal> securityContext) {
        this.securityContext = securityContext;
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


    //    //todo I dont think that i need this. Every information needed by the client is in the UserDto he can get by sending GET user?id=myId
//    public Map<String, String> fetchFullToken(String authHeader) {
//
//        LecUtils.ensureCredentials(authorizationTokenService.parseClaim(authHeader.substring(JwtService.TOKEN_PREFIX_LENGTH),
//                AuthorizationTokenService.USER_CLAIM) == null, "com.naturalprogrammer.spring.fullTokenNotAllowed");
//
//        LemonUserDto currentUser = LecwUtils.currentUser();
//
//        Map<String, Object> claimMap = Collections.singletonMap(AuthorizationTokenService.USER_CLAIM,
//                MapperUtils.serialize(currentUser)); // Not serializing converts it to a JsonNode
//
//        Map<String, String> tokenMap = Collections.singletonMap("token", JwtService.TOKEN_PREFIX +
//                authorizationTokenService.createToken(AuthorizationTokenService.AUTH_AUDIENCE, currentUser.getEmail(),
//                        Long.valueOf(properties.getJwt().getShortLivedMillis()),
//                        claimMap));
//
//        return tokenMap;
//    }




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
}
