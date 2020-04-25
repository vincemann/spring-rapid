package com.naturalprogrammer.spring.lemon.auth.service;

import com.naturalprogrammer.spring.lemon.auth.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.mail.LemonMailData;
import com.naturalprogrammer.spring.lemon.auth.mail.MailSender;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonRole;
import com.naturalprogrammer.spring.lemon.auth.security.service.BlueTokenService;
import com.naturalprogrammer.spring.lemon.auth.security.service.GreenTokenService;
import com.naturalprogrammer.spring.lemon.auth.util.LecUtils;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ServiceComponent
public abstract class AbstractLemonService
	<U extends AbstractUser<ID>, ID extends Serializable,R extends AbstractUserRepository<U,ID>>
			extends JPACrudService<U,ID,R> {

    private static final Log log = LogFactory.getLog(AbstractLemonService.class);
	protected PasswordEncoder passwordEncoder;
	protected LemonProperties properties;
	protected BlueTokenService blueTokenService;
	protected GreenTokenService greenTokenService;
	protected MailSender mailSender;



	protected Map<String, Object> buildContext() {
		
		// make the context
		Map<String, Object> sharedProperties = new HashMap<String, Object>(2);
		sharedProperties.put("reCaptchaSiteKey", properties.getRecaptcha().getSitekey());
		sharedProperties.put("shared", properties.getShared());
		
		Map<String, Object> context = new HashMap<>();
		context.put("context", sharedProperties);
		
		return context;		
	}
	
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
			
			String verificationCode = greenTokenService.createToken(GreenTokenService.VERIFY_AUDIENCE,
					user.getId().toString(), properties.getJwt().getExpirationMillis(),
					LecUtils.mapOf("email", user.getEmail()));

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

		String forgotPasswordCode = greenTokenService.createToken(
				GreenTokenService.FORGOT_PASSWORD_AUDIENCE,
				user.getEmail(), properties.getJwt().getExpirationMillis());

		// make the link
		String forgotPasswordLink =	properties.getApplicationUrl()
			    + "/reset-password?code=" + forgotPasswordCode;
		
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

}
