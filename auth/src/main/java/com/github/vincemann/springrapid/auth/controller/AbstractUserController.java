package com.github.vincemann.springrapid.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.controller.dto.*;
import com.github.vincemann.springrapid.auth.controller.dto.user.FindForeignUserDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.controller.dto.user.FindOwnUserDto;
import com.github.vincemann.springrapid.auth.controller.dto.user.FullUserDto;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.HttpTokenService;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.acl.proxy.Secured;

import com.github.vincemann.springrapid.auth.util.UrlParamUtil;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.*;
import com.github.vincemann.springrapid.core.controller.id.IdFetchingException;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;


@Slf4j
@Getter
public abstract class AbstractUserController<U extends AbstractUser<Id>, Id extends Serializable>
			extends CrudController<U, Id> {



	private UserService<U, Id> userService;
	private UserService<U, Id> securedService;

	private HttpTokenService httpTokenService;
	private AuthProperties authProperties;


	public String loginUrl;
	public String pingUrl;
	public String contextUrl;
	public String signupUrl;

	public String resetPasswordUrl;
	public String resetPasswordViewUrl;
	public String fetchByContactInformationUrl;
	public String changeContactInformationUrl;
	public String changeContactInformationViewUrl;
	public String verifyUserUrl;
	public String resendVerificationContactInformationUrl;
	public String forgotPasswordUrl;
	public String changePasswordUrl;
	public String requestContactInformationChangeUrl;
	public String fetchNewAuthTokenUrl;

	@Override
	protected void initUrls() {
		super.initUrls();

		pingUrl = getAuthProperties().getController().getPingUrl();
		loginUrl = getAuthProperties().getController().getLoginUrl();
		contextUrl = getAuthProperties().getController().getContextUrl();
		signupUrl = getAuthProperties().getController().getSignupUrl();

		resetPasswordUrl = getAuthProperties().getController().getResetPasswordUrl();
		resetPasswordViewUrl = getAuthProperties().getController().getResetPasswordViewUrl();
		fetchByContactInformationUrl = getAuthProperties().getController().getFetchByContactInformationUrl();
		changeContactInformationUrl = getAuthProperties().getController().getChangeContactInformationUrl();
		changeContactInformationViewUrl = getAuthProperties().getController().getChangeContactInformationViewUrl();
		verifyUserUrl = getAuthProperties().getController().getVerifyUserUrl();
		resendVerificationContactInformationUrl = getAuthProperties().getController().getResendVerifyContactInformationMsgUrl();
		forgotPasswordUrl = getAuthProperties().getController().getForgotPasswordUrl();
		changePasswordUrl = getAuthProperties().getController().getChangePasswordUrl();
		requestContactInformationChangeUrl = getAuthProperties().getController().getRequestContactInformationChangeUrl();
		fetchNewAuthTokenUrl = getAuthProperties().getController().getFetchNewAuthTokenUrl();
	}


	//              CONTROLLER METHODS

	/**
	 * Returns public shared context properties needed at the client side,
	 */
	public ResponseEntity<String> context(HttpServletRequest request,HttpServletResponse response) throws JsonProcessingException {

		log.debug("Getting context ");
		Map<String, Object> context = getSecuredService().getContext();
		log.debug("Returning context: " + context);
		return ok(getJsonMapper().writeDto(context));
	}



	public ResponseEntity<String> signup(
			HttpServletRequest request,
			HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {
		

		String jsonDto = readBody(request);
		Class<?> dtoClass = createDtoClass(getSignupUrl(),Direction.REQUEST,request.getParameterMap(),null);
		Object signupDto = getJsonMapper().readDto(jsonDto, dtoClass);
		getDtoValidationStrategy().validate(signupDto);
		log.debug("Signing up: " + signupDto);
		U user = getDtoMapper().mapToEntity(signupDto, getEntityClass());
  		U saved = getSecuredService().signup(user);
		log.debug("Signed up: " + signupDto);

		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getSignupUrl(), Direction.RESPONSE,request.getParameterMap(), saved));
		return ok(getJsonMapper().writeDto(dto));
	}

	// todo add limit actions extension ect so nobody can spam contactInformations
	public ResponseEntity<?> resendVerificationMail(HttpServletRequest request,HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IdFetchingException {
		String contactInformation = readRequestParam(request, "contactInformation");
		log.debug("Resending verification mail for user with contactInformation " + contactInformation);
		Optional<U> byContactInformation = getUserService().findByContactInformation(contactInformation);
		VerifyEntity.isPresent(byContactInformation,"no user found with contactInformation: "+ contactInformation);
		getSecuredService().resendVerificationMessage(byContactInformation.get());
		return okNoContent();
	}


	/**
	 * Verifies current-user -> send code per contactInformation
	 */
	public ResponseEntity<String> verifyUser(
			HttpServletRequest request,
//			@RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException, BadTokenException, IdFetchingException {

		String code = readRequestParam(request, "code");

//		log.debug("Verifying user with id: " + id);
//		U user = fetchUser(id);
		U saved = getSecuredService().verifyUser(code);

		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(getVerifyUserUrl(), Direction.RESPONSE,request.getParameterMap(), saved));
		return ok(getJsonMapper().writeDto(dto));
	}


	/**
	 * The forgot Password feature -> mail new password to contactInformation
	 */
	public ResponseEntity<?> forgotPassword(HttpServletRequest request,HttpServletResponse response/*@RequestParam String contactInformation*/) throws EntityNotFoundException, BadEntityException {
		String contactInformation = readRequestParam(request, "contactInformation");
		log.debug("Received forgot password request for: " + contactInformation);
		getSecuredService().forgotPassword(contactInformation);
		return okNoContent();
	}

	/**
	 * Resets password after it's forgotten
	 */
	public ResponseEntity<String> resetPassword(
//			@RequestBody ResetPasswordForm form,
			HttpServletRequest request,HttpServletResponse response) throws IOException, BadEntityException, EntityNotFoundException, BadTokenException {

		String body = readBody(request);
		// todo terrible, fix this, check for equality in inline js in html file
		// and send propert json in body not url param encoded in body
		Map<String, String> queryParams = UrlParamUtil.splitQuery(body);
		String code = readRequestParam(request, "code");
		String password = queryParams.get("password");
		String matchPassword = queryParams.get("matchPassword");
		if (password==null || matchPassword == null){
			throw new BadEntityException("Insufficient Password data");
		}
		if (!password.equals(matchPassword)){
			throw new BadEntityException("Passwords do not match");
		}
		ResetPasswordDto resetPasswordDto = new ResetPasswordDto(password);
//		ResetPasswordDto resetPasswordDto = getJsonMapper().readDto(body, ResetPasswordDto.class);
		getDtoValidationStrategy().validate(resetPasswordDto);

		log.debug("Resetting password ... ");
		U saved = getSecuredService().resetPassword(password, code);
		appendFreshTokenOf(saved,response);
		Object dto = getDtoMapper().mapToDto(saved,
				createDtoClass(resetPasswordUrl, Direction.RESPONSE,request.getParameterMap(), saved));
		return ok(getJsonMapper().writeDto(dto));
	}

	// use gui here bc someone could issue many forgot password requests for foreign contactInformations with his new password set in forntend
	// and if the user just clicks the link, his pw would be reset to attackers pw
	// -> better to show view at backend, to make sure user can enter his password
	public String showResetPassword(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, BadEntityException, EntityNotFoundException, BadTokenException {
		String code = readRequestParam(request, "code");
		model.addAttribute("code",code);
		model.addAttribute("resetPasswordUrl",getResetPasswordUrl());
		model.addAttribute("resetPasswordDto",new ResetPasswordView());
		return "reset-password";
	}


	public ResponseEntity<String> fetchByContactInformation(HttpServletRequest request,HttpServletResponse response/*,@RequestParam String contactInformation*/) throws JsonProcessingException, BadEntityException, EntityNotFoundException {

		String contactInformation = readRequestParam(request, "contactInformation");
		log.debug("Fetching user by contactInformation: " + contactInformation);
		Optional<U> byContactInformation = getSecuredService().findByContactInformation(contactInformation);
		VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+contactInformation+" not found");
		U user = byContactInformation.get();
		Object responseDto = getDtoMapper().mapToDto(user,
				createDtoClass(getFetchByContactInformationUrl(), Direction.RESPONSE,request.getParameterMap(), user));
		return ok(getJsonMapper().writeDto(responseDto));
	}


	public ResponseEntity<?> changePassword(HttpServletRequest request,
//			@RequestBody ChangePasswordForm changePasswordForm,
			HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException {

		Id id = fetchId(request);
		String body = readBody(request);
		U user = fetchUser(id);
		Class<?> dtoClass = createDtoClass(getChangePasswordUrl(),Direction.REQUEST,request.getParameterMap(),user);
		ChangePasswordDto changePasswordDto = (ChangePasswordDto) getJsonMapper().readDto(body, dtoClass);
		getDtoValidationStrategy().validate(changePasswordDto);

		log.debug("Changing password of user with id: " + id);
		getSecuredService().changePassword(user, changePasswordDto.getOldPassword(),changePasswordDto.getNewPassword(),changePasswordDto.getRetypeNewPassword());
		appendFreshTokenOf(user,response);
		return okNoContent();
	}


	public ResponseEntity<?> requestContactInformationChange(HttpServletRequest request
								   /*@RequestBody RequestContactInformationChangeForm contactInformationChangeForm*/,HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IdFetchingException, IOException, AlreadyRegisteredException {

		Id id = fetchId(request);
		String body = readBody(request);
		U user = fetchUser(id);
		Class<?> dtoClass = createDtoClass(getRequestContactInformationChangeUrl(),Direction.REQUEST,request.getParameterMap(),user);
		RequestContactInformationChangeDto dto = (RequestContactInformationChangeDto) getJsonMapper().readDto(body, dtoClass);
		getDtoValidationStrategy().validate(dto);
		log.debug("Requesting contactInformation change for user: " + user);
		getSecuredService().requestContactInformationChange(user, dto.getNewContactInformation());
		return okNoContent();
	}


	public ResponseEntity<?> changeContactInformation(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, AlreadyRegisteredException {
		String code = readRequestParam(request, "code");
		U saved = getSecuredService().changeContactInformation(code);
		appendFreshTokenOf(saved,response);
		return okNoContent();
	}


	// does not need view page bc contactInformation is encapsulated in code and you can only
	// request contactInformation change if logged in (as opposed to forgotpassword)

//	public String showChangeContactInformation(
//			HttpServletRequest request,
//			HttpServletResponse response,
//			Model model) throws BadEntityException {
//		String code = readRequestParam(request, "code");
//		model.addAttribute("code",code);
//		model.addAttribute("changeContactInformationUrl",getChangeContactInformationUrl());
//		model.addAttribute("changeContactInformationDto",new ChangeContactInformationView());
//		return "change-contactInformation";
//	}

	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
	public ResponseEntity<String> createNewAuthToken(
			/*@RequestParam Optional<String> contactInformation,*/HttpServletRequest request,
			HttpServletResponse response) throws BadEntityException, JsonProcessingException, EntityNotFoundException {

		log.debug("Fetching a new auth token ... ");
		Optional<String> contactInformation = readOptionalRequestParam(request, "contactInformation");
		String token;
		if (contactInformation.isEmpty()){
			// for logged in user, if he is not logged in this will fail
			token = getSecuredService().createNewAuthToken();
		}else {
			// for foreign user
			token = getSecuredService().createNewAuthToken(contactInformation.get());
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
	protected void configureDtoMappings(DtoMappingsBuilder builder) {

		builder.when(direction(Direction.RESPONSE)
						.and(roles(AuthRoles.ADMIN)))
				.thenReturn(FullUserDto.class);


		builder.when(endpoint(getSignupUrl())
						.and(direction(Direction.REQUEST)))
				.thenReturn(SignupDto.class);

		builder.when(endpoint(getSignupUrl())
						.and(direction(Direction.RESPONSE)))
				.thenReturn(FindOwnUserDto.class);

		builder.when(endpoint(getRequestContactInformationChangeUrl())
						.and(direction(Direction.REQUEST)))
				.thenReturn(RequestContactInformationChangeDto.class);



		builder.when(endpoint(getChangePasswordUrl())
						.and(direction(Direction.REQUEST)))
				.thenReturn(ChangePasswordDto.class);

		builder.when(endpoint(getVerifyUserUrl())
						.and(direction(Direction.RESPONSE)))
				.thenReturn(FindOwnUserDto.class);

		builder.when(endpoint(getResetPasswordUrl())
						.and(direction(Direction.REQUEST)))
				.thenReturn(ResetPasswordDto.class);

		builder.when(endpoint(getResetPasswordUrl())
						.and(direction(Direction.RESPONSE)))
				.thenReturn(FindOwnUserDto.class);

		builder.when(endpoint(getFetchByContactInformationUrl())
						.and(direction(Direction.RESPONSE))
						.and(roles(AuthRoles.ANON)))
				.thenReturn(FindForeignUserDto.class);


		builder.when(direction(Direction.RESPONSE)
						.and(principal(Principal.OWN)))
				.thenReturn(FindOwnUserDto.class);

		builder.when(direction(Direction.RESPONSE)
						.and(principal(Principal.FOREIGN)))
				.thenReturn(FindForeignUserDto.class);
	}

//	/**
//	 * Preconfigured UserDtoMappingContextBuilder.
//	 * To extend configuration override {@link this#provideDtoMappingContext(UserDtoMappingContextBuilder)} and continue configuring.
//	 * To remove pre-configuration, override this method with empty impl and then override {@link this#provideDtoMappingContext(UserDtoMappingContextBuilder)}.
//	 */
//	@Override
//	protected void preconfigureDtoMappingContextBuilder(UserDtoMappingContextBuilder builder) {
//		super.preconfigureDtoMappings(builder);
//		builder
//
//				.withAllPrincipals()
//				.withAllRoles()
//				.forResponse(RapidFindForeignUserDto.class)
//
//				.withAllPrincipals()
//				.withAllRoles()
//				.forEndpoint(getSignupUrl(), Direction.REQUEST, SignupDto.class)
//				.forEndpoint(getSignupUrl(), Direction.RESPONSE, RapidFindOwnUserDto.class)
//
//				.withAllPrincipals()
//				.withAllRoles()
//				.forEndpoint(getRequestContactInformationChangeUrl(), Direction.REQUEST, RequestContactInformationChangeDto.class)
//
//				.withAllPrincipals()
//				.withAllRoles()
//				.forEndpoint(getChangePasswordUrl(), Direction.REQUEST, ChangePasswordDto.class)
//
//
//				.withAllPrincipals()
//				.withAllRoles()
//				.forEndpoint(getVerifyUserUrl(),Direction.RESPONSE, RapidFindOwnUserDto.class)
//
//				.withAllRoles()
//				.withPrincipal(DtoRequestInfo.Principal.OWN)
//				.forResponse(RapidFindOwnUserDto.class)
//
//				.withAllPrincipals()
//				.withRoles(Roles.ADMIN)
//				.forAll(RapidFullUserDto.class)
//
//				.withAllRoles()
//				.withAllPrincipals();
//	}



	//              REGISTER ENDPOINTS

	@Override
	protected void registerEndpoints() throws NoSuchMethodException {
		super.registerEndpoints();

		if (!getIgnoredEndPoints().contains(getContextUrl())){
			registerEndpoint(createContextRequestMappingInfo(),"context");
		}
		if (!getIgnoredEndPoints().contains(getSignupUrl())){
			registerEndpoint(createSignupRequestMappingInfo(),"signup");
		}
		if (!getIgnoredEndPoints().contains(getResendVerificationContactInformationUrl())){
			registerEndpoint(createResendVerificationContactInformationRequestMappingInfo(),"resendVerificationMail");
		}
		if (!getIgnoredEndPoints().contains(getVerifyUserUrl())){
			registerEndpoint(createVerifyUserRequestMappingInfo(),"verifyUser");
		}
		if (!getIgnoredEndPoints().contains(getForgotPasswordUrl())){
			registerEndpoint(createForgotPasswordRequestMappingInfo(),"forgotPassword");
		}
		if (!getIgnoredEndPoints().contains(getResetPasswordViewUrl())){
			registerViewEndpoint(createResetPasswordViewRequestMappingInfo(),"showResetPassword");
		}
		if (!getIgnoredEndPoints().contains(getResetPasswordUrl())){
			registerEndpoint(createResetPasswordRequestMappingInfo(),"resetPassword");
		}
		if (!getIgnoredEndPoints().contains(getFetchByContactInformationUrl())){
			registerEndpoint(createFetchByContactInformationRequestMappingInfo(),"fetchByContactInformation");
		}
		if (!getIgnoredEndPoints().contains(getChangePasswordUrl())){
			registerEndpoint(createChangePasswordRequestMappingInfo(),"changePassword");
		}
		if (!getIgnoredEndPoints().contains(getRequestContactInformationChangeUrl())){
			registerEndpoint(createRequestContactInformationChangeRequestMappingInfo(),"requestContactInformationChange");
		}
		if (!getIgnoredEndPoints().contains(getChangeContactInformationUrl())){
			registerEndpoint(createChangeContactInformationRequestMappingInfo(),"changeContactInformation");
		}

//		if (getEndpointInfo().isExposeChangeContactInformationView()){
//			registerViewEndpoint(createChangeContactInformationRequestViewMappingInfo(),"showChangeContactInformation");
//		}
		if (!getIgnoredEndPoints().contains(getFetchNewAuthTokenUrl())){
			registerEndpoint(createNewAuthTokenRequestMappingInfo(),"createNewAuthToken");
		}
		if (!getIgnoredEndPoints().contains(getPingUrl())){
			registerEndpoint(createPingRequestMappingInfo(),"ping");
		}
	}

	protected RequestMappingInfo createContextRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getContextUrl())
				.methods(RequestMethod.GET)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createSignupRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getSignupUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createResendVerificationContactInformationRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getResendVerificationContactInformationUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}

	protected RequestMappingInfo createVerifyUserRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getVerifyUserUrl())
				.methods(RequestMethod.GET)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createForgotPasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getForgotPasswordUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}

	protected RequestMappingInfo createResetPasswordViewRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getResetPasswordViewUrl())
				.methods(RequestMethod.GET)
//				.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}

	protected RequestMappingInfo createResetPasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getResetPasswordUrl())
				.methods(RequestMethod.POST)
				.produces(getMediaType())
//				.consumes(getMediaType())
				// todo terrible, fix this, check for equality in inline js in html file
				.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}


	protected RequestMappingInfo createFetchByContactInformationRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getFetchByContactInformationUrl())
				.methods(RequestMethod.GET)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}



	protected RequestMappingInfo createChangePasswordRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getChangePasswordUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();
	}



	protected RequestMappingInfo createRequestContactInformationChangeRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getRequestContactInformationChangeUrl())
				.methods(RequestMethod.POST)
				.consumes(getMediaType())
				.build();
	}

	protected RequestMappingInfo createChangeContactInformationRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getChangeContactInformationUrl())
				.methods(RequestMethod.POST)
//				.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}

//	protected RequestMappingInfo createChangeContactInformationRequestViewMappingInfo() {
//		return RequestMappingInfo
//				.paths(getChangeContactInformationViewUrl())
//				.methods(RequestMethod.GET)
//				.produces(getMediaType())
//				.build();
//	}

	protected RequestMappingInfo createNewAuthTokenRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getFetchNewAuthTokenUrl())
				.methods(RequestMethod.POST)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}

	protected RequestMappingInfo createPingRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getPingUrl())
				.methods(RequestMethod.GET)
				.build();
	}



	//				HELPERS



	/**
	 * Adds an Authorization header to the response for certain user
	 */
	protected void appendFreshTokenOf(U user, HttpServletResponse response) throws EntityNotFoundException {
		String token = getUserService().createNewAuthToken(user.getContactInformation());
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

	protected U fetchUser(Id userId) throws EntityNotFoundException {
		Optional<U> byId =  getUserService().findById(userId);
		VerifyEntity.isPresent(byId,"User with id: "+userId+" not found");
		return byId.get();
	}




	//              INJECT DEPENDENCIES


	@Autowired
	public void setHttpTokenService(HttpTokenService httpTokenService) {
		this.httpTokenService = httpTokenService;
	}

	// overwrite autowired annotation
	@Override
	public void setCrudService(CrudService<U, Id> crudService) {
		super.setCrudService(crudService);
	}

//	@Root
	@Lazy
	@Autowired
	public void setUserService(UserService<U, Id> Service) {
		this.userService = Service;
	}

	@Autowired
	@Secured
	@Lazy
	public void setSecuredService(UserService<U, Id> securedService) {
		this.securedService = securedService;
		this.setCrudService(securedService);
	}



	@Autowired
	public void setAuthProperties(AuthProperties authProperties) {
		this.authProperties = authProperties;
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
