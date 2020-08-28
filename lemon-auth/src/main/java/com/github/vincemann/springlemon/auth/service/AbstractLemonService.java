package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.mail.LemonMailData;
import com.github.vincemann.springlemon.auth.mail.MailSender;
import com.github.vincemann.springlemon.auth.domain.LemonRole;
import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springlemon.auth.service.token.JweTokenService;
import com.github.vincemann.springlemon.auth.util.LemonValidationUtils;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.JPACrudService;
import com.github.vincemann.springlemon.exceptions.util.LexUtils;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ServiceComponent
@Slf4j
public abstract class AbstractLemonService
	<U extends AbstractUser<ID>, ID extends Serializable,R extends AbstractUserRepository<U,ID>>
			extends JPACrudService<U,ID,R> {

	protected static final String VERIFY_AUDIENCE = "verify";
	protected static final String FORGOT_PASSWORD_AUDIENCE = "forgot-password";


	protected PasswordEncoder passwordEncoder;
	protected LemonProperties properties;
	protected JweTokenService jweTokenService;
	protected MailSender mailSender;



	protected U initUser(U user) throws BadEntityException {
		
		log.debug("Initializing user: " + user);

		user.setPassword(passwordEncoder.encode(user.getPassword())); // encode the password
		return user;
	}
	
	/**
	 * Makes a user unverified
	 */
	protected void makeUnverified(U user) {
		user.getRoles().add(LemonRole.UNVERIFIED);
		user.setCredentialsUpdatedMillis(System.currentTimeMillis());
	}
    
	/**
	 * Sends verification mail to a unverified user.
	 */
	protected void sendVerificationMail(final U user) {
		try {
			
			log.debug("Sending verification mail to: " + user);
			
			String verificationCode = createToken(
					VERIFY_AUDIENCE,
					user.getId().toString(),
					properties.getJwt().getExpirationMillis(),
					//payload
					LemonValidationUtils.mapOf("email", user.getEmail()));

			// make the link
			String verifyLink = properties.getApplicationUrl()
				+ "/users/" + user.getId() + "/verification?code=" + verificationCode;

			// send the mail
			sendVerificationMail(user, verifyLink);

			log.debug("Verification mail to " + user.getEmail() + " queued.");
			
		} catch (Throwable e) {
			// In case of exception, just log the error and keep silent
			log.error(ExceptionUtils.getStackTrace(e));
		}
	}	
	
	/**
	 * Sends verification mail to a unverified user.
	 * Override this method if you're using a different MailData
	 */
	protected void sendVerificationMail(final U user, String verifyLink) {
		
		// send the mail
		mailSender.send(LemonMailData.of(user.getEmail(),
			LexUtils.getMessage("com.naturalprogrammer.spring.verifySubject"),
			LexUtils.getMessage(
				"com.naturalprogrammer.spring.verifyEmail",	verifyLink)));
	}	
	
	/**
	 * Mails the forgot password link.
	 * 
	 * @param user
	 */
	public void mailForgotPasswordLink(U user) {
		
		log.debug("Mailing forgot password link to user: " + user);

		String forgotPasswordCode = createToken(FORGOT_PASSWORD_AUDIENCE,
				user.getEmail(),
				properties.getJwt().getExpirationMillis()
		);

		// make the link
		String forgotPasswordLink =	properties.getApplicationUrl() + "/reset-password?code=" + forgotPasswordCode;
		
		mailForgotPasswordLink(user, forgotPasswordLink);
		
		log.debug("Forgot password link mail queued.");
	}

	
	/**
	 * Mails the forgot password link.
	 * 
	 * Override this method if you're using a different MailData
	 */
	public void mailForgotPasswordLink(U user, String forgotPasswordLink) {
		
		// send the mail
		mailSender.send(LemonMailData.of(user.getEmail(),
				LexUtils.getMessage("com.naturalprogrammer.spring.forgotPasswordSubject"),
				LexUtils.getMessage("com.naturalprogrammer.spring.forgotPasswordEmail",
					forgotPasswordLink)));
	}
	protected String createToken(String aud, String subject, long expirationMillis){
		return createToken(aud,subject,expirationMillis,new HashMap<>());
	}

	protected String createToken(String aud, String subject, long expirationMillis, Map<String,Object> otherClaims)	{
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

		builder
				//.issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + expirationMillis))
				.audience(aud)
				.subject(subject)
				.issueTime(new Date());

		otherClaims.forEach(builder::claim);
		JWTClaimsSet claims = builder.build();
		return jweTokenService.createToken(claims);
	}

	protected JWTClaimsSet parseToken(String token, String audience) throws BadTokenException {
		JWTClaimsSet claims = jweTokenService.parseToken(token);
		LemonValidationUtils.ensureCredentials(audience != null &&
						claims.getAudience().contains(audience),
				"com.naturalprogrammer.spring.wrong.audience");
		long expirationTime = claims.getExpirationTime().getTime();
		long currentTime = System.currentTimeMillis();

		log.debug("Parsing JWT. Expiration time = " + expirationTime
				+ ". Current time = " + currentTime);

		LemonValidationUtils.ensureCredentials(expirationTime >= currentTime,
				"com.naturalprogrammer.spring.expiredToken");
		return claims;
	}

	protected JWTClaimsSet parseToken(String token, String expectedAud,long issuedAfter) throws BadTokenException {
		JWTClaimsSet claims = parseToken(token, expectedAud);
		long issueTime = claims.getIssueTime().getTime();
		LemonValidationUtils.ensureCredentials(issueTime >= issuedAfter,
				"com.naturalprogrammer.spring.obsoleteToken");
		return claims;
	}

	@Autowired
	public void injectPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void injectProperties(LemonProperties properties) {
		this.properties = properties;
	}

	@Autowired
	public void injectJweTokenService(JweTokenService jweTokenService) {
		this.jweTokenService = jweTokenService;
	}

	@Autowired
	public void injectMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
}
