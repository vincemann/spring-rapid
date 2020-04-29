package com.naturalprogrammer.spring.lemon.auth.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.fge.jsonpatch.JsonPatchException;
import com.naturalprogrammer.spring.lemon.auth.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.auth.domain.ChangePasswordForm;
import com.naturalprogrammer.spring.lemon.auth.domain.ResetPasswordForm;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import com.naturalprogrammer.spring.lemon.auth.util.*;
import io.github.vincemann.springrapid.acl.service.Secured;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import io.github.vincemann.springrapid.core.slicing.components.WebComponent;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * The Lemon API. See the
 * <a href="https://github.com/naturalprogrammer/spring-lemon#documentation-and-resources">
 * API documentation</a> for details.
 * 
 * @author Sanjay Patel
 */
@WebComponent
public abstract class LemonController
	<U extends AbstractUser<ID>, ID extends Serializable>
			extends RapidController<U,ID> {

	private static final Log log = LogFactory.getLog(LemonController.class);

    private long jwtExpirationMillis;
	private LemonService<U, ID,?> lemonService;

	public LemonController(DtoMappingContext dtoMappingContext) {
		super(dtoMappingContext);
	}

	public LemonController() {
	}


	@Autowired
	public void createLemonController(
			LemonProperties properties,
			@Secured LemonService<U, ID,? extends AbstractUserRepository<U,ID>> lemonService) {
		
		this.jwtExpirationMillis = properties.getJwt().getExpirationMillis();
		this.lemonService = lemonService;
		
		log.info("Created");
	}


	/**
	 * A simple function for pinging this server.
	 */
	@GetMapping("/ping")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ping() {
		
		log.debug("Received a ping");
	}
	

	/**
	 * Returns context properties needed at the client side,
	 * current-user data and an Authorization token as a response header.
	 */
	@GetMapping("/context")
	@ResponseBody
	public Map<String, Object> getContext(
			@RequestParam Optional<Long> expirationMillis,
			HttpServletResponse response) {

		log.debug("Getting context ");
		Map<String, Object> context = lemonService.getContext(expirationMillis, response);
		log.debug("Returning context: " + context);

		return context;
	}
	

	/**
	 * Signs up a user, and
	 * returns current-user data and an Authorization token as a response header.
	 */
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public LemonUserDto signup(@RequestBody @JsonView(UserUtils.SignupInput.class) U user,
							   HttpServletResponse response) throws BadEntityException {

		log.debug("Signing up: " + user);
		lemonService.signup(user);
		log.debug("Signed up: " + user);

		return userWithToken(response);
	}
	
	
	/**
	 * Resends verification mail
	 */
	@PostMapping("/users/{id}/resend-verification-mail")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resendVerificationMail(@PathVariable("id") U user) {
		
		log.debug("Resending verification mail for: " + user);
		lemonService.resendVerificationMail(user);
		log.debug("Resent verification mail for: " + user);
	}	


	/**
	 * Verifies current-user -> send code per email
	 */
	@PostMapping("/users/{id}/verification")
	@ResponseBody
	public LemonUserDto verifyUser(
			@PathVariable ID id,
			@RequestParam String code,
			HttpServletResponse response) {
		
		log.debug("Verifying user ...");		
		lemonService.verifyUser(id, code);
		
		return userWithToken(response);
	}
	

	/**
	 * The forgot Password feature -> mail new password to email
	 */
	@PostMapping("/forgot-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@RequestParam String email) {
		
		log.debug("Received forgot password request for: " + email);				
		lemonService.forgotPassword(email);
	}
	

	/**
	 * Resets password after it's forgotten
	 */
	@PostMapping("/reset-password")
	@ResponseBody
	public LemonUserDto resetPassword(
			@RequestBody ResetPasswordForm form,
			HttpServletResponse response) {
		
		log.debug("Resetting password ... ");				
		lemonService.resetPassword(form);
		
		return userWithToken(response);
	}


	/**
	 * Fetches a user by email
	 */
	@PostMapping("/users/fetch-by-email")
	@ResponseBody
	public IdentifiableEntity<ID> fetchUserByEmail(@RequestParam String email) throws DtoMappingException {
		
		log.debug("Fetching user by email: " + email);
		U byEmail = lemonService.findByEmail(email);
		LexUtils.ensureFound(byEmail);
		byEmail.setPassword(null);
		IdentifiableEntity<ID> dto = getDtoMapper().mapToDto(byEmail,
				findDtoClass(CrudDtoEndpoint.FIND, Direction.RESPONSE));
		return dto;
	}


	
	/**
	 * Updates a user
	 */
	@PatchMapping("/users/{id}")
	@ResponseBody
	public LemonUserDto authUpdate(
			@PathVariable("id") U user,
			@RequestBody String patch,
			HttpServletResponse response)
			throws IOException, JsonPatchException, BadEntityException, EntityNotFoundException, DtoMappingException {

		log.debug("Updating user ... ");

		// ensure that the user exists
		LexUtils.ensureFound(user);
		U updateUser = LmapUtils.applyPatch(user, patch); // create a patched form
		//default security Rule checks for write permission
		LemonUserDto updated = lemonService.updateUser(user, updateUser);
		// Send a new token for logged in user in the response
		userWithToken(response);
		return updated;
	}



	
	
	/**
	 * Changes password
	 */
	@PostMapping("/users/{id}/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@PathVariable("id") U user,
			@RequestBody ChangePasswordForm changePasswordForm,
			HttpServletResponse response) {
		
		log.debug("Changing password ... ");				
		String username = lemonService.changePassword(user, changePasswordForm);
		
		lemonService.addAuthHeader(response, username, jwtExpirationMillis);
	}


	/**
	 * Requests for changing email
	 */
	@PostMapping("/users/{id}/email-change-request")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void requestEmailChange(@PathVariable("id") ID userId,
								   @RequestBody U updatedUser) {
		
		log.debug("Requesting email change ... ");				
		lemonService.requestEmailChange(userId, updatedUser);
	}


	/**
	 * Changes the email
	 */
	@PostMapping("/users/{userId}/email")
	@ResponseBody
	public LemonUserDto changeEmail(
			@PathVariable ID userId,
			@RequestParam String code,
			HttpServletResponse response) {
		
		log.debug("Changing email of user ...");		
		lemonService.changeEmail(userId, code);
		
		// return the currently logged in user with new email
		return userWithToken(response);		
	}


	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
	@PostMapping("/fetch-new-auth-token")
	@ResponseBody
	public Map<String, String> fetchNewToken(
			@RequestParam Optional<Long> expirationMillis,
			@RequestParam Optional<String> username,
			HttpServletResponse response) {
		
		log.debug("Fetching a new token ... ");
		return LecUtils.mapOf("token", lemonService.fetchNewToken(expirationMillis, username));
	}


	/**
	 * Fetch a self-sufficient token with embedded UserDto - for interservice communications
	 */
	@GetMapping("/fetch-full-token")
	@ResponseBody
	public Map<String, String> fetchFullToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
		
		log.debug("Fetching a micro token");
		return lemonService.fetchFullToken(authHeader);
	}	

	
	/**
	 * returns the current user and puts a new authorization token in the response
	 */
	protected LemonUserDto userWithToken(HttpServletResponse response) {
		
		LemonUserDto currentUser = LecwUtils.currentUser();
		lemonService.addAuthHeader(response, currentUser.getUsername(), jwtExpirationMillis);
		return currentUser;
	}
}
