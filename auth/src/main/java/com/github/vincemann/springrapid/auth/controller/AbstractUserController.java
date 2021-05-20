package com.github.vincemann.springrapid.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.auth.domain.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.domain.dto.RequestEmailChangeDto;
import com.github.vincemann.springrapid.auth.domain.dto.SignupDto;
import com.github.vincemann.springrapid.auth.domain.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.domain.dto.user.RapidFindForeignUserDto;
import com.github.vincemann.springrapid.auth.domain.dto.user.RapidFindOwnUserDto;
import com.github.vincemann.springrapid.auth.domain.dto.user.RapidFullUserDto;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.HttpTokenService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.acl.proxy.Secured;

import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.IdFetchingException;
import com.github.vincemann.springrapid.core.security.Roles;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
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


	// dont change to S, autoconfig needs raw userService version, getUserService methods will cast to S
	private UserService<U,ID> userService;
	private HttpTokenService httpTokenService;
	private AuthProperties authProperties;


	//              CONTROLLER METHODS

	/**
	 * Returns public shared context properties needed at the client side,
	 */
//	@GetMapping("#{lemon.userController.contextUrl}")
//	@ResponseBody
	public ResponseEntity<String> context(HttpServletRequest request,HttpServletResponse response) throws JsonProcessingException {

		log.debug("Getting context ");
		Map<String, Object> context = getSecuredUserService().getContext();
		log.debug("Returning context: " + context);
		return ok(getJsonMapper().writeDto(context));
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
			HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {

		String jsonDto = readBody(request);
		Object signupDto = getJsonMapper().readDto(jsonDto,
				createDtoClass(getAuthProperties().getController().getSignupUrl(), Direction.REQUEST, null));
		getDtoValidationStrategy().validate(signupDto);
		log.debug("Signing up: " + signupDto);
		U user = getDtoMapper().mapToEntity(signupDto, getEntityClass());
  		U saved = getSecuredUserService().signup(user);
		log.debug("Signed up: " + signupDto);

		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getAuthProperties().getController().getSignupUrl(), Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeDto(dto));
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
		getSecuredUserService().resendVerificationMail(user);
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

//		log.debug("Verifying user with id: " + id);
//		U user = fetchUser(id);
		U saved = getSecuredUserService().verifyUser(code);

		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getAuthProperties().getController().getVerifyUserUrl(), Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeDto(dto));
	}


	/**
	 * The forgot Password feature -> mail new password to email
	 */
//	@PostMapping("${lemon.userController.forgotPasswordUrl}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> forgotPassword(HttpServletRequest request,HttpServletResponse response/*@RequestParam String email*/) throws EntityNotFoundException, BadEntityException {
		String email = readRequestParam(request, "email");
		log.debug("Received forgot password request for: " + email);
		getSecuredUserService().forgotPassword(email);
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
		String code = readRequestParam(request, "code");
		ResetPasswordDto resetPasswordDto = getJsonMapper().readDto(body, ResetPasswordDto.class);
		getDtoValidationStrategy().validate(resetPasswordDto);

		log.debug("Resetting password ... ");
		U saved = getSecuredUserService().resetPassword(resetPasswordDto, code);
		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getAuthProperties().getController().resetPasswordUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeDto(dto));
	}


	/**
	 * Fetches a user by email
	 */
//	@PostMapping("${lemon.userController.fetchByEmailUrl}")
//	@ResponseBody
	public ResponseEntity<String> fetchByEmail(HttpServletRequest request,HttpServletResponse response/*,@RequestParam String email*/) throws JsonProcessingException, BadEntityException, EntityNotFoundException {
		String email = readRequestParam(request, "email");
		log.debug("Fetching user by email: " + email);
		Optional<U> byEmail = getSecuredUserService().findByEmail(email);
		VerifyEntity.isPresent(byEmail,"User with email: "+email+" not found");
		U user = byEmail.get();
		Object responseDto = getDtoMapper().mapToDto(user,
				createDtoClass(getAuthProperties().getController().getFetchByEmailUrl(), Direction.RESPONSE, user));
		return ok(getJsonMapper().writeDto(responseDto));
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
		ChangePasswordDto form = getJsonMapper().readDto(body, ChangePasswordDto.class);
		getDtoValidationStrategy().validate(form);

		log.debug("Changing password of user with id: " + id);
		U user = fetchUser(id);
		getSecuredUserService().changePassword(user, form);
		appendFreshTokenOf(user,response);
		return okNoContent();
	}


	/**
	 * Requests for changing email
	 */
//	@PostMapping("${lemon.userController.requestEmailChangeUrl}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> requestEmailChange(HttpServletRequest request
								   /*@RequestBody RequestEmailChangeForm emailChangeForm*/,HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IdFetchingException, IOException, AlreadyRegisteredException {
		ID id = fetchId(request);
		String body = readBody(request);
		RequestEmailChangeDto form = getJsonMapper().readDto(body, RequestEmailChangeDto.class);
		getDtoValidationStrategy().validate(form);
		log.debug("Requesting email change for user with " + id);
		U user = fetchUser(id);
		getSecuredUserService().requestEmailChange(user, form);
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
//		ID id = fetchId(request);
//		log.debug("Changing email of user with id: " + id);
		String code = readRequestParam(request, "code");
//		U user = fetchUser(id);
		U saved = getSecuredUserService().changeEmail(code);
		appendFreshTokenOf(saved,response);
		Object responseDto = getDtoMapper().mapToDto(saved,
				createDtoClass(getAuthProperties().getController().changeEmailUrl, Direction.RESPONSE, saved));
		return ok(getJsonMapper().writeDto(responseDto));
	}


	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
//	@PostMapping("${lemon.userController.newAuthTokenUrl}")
//	@ResponseBody
	public ResponseEntity<String> createNewAuthToken(
			/*@RequestParam Optional<String> email,*/HttpServletRequest request,
			HttpServletResponse response) throws BadEntityException, JsonProcessingException, EntityNotFoundException {

		log.debug("Fetching a new auth token ... ");
		Optional<String> email = readOptionalRequestParam(request, "email");
		String token;
		if (email.isEmpty()){
			// for logged in user, if he is not logged in this will fail
			token = getSecuredUserService().createNewAuthToken();
		}else {
			// for foreign user
			token = getSecuredUserService().createNewAuthToken(email.get());
		}
		// result = {token:asfsdfjsdjfnd}
		return ok(
				getJsonMapper().writeDto(
						MapUtils.mapOf("token", token)));
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
				.withAllRoles()
				.forResponse(RapidFindForeignUserDto.class)

				.withAllPrincipals()
				.withAllRoles()
				.forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.REQUEST, SignupDto.class)
				.forEndpoint(getAuthProperties().getController().getSignupUrl(), Direction.RESPONSE, RapidFindOwnUserDto.class)


				.withAllPrincipals()
				.withAllRoles()
				.forEndpoint(getAuthProperties().getController().getVerifyUserUrl(),Direction.RESPONSE, RapidFindOwnUserDto.class)

				.withAllRoles()
				.withPrincipal(DtoRequestInfo.Principal.OWN)
				.forResponse(RapidFindOwnUserDto.class)

				.withAllPrincipals()
				.withRoles(Roles.ADMIN)
				.forAll(RapidFullUserDto.class)

				.withAllRoles()
				.withAllPrincipals();
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
				.paths(getAuthProperties().getController().getContextUrl())
				.methods(RequestMethod.GET)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createSignupRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getSignupUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createResendVerificationEmailRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getResendVerificationEmailUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}

	protected RequestMappingInfo createVerifyUserRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getVerifyUserUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createForgotPasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getForgotPasswordUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}

	protected RequestMappingInfo createResetPasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getResetPasswordUrl())
				.methods(RequestMethod.POST)
				.produces(getMediaType())
				.consumes(getMediaType())
				.build();
	}


	protected RequestMappingInfo createFetchByEmailRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getFetchByEmailUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}



	protected RequestMappingInfo createChangePasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getChangePasswordUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}



	protected RequestMappingInfo createRequestEmailChangeRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getRequestEmailChangeUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.build();
	}

	protected RequestMappingInfo createChangeEmailRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getChangeEmailUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createNewAuthTokenRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getNewAuthTokenUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createPingRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getAuthProperties().getController().getPingUrl())
				.methods(RequestMethod.GET)
				.build();
	}



	//				HELPERS



	/**
	 * Adds an Authorization header to the response for certain user
	 */
	protected void appendFreshTokenOf(U user, HttpServletResponse response) throws EntityNotFoundException {
		String token = getUserService().createNewAuthToken(user.getEmail());
		httpTokenService.appendToken(token,response);
//		response.addHeader(LecUtils.TOKEN_RESPONSE_HEADER_NAME, JwtService.TOKEN_PREFIX + token);
	}


	// nobody should be logged in anymore at controller level!
//	/**
//	 * Adds an Authorization header to the response for logged in user
//	 */
//	protected void appendFreshToken(HttpServletResponse response) throws EntityNotFoundException {
//		String token = getUserService().createNewAuthToken();
//		httpTokenService.appendToken(token,response);
//	}

	protected U fetchUser(ID userId) throws BadEntityException, EntityNotFoundException {
		Optional<U> byId =  getUserService().findById(userId);
		VerifyEntity.isPresent(byId,"User with id: "+userId+" not found");
		return byId.get();
	}




	//              INJECT DEPENDENCIES


	@Autowired
	public void injectHttpTokenService(HttpTokenService httpTokenService) {
		this.httpTokenService = httpTokenService;
	}

	@Lazy
	@Autowired
	@Secured
	@Override
	public void injectCrudService(UserService<U,ID> crudService) {
		super.injectCrudService(crudService);
	}

//	@Root
	@Lazy
	@Autowired
	public void injectUserService(UserService<U,ID> Service) {
		this.userService = Service;
	}

	protected S getSecuredUserService(){
		return (S) getService();
	}

	protected S getUserService(){
		return (S) this.userService;
	}


	@Autowired
	public void injectAuthProperties(AuthProperties authProperties) {
		this.authProperties = authProperties;
	}


	@Autowired
	@Qualifier("userEndpointInfo")
	@Override
	public void injectEndpointInfo(UserEndpointInfo endpointInfo) {
		super.injectEndpointInfo(endpointInfo);
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
