package com.github.vincemann.springlemon.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.aoplog.api.Lp;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.LemonSignupForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonAdminUpdateUserDto;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonFindForeignDto;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonReadUserDto;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springlemon.auth.service.token.HttpTokenService;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.util.LemonMapUtils;

import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.security.RapidRoles;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingInfo;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.RapidDtoEndpoint;
import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.WebComponent;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * Lemon API.
 *
 * @author Sanjay Patel
 * @modified vincemann
 *
 */
@WebComponent
@Slf4j
public abstract class AbstractUserController<U extends AbstractUser<ID>, ID extends Serializable>
			extends RapidController<U,ID, UserService<U, ID>>  {

	private UserService<U, ID> unsecuredUserService;
	private HttpTokenService httpTokenService;

	/**
	 * Returns public shared context properties needed at the client side,
	 */
	@GetMapping("/context")
	@ResponseBody
	public Map<String, Object> getContext() {

		log.debug("Getting context ");
		Map<String, Object> context = getService().getContext();
		log.debug("Returning context: " + context);
		return context;
	}

	@Override
	public DtoMappingContext provideDtoMappingContext() {
		return LemonDtoMappingContextBuilder.builder()
				.withAllPrincipals()
				.forAll(LemonUserDto.class)
				.forResponse(LemonReadUserDto.class)
				.forEndpoint(LemonDtoEndpoint.SIGN_UP, Direction.REQUEST, LemonSignupForm.class)

				.withPrincipal(DtoMappingInfo.Principal.FOREIGN)
				.forEndpoint(LemonDtoEndpoint.FETCH_BY_EMAIL,Direction.RESPONSE, LemonFindForeignDto.class)

				.withAllPrincipals()
				.withRoles(RapidRoles.ADMIN)
				.forEndpoint(RapidDtoEndpoint.UPDATE, LemonAdminUpdateUserDto.class)

				.build();
	}

	/**
	 * Signs up a user, and
	 * returns current-user data and an Authorization token as a response header.
	 */
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ResponseEntity<String> signup(/*@RequestBody @JsonView(UserUtils.SignupInput.class) S signupForm,*/
							   @Lp HttpServletRequest request,
							   HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException {

		String signupForm = readBody(request);
		Object signupDto = getJsonMapper().readValue(signupForm,
				createDtoClass(LemonDtoEndpoint.SIGN_UP, Direction.REQUEST, null));
		getValidationStrategy().validateDto(signupDto);
		log.debug("Signing up: " + signupDto);
		U user = getDtoMapper().mapToEntity(signupDto, getEntityClass());
		U saved = getService().signup(user);
		log.debug("Signed up: " + signupForm);

		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(LemonDtoEndpoint.SIGN_UP, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * Resends verification mail
	 */
	@PostMapping("/users/{id}/resend-verification-mail")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	
	public void resendVerificationMail(@PathVariable("id") ID id) throws BadEntityException, EntityNotFoundException {
		log.debug("Resending verification mail for user with id " + id);
		U user = fetchUser(id);
		getService().resendVerificationMail(user);
	}


	/**
	 * Verifies current-user -> send code per email
	 */
	@PostMapping("/users/{id}/verification")
	@ResponseBody
	
	public ResponseEntity<String> verifyUser(
			@Lp @PathVariable("id") ID id,
			@Lp @RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException {
		getValidationStrategy().validateId(id);
		log.debug("Verifying user with id: " + id);
		U user = fetchUser(id);
		U saved = getService().verifyUser(user, code);

		appendFreshToken(response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(LemonDtoEndpoint.VERIFY_USER, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * The forgot Password feature -> mail new password to email
	 */
	@PostMapping("/forgot-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@RequestParam String email) throws EntityNotFoundException {
		log.debug("Received forgot password request for: " + email);
		getService().forgotPassword(email);
	}


	/**
	 * Resets password after it's forgotten
	 */
	@PostMapping("/reset-password")
	@ResponseBody
	public ResponseEntity<String> resetPassword(
			@Lp @RequestBody ResetPasswordForm form,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException {

		log.debug("Resetting password ... ");
		U saved = getService().resetPassword(form);
		appendFreshToken(response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(LemonDtoEndpoint.RESET_PASSWORD, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * Fetches a user by email
	 */
	@PostMapping("/users/fetch-by-email")
	@ResponseBody
	public ResponseEntity<String> fetchUserByEmail(@RequestParam String email) throws JsonProcessingException, BadEntityException, EntityNotFoundException {
		log.debug("Fetching user by email: " + email);
		Optional<U> byEmail = getService().findByEmail(email);
		VerifyEntity.isPresent(byEmail,"User with email: "+email+" not found");
		U user = byEmail.get();
		Object responseDto = getDtoMapper().mapToDto(user,
				createDtoClass(RapidDtoEndpoint.FIND, Direction.RESPONSE, user));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}

//	@Override
//	public void afterUpdate(Object dto, U updated, HttpServletRequest httpServletRequest, HttpServletResponse response) {
//		super.afterUpdate(dto, updated, httpServletRequest, response);
//		appendFreshToken(response);
//	}
	
	/**
	 * Changes password
	 */
	@PostMapping("/users/{id}/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@Lp @PathVariable("id") ID id,
			@Lp @RequestBody ChangePasswordForm changePasswordForm,
			HttpServletResponse response) throws BadEntityException, EntityNotFoundException {

		log.debug("Changing password of user with id: " + id);
		U user = fetchUser(id);
		getService().changePassword(user, changePasswordForm);
		appendFreshToken(response);
	}


	/**
	 * Requests for changing email
	 */
	@PostMapping("/users/{id}/email-change-request")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void requestEmailChange(@PathVariable("id") ID id,
								   @RequestBody RequestEmailChangeForm emailChangeForm) throws BadEntityException, EntityNotFoundException {
		log.debug("Requesting email change for user with " + id);
		U user = fetchUser(id);
		getService().requestEmailChange(user, emailChangeForm);
	}


	/**
	 * Changes the email
	 */
	@PostMapping("/users/{id}/email")
	@ResponseBody
	public ResponseEntity<String> changeEmail(
			@Lp @PathVariable("id") ID id,
			@Lp @RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException {

		log.debug("Changing email of user with id: " + id);
		U user = fetchUser(id);
		U saved = getService().changeEmail(user, code);
		appendFreshToken(response);
		Object responseDto = getDtoMapper().mapToDto(saved,
				createDtoClass(LemonDtoEndpoint.CHANGE_EMAIL, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}


	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
	@PostMapping("/fetch-new-auth-token")
	@ResponseBody
	public Map<String, String> fetchNewAuthToken(
			@RequestParam Optional<String> email) {

		log.debug("Fetching a new auth token ... ");
		String token;
		if (email.isEmpty()){
			token = getService().createNewAuthToken();
		}else {
			token = getService().createNewAuthToken(email.get());
		}
		// result = {token:asfsdfjsdjfnd}
		return LemonMapUtils.mapOf("token", token);
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
	 * Adds an Authorization header to the response for certain user
	 */
	public void appendFreshTokenOf(U user, HttpServletResponse response) {
		String token = getService().createNewAuthToken(user.getEmail());
		httpTokenService.appendToken(token,response);
//		response.addHeader(LecUtils.TOKEN_RESPONSE_HEADER_NAME, JwtService.TOKEN_PREFIX + token);
	}


	/**
	 * Adds an Authorization header to the response for logged in user
	 */
	public void appendFreshToken(HttpServletResponse response){
		String token = getService().createNewAuthToken();
		httpTokenService.appendToken(token,response);
	}

	protected U fetchUser(ID userId) throws BadEntityException, EntityNotFoundException {
		Optional<U> byId =  unsecuredUserService.findById(userId);
		VerifyEntity.isPresent(byId,"User with id: "+userId+" not found");
		return byId.get();
	}


	@Autowired
	public void injectHttpTokenService(HttpTokenService httpTokenService) {
		this.httpTokenService = httpTokenService;
	}

	@Autowired
	@Secured
	@Override
	public void injectCrudService(UserService<U, ID> crudService) {
		super.injectCrudService(crudService);
	}


	@Autowired
	@Unsecured
	public void injectUnsecuredService(UserService<U, ID> unsecuredService) {
		this.unsecuredUserService = unsecuredService;
	}




	//	/**
//	 * Fetch a self-sufficient token with embedded UserDto - for interservice communications
//	 */
//	@GetMapping("/fetch-full-token")
//	@ResponseBody
//	public Map<String, String> fetchFullToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
//		log.debug("Fetching a micro token");
//		return getService().fetchFullToken(authHeader);
//	}

//	@Override
//	protected U serviceUpdate(U update, boolean full) throws BadEntityException, EntityNotFoundException {
//		U updated = super.serviceUpdate(update, full);
//		//set password should not trigger immediate update
//		updated.setPassword(null);
//		return updated;
//	}

	
//	//todo remove and replace with rapidController update endpoint entirely...
//	/**
//	 * Updates a user
//	 */
//	@PatchMapping("/users/{id}")
//	@ResponseBody
//	public LemonUserDto authUpdate(
//			@PathVariable("id") ID userId,
//			@RequestBody String patch,
//			HttpServletResponse response)
//			throws IOException, JsonPatchException, BadEntityException, EntityNotFoundException, DtoMappingException {
//
//		log.debug("Updating user ... ");
//
//		// ensure that the user exists
//		LexUtils.ensureFound(userId);
//		Optional<U> byId = getCrudService().findById(userId);
//		U updateUser = LmapUtils.applyPatch(byId.get(), patch); // create a patched form
//		//default security Rule checks for write permission
//		U updated = getCrudService().updateUser(byId.get(), updateUser);
//		LemonUserDto dto = updated.toUserDto();
//		dto.setPassword(null);
//		// Send a new token for logged in user in the response
//
//		return dto;
//	}
}
