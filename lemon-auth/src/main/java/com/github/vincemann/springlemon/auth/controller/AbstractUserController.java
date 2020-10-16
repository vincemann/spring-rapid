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
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
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
public abstract class AbstractUserController<U extends AbstractUser<ID>, ID extends Serializable, S extends UserService<U,ID>>
			extends GenericCrudController<U,ID, UserService<U,ID>,UserEndpointInfo,UserDtoMappingContextBuilder> {


	//              DEPENDENCIES


	private UserService<U,ID> unsecuredUserService;
	private HttpTokenService httpTokenService;
	private LemonProperties lemonProperties;


	//              CONTROLLER METHODS

	/**
	 * Returns public shared context properties needed at the client side,
	 */
//	@GetMapping("#{lemon.userController.contextUrl}")
//	@ResponseBody
	public ResponseEntity<String> context(HttpServletRequest request,HttpServletResponse response) throws JsonProcessingException {

		log.debug("Getting context ");
		Map<String, Object> context = getService().getContext();
		log.debug("Returning context: " + context);
		return ok(getJsonMapper().writeValueAsString(context));
	}



	/**
	 * Signs up a user, and
	 * returns current-user data and an Authorization token as a response header.
	 */
//	@PostMapping("${lemon.userController.signupUrl}")
//	@ResponseStatus(HttpStatus.CREATED)
//	@ResponseBody
	public ResponseEntity<String> signup(/*@RequestBody @JsonView(UserUtils.SignupInput.class) S signupForm,*/
			HttpServletRequest request,
			HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException {

		String signupForm = readBody(request);
		Object signupDto = getJsonMapper().readValue(signupForm,
				createDtoClass(getLemonProperties().getController().getSignupUrl(), Direction.REQUEST, null));
		getValidationStrategy().validateDto(signupDto);
		log.debug("Signing up: " + signupDto);
		U user = getDtoMapper().mapToEntity(signupDto, getEntityClass());
		U saved = getService().signup(user);
		log.debug("Signed up: " + signupForm);

		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getLemonProperties().getController().getSignupUrl(), Direction.RESPONSE, saved));
		return okCreated(getJsonMapper().writeValueAsString(dto));
	}

	/**
	 * Resends verification mail
	 */
//	@PostMapping("${lemon.userController.resendVerificationEmailUrl}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> resendVerificationMail(HttpServletRequest request,HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IdFetchingException {
		ID id = fetchId(request);
		log.debug("Resending verification mail for user with id " + id);
		U user = fetchUser(id);
		getService().resendVerificationMail(user);
		return okNoContent();
	}


	/**
	 * Verifies current-user -> send code per email
	 */
//	@PostMapping("${lemon.userController.verifyUserUrl}")
//	@ResponseBody
	public ResponseEntity<String> verifyUser(
			HttpServletRequest request,
//			@RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException, IdFetchingException {
		ID id = fetchId(request);
		String code = readRequestParam(request, "code");

		log.debug("Verifying user with id: " + id);
		U user = fetchUser(id);
		U saved = getService().verifyUser(user, code);

		appendFreshToken(response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getLemonProperties().getController().getVerifyUserUrl(), Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * The forgot Password feature -> mail new password to email
	 */
//	@PostMapping("${lemon.userController.forgotPasswordUrl}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> forgotPassword(HttpServletRequest request,HttpServletResponse response/*@RequestParam String email*/) throws EntityNotFoundException, BadEntityException {
		String email = readRequestParam(request, "email");
		log.debug("Received forgot password request for: " + email);
		getService().forgotPassword(email);
		return okNoContent();
	}


	/**
	 * Resets password after it's forgotten
	 */
//	@PostMapping("${lemon.userController.resetPasswordUrl}")
//	@ResponseBody
	public ResponseEntity<String> resetPassword(
//			@RequestBody ResetPasswordForm form,
			HttpServletRequest request,HttpServletResponse response) throws IOException, BadEntityException, EntityNotFoundException, BadTokenException {
		String body = readBody(request);
		ResetPasswordForm form = getJsonMapper().readValue(body, ResetPasswordForm.class);

		log.debug("Resetting password ... ");
		U saved = getService().resetPassword(form);
		appendFreshToken(response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getLemonProperties().getController().resetPasswordUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * Fetches a user by email
	 */
//	@PostMapping("${lemon.userController.fetchByEmailUrl}")
//	@ResponseBody
	public ResponseEntity<String> fetchByEmail(HttpServletRequest request,HttpServletResponse response/*,@RequestParam String email*/) throws JsonProcessingException, BadEntityException, EntityNotFoundException {
		String email = readRequestParam(request, "email");
		log.debug("Fetching user by email: " + email);
		Optional<U> byEmail = getService().findByEmail(email);
		VerifyEntity.isPresent(byEmail,"User with email: "+email+" not found");
		U user = byEmail.get();
		Object responseDto = getDtoMapper().mapToDto(user,
				createDtoClass(getLemonProperties().getController().getFetchByEmailUrl(), Direction.RESPONSE, user));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}

	
	/**
	 * Changes password
	 */
//	@PostMapping("${lemon.userController.changePasswordUrl}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> changePassword(HttpServletRequest request,
//			@RequestBody ChangePasswordForm changePasswordForm,
			HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IdFetchingException, IOException {
		ID id = fetchId(request);
		String body = readBody(request);
		ChangePasswordForm form = getJsonMapper().readValue(body, ChangePasswordForm.class);

		log.debug("Changing password of user with id: " + id);
		U user = fetchUser(id);
		getService().changePassword(user, form);
		appendFreshToken(response);
		return okNoContent();
	}


	/**
	 * Requests for changing email
	 */
//	@PostMapping("${lemon.userController.requestEmailChangeUrl}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> requestEmailChange(HttpServletRequest request
								   /*@RequestBody RequestEmailChangeForm emailChangeForm*/,HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IdFetchingException, IOException {
		ID id = fetchId(request);
		String body = readBody(request);
		RequestEmailChangeForm form = getJsonMapper().readValue(body, RequestEmailChangeForm.class);
		log.debug("Requesting email change for user with " + id);
		U user = fetchUser(id);
		getService().requestEmailChange(user, form);
		return okNoContent();
	}


	/**
	 * Changes the email
	 */
//	@PostMapping("${lemon.userController.changeEmailUrl}")
//	@ResponseBody
	public ResponseEntity<String> changeEmail(
			HttpServletRequest request,
//			@RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException, IdFetchingException {
		ID id = fetchId(request);
		log.debug("Changing email of user with id: " + id);
		String code = readRequestParam(request, "code");
		U user = fetchUser(id);
		U saved = getService().changeEmail(user, code);
		appendFreshToken(response);
		Object responseDto = getDtoMapper().mapToDto(saved,
				createDtoClass(getLemonProperties().getController().changeEmailUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}


	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
//	@PostMapping("${lemon.userController.newAuthTokenUrl}")
//	@ResponseBody
	public Map<String, String> createNewAuthToken(
			/*@RequestParam Optional<String> email,*/HttpServletRequest request,
			HttpServletResponse response) throws BadEntityException {

		log.debug("Fetching a new auth token ... ");
		Optional<String> email = readOptionalRequestParam(request, "email");
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
//	@GetMapping("${lemon.userController.pingUrl}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> ping(HttpServletRequest request,HttpServletResponse response) {
		log.debug("Received a ping");
		return okNoContent();
	}


	//             INIT


	@Override
	protected UserDtoMappingContextBuilder createDtoMappingContextBuilder() {
		return new UserDtoMappingContextBuilder(this);
	}

	/**
	 * Preconfigured UserDtoMappingContextBuilder.
	 * To extend configuration override {@link this#provideDtoMappingContext(UserDtoMappingContextBuilder)} and continue configuring.
	 * To remove pre-configuration, override this method with empty impl and then override {@link this#provideDtoMappingContext(UserDtoMappingContextBuilder)}.
	 */
	@Override
	protected void preconfigureDtoMappingContextBuilder(UserDtoMappingContextBuilder builder) {
		super.preconfigureDtoMappingContextBuilder(builder);
		builder

				.withAllPrincipals()
				.forAll(LemonUserDto.class)
				.forResponse(LemonReadUserDto.class)
				.forEndpoint(getLemonProperties().getController().getSignupUrl(), Direction.REQUEST, LemonSignupForm.class)

				.withPrincipal(DtoRequestInfo.Principal.FOREIGN)
				.forEndpoint(getLemonProperties().getController().getFetchByEmailUrl(),Direction.RESPONSE, LemonFindForeignDto.class)

				.withAllPrincipals()
				.withRoles(RapidRoles.ADMIN)
				.forEndpoint(getUpdateUrl(), LemonAdminUpdateUserDto.class);
	}

	@Override
	protected DtoMappingContext provideDtoMappingContext(UserDtoMappingContextBuilder builder) {
		return builder.build();
	}


	//              REGISTER ENDPOINTS

	@Override
	protected void registerEndpoints() throws NoSuchMethodException {
		super.registerEndpoints();

		if (getEndpointInfo().isExposeContext()){
			registerEndpoint(createContextRequestMappingInfo(),"context");
		}
		if (getEndpointInfo().isExposeSignup()){
			registerEndpoint(createSignupRequestMappingInfo(),"signup");
		}
		if (getEndpointInfo().isExposeResendVerificationMail()){
			registerEndpoint(createResendVerificationEmailRequestMappingInfo(),"resendVerificationMail");
		}
		if (getEndpointInfo().isExposeVerifyUser()){
			registerEndpoint(createVerifyUserRequestMappingInfo(),"verifyUser");
		}
		if (getEndpointInfo().isExposeForgotPassword()){
			registerEndpoint(createForgotPasswordRequestMappingInfo(),"forgotPassword");
		}
		if (getEndpointInfo().isExposeResetPassword()){
			registerEndpoint(createResetPasswordRequestMappingInfo(),"resetPassword");
		}
		if (getEndpointInfo().isExposeFetchByEmail()){
			registerEndpoint(createFetchByEmailRequestMappingInfo(),"fetchByEmail");
		}
		if (getEndpointInfo().isExposeChangePassword()){
			registerEndpoint(createChangePasswordRequestMappingInfo(),"changePassword");
		}
		if (getEndpointInfo().isExposeRequestEmailChange()){
			registerEndpoint(createRequestEmailChangeRequestMappingInfo(),"requestEmailChange");
		}
		if (getEndpointInfo().isExposeChangeEmail()){
			registerEndpoint(createChangeEmailRequestMappingInfo(),"changeEmail");
		}
		if (getEndpointInfo().isExposeNewAuthToken()){
			registerEndpoint(createNewAuthTokenRequestMappingInfo(),"createNewAuthToken");
		}
		if (getEndpointInfo().isExposePing()){
			registerEndpoint(createPingRequestMappingInfo(),"ping");
		}
	}

	protected RequestMappingInfo createContextRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().contextUrl)
				.methods(RequestMethod.GET)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createSignupRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getSignupUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createResendVerificationEmailRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getResendVerificationEmailUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.build();
	}

	protected RequestMappingInfo createVerifyUserRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getVerifyUserUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createForgotPasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getForgotPasswordUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.build();
	}

	protected RequestMappingInfo createResetPasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getResetPasswordUrl())
				.methods(RequestMethod.POST)
				.produces(getMediaType())
				.consumes(getMediaType())
				.build();
	}


	protected RequestMappingInfo createFetchByEmailRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getFetchByEmailUrl())
				.methods(RequestMethod.POST)
				.produces(getMediaType())
				.consumes(getMediaType())
				.build();
	}



	protected RequestMappingInfo createChangePasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getChangePasswordUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.build();
	}



	protected RequestMappingInfo createRequestEmailChangeRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getRequestEmailChangeUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.build();
	}

	protected RequestMappingInfo createChangeEmailRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getChangeEmailUrl())
				.methods(RequestMethod.POST)
				.produces(getMediaType())
				.consumes(getMediaType())
				.build();
	}

	protected RequestMappingInfo createNewAuthTokenRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getNewAuthTokenUrl())
				.methods(RequestMethod.POST)
				.produces(getMediaType())
				.consumes(getMediaType())
				.build();
	}

	protected RequestMappingInfo createPingRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getLemonProperties().getController().getPingUrl())
				.methods(RequestMethod.GET)
				.build();
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
	public void injectCrudService(UserService<U,ID> crudService) {
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

	public S getUnsecuredUserService() {
		return (S) unsecuredUserService;
	}

	@Override
	public S getService() {
		return (S) super.getService();
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
