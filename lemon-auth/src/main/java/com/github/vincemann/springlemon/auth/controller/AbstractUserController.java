package com.github.vincemann.springlemon.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.LemonSignupForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.ResetPasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonAdminUpdateUserDto;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonFindForeignDto;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonReadUserDto;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springlemon.auth.service.token.HttpTokenService;
import com.github.vincemann.springlemon.auth.util.LemonMapUtils;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.proxy.Unsecured;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.IdFetchingException;
import com.github.vincemann.springrapid.core.security.RapidRoles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.components.WebComponent;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Getter
@RequestMapping("${rapid.core.baseUrl}")
public abstract class AbstractUserController<U extends AbstractUser<ID>, ID extends Serializable, S extends UserService<U,ID>>
			extends GenericCrudController<U,ID, S,UserEndpointInfo,UserDtoMappingContextBuilder> {


	//              DEPENDENCIES


	private S unsecuredUserService;
	private HttpTokenService httpTokenService;
	private LemonProperties lemonProperties;


	//              CONTROLLER METHODS


	/**
	 * Returns public shared context properties needed at the client side,
	 */
	@GetMapping("${lemon.userController.contextUrl}")
	@ResponseBody
	public Map<String, Object> getContext() {

		log.debug("Getting context ");
		Map<String, Object> context = getService().getContext();
		log.debug("Returning context: " + context);
		return context;
	}

	/**
	 * Signs up a user, and
	 * returns current-user data and an Authorization token as a response header.
	 */
	@PostMapping("${lemon.userController.signupUrl}")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ResponseEntity<String> signup(/*@RequestBody @JsonView(UserUtils.SignupInput.class) S signupForm,*/
							   HttpServletRequest request,
							   HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException {

		String signupForm = readBody(request);
		Object signupDto = getJsonMapper().readValue(signupForm,
				createDtoClass(lemonProperties.userController.signupUrl, Direction.REQUEST, null));
		getValidationStrategy().validateDto(signupDto);
		log.debug("Signing up: " + signupDto);
		U user = getDtoMapper().mapToEntity(signupDto, getEntityClass());
		U saved = getService().signup(user);
		log.debug("Signed up: " + signupForm);

		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(lemonProperties.userController.signupUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * Resends verification mail
	 */
	@PostMapping("${lemon.userController.resendVerificationEmailUrl}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resendVerificationMail(HttpServletRequest request) throws BadEntityException, EntityNotFoundException, IdFetchingException {
		ID id = fetchId(request);
		log.debug("Resending verification mail for user with id " + id);
		U user = fetchUser(id);
		getService().resendVerificationMail(user);
	}


	/**
	 * Verifies current-user -> send code per email
	 */
	@PostMapping("${lemon.userController.verifyUserUrl}")
	@ResponseBody
	public ResponseEntity<String> verifyUser(
			HttpServletRequest request,
			@RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException, IdFetchingException {
		ID id = fetchId(request);

		log.debug("Verifying user with id: " + id);
		U user = fetchUser(id);
		U saved = getService().verifyUser(user, code);

		appendFreshToken(response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(lemonProperties.userController.verifyUserUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * The forgot Password feature -> mail new password to email
	 */
	@PostMapping("${lemon.userController.forgotPasswordUrl}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@RequestParam String email) throws EntityNotFoundException {
		log.debug("Received forgot password request for: " + email);
		getService().forgotPassword(email);
	}


	/**
	 * Resets password after it's forgotten
	 */
	@PostMapping("${lemon.userController.resetPasswordUrl}")
	@ResponseBody
	public ResponseEntity<String> resetPassword(
			@RequestBody ResetPasswordForm form,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException {

		log.debug("Resetting password ... ");
		U saved = getService().resetPassword(form);
		appendFreshToken(response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(lemonProperties.userController.resetPasswordUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * Fetches a user by email
	 */
	@PostMapping("${lemon.userController.fetchByEmailUrl}")
	@ResponseBody
	public ResponseEntity<String> fetchByEmail(@RequestParam String email) throws JsonProcessingException, BadEntityException, EntityNotFoundException {
		log.debug("Fetching user by email: " + email);
		Optional<U> byEmail = getService().findByEmail(email);
		VerifyEntity.isPresent(byEmail,"User with email: "+email+" not found");
		U user = byEmail.get();
		Object responseDto = getDtoMapper().mapToDto(user,
				createDtoClass(lemonProperties.userController.fetchByEmailUrl, Direction.RESPONSE, user));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}


	
	/**
	 * Changes password
	 */
	@PostMapping("${lemon.userController.changePasswordUrl}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(HttpServletRequest request,
			@RequestBody ChangePasswordForm changePasswordForm,
			HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IdFetchingException {
		ID id = fetchId(request);
		log.debug("Changing password of user with id: " + id);
		U user = fetchUser(id);
		getService().changePassword(user, changePasswordForm);
		appendFreshToken(response);
	}


	/**
	 * Requests for changing email
	 */
	@PostMapping("${lemon.userController.requestEmailChangeUrl}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void requestEmailChange(HttpServletRequest request,
								   @RequestBody RequestEmailChangeForm emailChangeForm) throws BadEntityException, EntityNotFoundException, IdFetchingException {
		ID id = fetchId(request);
		log.debug("Requesting email change for user with " + id);
		U user = fetchUser(id);
		getService().requestEmailChange(user, emailChangeForm);
	}


	/**
	 * Changes the email
	 */
	@PostMapping("${lemon.userController.changeEmailUrl}")
	@ResponseBody
	public ResponseEntity<String> changeEmail(
			HttpServletRequest request,
			@RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException, IdFetchingException {
		ID id = fetchId(request);
		log.debug("Changing email of user with id: " + id);
		U user = fetchUser(id);
		U saved = getService().changeEmail(user, code);
		appendFreshToken(response);
		Object responseDto = getDtoMapper().mapToDto(saved,
				createDtoClass(lemonProperties.userController.changeEmailUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}


	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
	@PostMapping("${lemon.userController.newAuthTokenUrl}")
	@ResponseBody
	public Map<String, String> fetchNewAuthToken(
			@RequestParam Optional<String> email) {

		log.debug("Fetching a new auth token ... ");
		String token;
		if (email.isEmpty()){
			// for logged in user
			token = getService().createNewAuthToken();
		}else {
			// for foreign user
			token = getService().createNewAuthToken(email.get());
		}
		// result = {token:asfsdfjsdjfnd}
		return LemonMapUtils.mapOf("token", token);
	}

	/**
	 * A simple function for pinging this server.
	 */
	@GetMapping("${lemon.userController.pingUrl}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ping() {
		log.debug("Received a ping");
	}


	//             INIT


	/**
	 * Preconfigured UserDtoMappingContextBuilder.
	 * To extend configuration override {@link this#provideDtoMappingContext(UserDtoMappingContextBuilder)} and continue configuring.
	 * To remove pre-configuration, override this method with empty impl and then override {@link this#provideDtoMappingContext(UserDtoMappingContextBuilder)}.
	 */
	@Override
	protected void preConfigureDtoMappingContextBuilder(UserDtoMappingContextBuilder builder) {
		super.preConfigureDtoMappingContextBuilder(builder);
		builder.withAllPrincipals()
				.forAll(LemonUserDto.class)
				.forResponse(LemonReadUserDto.class)
				.forEndpoint(lemonProperties.userController.signupUrl, Direction.REQUEST, LemonSignupForm.class)

				.withPrincipal(DtoRequestInfo.Principal.FOREIGN)
				.forEndpoint(lemonProperties.userController.fetchByEmailUrl,Direction.RESPONSE, LemonFindForeignDto.class)

				.withAllPrincipals()
				.withRoles(RapidRoles.ADMIN)
				.forEndpoint(getCoreProperties().controller.endpoints.update, LemonAdminUpdateUserDto.class);
	}

	@Override
	protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
		return builder.build();
	}


	//				HELPERS



	/**
	 * Adds an Authorization header to the response for certain user
	 */
	protected void appendFreshTokenOf(U user, HttpServletResponse response) {
		String token = getService().createNewAuthToken(user.getEmail());
		httpTokenService.appendToken(token,response);
//		response.addHeader(LecUtils.TOKEN_RESPONSE_HEADER_NAME, JwtService.TOKEN_PREFIX + token);
	}

	/**
	 * Adds an Authorization header to the response for logged in user
	 */
	protected void appendFreshToken(HttpServletResponse response){
		String token = getService().createNewAuthToken();
		httpTokenService.appendToken(token,response);
	}

	protected U fetchUser(ID userId) throws BadEntityException, EntityNotFoundException {
		Optional<U> byId =  unsecuredUserService.findById(userId);
		VerifyEntity.isPresent(byId,"User with id: "+userId+" not found");
		return byId.get();
	}


	//              INJECT DEPENDENCIES


	@Autowired
	public void injectHttpTokenService(HttpTokenService httpTokenService) {
		this.httpTokenService = httpTokenService;
	}
	@Autowired
	@Secured
	@Override
	public void injectCrudService(S crudService) {
		super.injectCrudService(crudService);
	}
	@Autowired
	@Unsecured
	public void injectUnsecuredService(S unsecuredService) {
		this.unsecuredUserService = unsecuredService;
	}
	@Autowired
	public void injectLemonProperties(LemonProperties lemonProperties) {
		this.lemonProperties = lemonProperties;
	}

	//	@Override
//	public void afterUpdate(Object dto, U updated, HttpServletRequest httpServletRequest, HttpServletResponse response) {
//		super.afterUpdate(dto, updated, httpServletRequest, response);
//		appendFreshToken(response);
//	}


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
