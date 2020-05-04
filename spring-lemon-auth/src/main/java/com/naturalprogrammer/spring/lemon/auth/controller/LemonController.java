package com.naturalprogrammer.spring.lemon.auth.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.fge.jsonpatch.JsonPatchException;
import com.naturalprogrammer.spring.lemon.auth.domain.*;
import com.naturalprogrammer.spring.lemon.auth.properties.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import com.naturalprogrammer.spring.lemon.auth.util.*;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import io.github.vincemann.springrapid.acl.service.Secured;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.CrudDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.rapid.DtoSerializingException;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.slicing.components.WebComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
	<U extends AbstractUser<ID>, ID extends Serializable,S extends LemonSignupForm>
			extends RapidController<U,ID,LemonService<U, ID,?>> {

	private static final Log log = LogFactory.getLog(LemonController.class);
//	public static final Long DEFAULT_JWT_EXPIRATION_MILLIS = 864000000L;// 10 days

    private long jwtExpirationMillis;

	public LemonController(DtoMappingContext dtoMappingContext) {
		super(dtoMappingContext);
		log.info("Created");
	}

//	public LemonController() {
//	}

	@Autowired
//	@Lazy
	public void injectProperties(LemonProperties properties){
		this.jwtExpirationMillis =/* properties.getJwt() == null
				? DEFAULT_JWT_EXPIRATION_MILLIS
				:*/ properties.getJwt().getExpirationMillis();
	}

	@Autowired
	@Secured
	@Override
	public void injectCrudService(LemonService<U, ID, ?> crudService) {
		super.injectCrudService(crudService);
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
		Map<String, Object> context = getCrudService().getContext(expirationMillis, response);
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
	public LemonUserDto signup(@RequestBody @JsonView(UserUtils.SignupInput.class) S signupForm,
							   HttpServletResponse response) throws BadEntityException, DtoMappingException {

		log.debug("Signing up: " + signupForm);
		U user = getDtoMapper().mapToEntity(signupForm, getEntityClass());
		U saved = getCrudService().signup(user);
		log.debug("Signed up: " + signupForm);

		return userWithToken(response,saved);
	}


	/**
	 * Resends verification mail
	 */
	@PostMapping("/users/{id}/resend-verification-mail")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resendVerificationMail(@PathVariable("id") U user) {

		log.debug("Resending verification mail for: " + user);
		getCrudService().resendVerificationMail(user);
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
		U saved = getCrudService().verifyUser(id, code);

		return userWithToken(response,saved);
	}


	/**
	 * The forgot Password feature -> mail new password to email
	 */
	@PostMapping("/forgot-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@RequestParam String email) {

		log.debug("Received forgot password request for: " + email);
		getCrudService().forgotPassword(email);
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
		U saved = getCrudService().resetPassword(form);

		return userWithToken(response,saved);
	}


	/**
	 * Fetches a user by email
	 */
	@PostMapping("/users/fetch-by-email")
	@ResponseBody
	public Object fetchUserByEmail(@RequestParam String email) throws DtoMappingException {

		log.debug("Fetching user by email: " + email);
		U byEmail = getCrudService().findByEmail(email);
		LexUtils.ensureFound(byEmail);
		byEmail.setPassword(null);
		Object dto = getDtoMapper().mapToDto(byEmail,
				findDtoClass(CrudDtoEndpoint.FIND, Direction.RESPONSE));
		return dto;
	}




	//todo remove and replace with rapidController update endpoint entirely...
	/**
	 * Updates a user
	 */
	@PatchMapping("/users/{id}")
	@ResponseBody
	public LemonUserDto authUpdate(
			@PathVariable("id") ID userId,
			@RequestBody String patch,
			HttpServletResponse response)
			throws IOException, JsonPatchException, BadEntityException, EntityNotFoundException, DtoMappingException {

		log.debug("Updating user ... ");

		// ensure that the user exists
		LexUtils.ensureFound(userId);
		Optional<U> byId = getCrudService().findById(userId);
		U updateUser = LmapUtils.applyPatch(byId.get(), patch); // create a patched form
		//default security Rule checks for write permission
		U updated = getCrudService().updateUser(byId.get(), updateUser);
		LemonUserDto dto = updated.toUserDto();
		dto.setPassword(null);
		// Send a new token for logged in user in the response

		return dto;
	}

	@Override
	public void afterUpdate(Object dto, U updated, HttpServletRequest httpServletRequest, boolean full, HttpServletResponse response) {
		super.afterUpdate(dto, updated, httpServletRequest, full, response);
		userWithToken(response,updated);
	}

	@Override
	protected U serviceUpdate(U update, boolean full) throws BadEntityException, EntityNotFoundException {
		return super.serviceUpdate(update, full);
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
		String username = getCrudService().changePassword(user, changePasswordForm);

		getCrudService().addAuthHeader(response, username, jwtExpirationMillis);
	}


	/**
	 * Requests for changing email
	 */
	@PostMapping("/users/{id}/email-change-request")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void requestEmailChange(@PathVariable("id") ID userId,
								   @RequestBody RequestEmailChangeForm emailChangeForm) {

		log.debug("Requesting email change ... ");
		getCrudService().requestEmailChange(userId, emailChangeForm);
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
		U saved = getCrudService().changeEmail(userId, code);

		// return the currently logged in user with new email
		return userWithToken(response,saved);
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
		return LecUtils.mapOf("token", getCrudService().fetchNewToken(expirationMillis, username));
	}


	/**
	 * Fetch a self-sufficient token with embedded UserDto - for interservice communications
	 */
	@GetMapping("/fetch-full-token")
	@ResponseBody
	public Map<String, String> fetchFullToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

		log.debug("Fetching a micro token");
		return getCrudService().fetchFullToken(authHeader);
	}


	/**
	 * returns the current user and puts a new authorization token in the response
	 */
	protected LemonUserDto userWithToken(HttpServletResponse response,U saved) {
		LemonUtils.login(saved);
		LemonUserDto currentUser = LecwUtils.currentUser();
		getCrudService().addAuthHeader(response, currentUser.getEmail(), jwtExpirationMillis);
		return currentUser;
	}
}
