package com.github.vincemann.springrapid.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.dto.*;
import com.github.vincemann.springrapid.auth.dto.user.FindForeignUserDto;
import com.github.vincemann.springrapid.auth.dto.user.FindOwnUserDto;
import com.github.vincemann.springrapid.auth.dto.user.FullUserDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.*;
import com.github.vincemann.springrapid.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;


@Slf4j
@Getter
public abstract class AbstractUserController<U extends AbstractUser<Id>, Id extends Serializable, S extends UserService<U,Id>>
			extends CrudController<U, Id,S> {


	private AuthProperties authProperties;


	private S unsecuredService;	// getService() to get secured

	private UserAuthTokenService authTokenService;
	private PasswordService passwordService;
	private SignupService signupService;
	private ContactInformationService contactInformationService;
	private VerificationService verificationService;
	private AuthorizationTokenService authorizationTokenService;



	//              CONTROLLER METHODS


	public ResponseEntity<FindOwnUserDto> signup(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {
		String body = readBody(request);
		SignupDto dto = getJsonMapper().readDto(body, SignupDto.class);
		getDtoValidationStrategy().validate(dto);
  		AbstractUser saved = signupService.signup(dto);
		FindOwnUserDto responseDto = getDtoMapper().mapToDto(saved, FindOwnUserDto.class);
		return okWithAuthToken(responseDto);
	}

	public ResponseEntity<Void> resendVerificationMail(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
		String contactInformation = readRequestParam(request, "ci");
		verificationService.resendVerificationMessage(contactInformation);
		return okNoContent();
	}


	/**
	 * Verifies current-user -> send code per contactInformation
	 */
	public ResponseEntity<Void> verifyUser(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, BadTokenException {
		String code = readRequestParam(request, "code");
		verificationService.verifyUser(code);
		return okWithAuthToken();
	}


	/**
	 * The forgot Password feature -> mail new password to contactInformation
	 */
	public ResponseEntity<Void> forgotPassword(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException {
		String contactInformation = readRequestParam(request, "ci");
		passwordService.forgotPassword(contactInformation);
		return okNoContent();
	}

	/**
	 * Resets password after it's forgotten
	 */
	public ResponseEntity<Void> resetPassword(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, BadTokenException, IOException {
		String body = readBody(request);
		ResetPasswordDto dto = getJsonMapper().readDto(body, ResetPasswordDto.class);
		getDtoValidationStrategy().validate(dto);
		passwordService.resetPassword(dto);
		return okWithAuthToken();
	}

	public String showResetPassword(@RequestParam("code") String code, Model model) {
		model.addAttribute("code", code);
		model.addAttribute("resetPasswordUrl", getResetPasswordUrl());
		model.addAttribute("resetPasswordDto", new ResetPasswordView());
		return "reset-password";
	}


	public ResponseEntity<Void> changePassword(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException {
		String body = readBody(request);
		ChangePasswordDto dto = getJsonMapper().readDto(body, ChangePasswordDto.class);
		getDtoValidationStrategy().validate(dto);
		passwordService.changePassword(dto);
		return okWithAuthToken();
	}


	public ResponseEntity<Void> requestChangeContactInformation(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException, IOException {
		String body = readBody(request);
		RequestContactInformationChangeDto dto = getJsonMapper().readDto(body, RequestContactInformationChangeDto.class);
		getDtoValidationStrategy().validate(dto);
		contactInformationService.requestContactInformationChange(dto);
		return okNoContent();
	}


	public ResponseEntity<Void> changeContactInformation(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadTokenException, AlreadyRegisteredException, BadEntityException {
		String code = readRequestParam(request, "code");
		contactInformationService.changeContactInformation(code);
		return okWithAuthToken();
	}

	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
	public ResponseEntity<String> createNewAuthToken(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, JsonProcessingException, EntityNotFoundException {
		Optional<String> contactInformation = readOptionalRequestParam(request, "ci");

		String token;
		if (contactInformation.isEmpty()){
			token = authTokenService.createNewAuthToken();
		}else {
			token = authTokenService.createNewAuthToken(contactInformation.get());
		}
		// result = {token:asfsdfjsdjfnd}
		return ok(getJsonMapper().writeDto(MapUtils.mapOf("token", token)));
	}

	public ResponseEntity<Void> testToken(HttpServletRequest request, HttpServletResponse response) {
		try {
			String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			authorizationTokenService.parseToken(authHeader);
			return okNoContent();
		} catch (BadTokenException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}


	public ResponseEntity<String> findByContactInformation(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException {
		String contactInformation = readRequestParam(request, "ci");
		Optional<U> byContactInformation = getService().findByContactInformation(contactInformation);
		VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+contactInformation+" not found");
		U user = byContactInformation.get();
		Object responseDto = getDtoMapper().mapToDto(user,
				createDtoClass(getFindByContactInformationUrl(), Direction.RESPONSE,request, user));
		return ok(getJsonMapper().writeDto(responseDto));
	}


	//             INIT


	@Override
	protected void configureDtoMappings(DtoMappingsBuilder builder) {

		builder.when(direction(Direction.RESPONSE)
						.and(roles(AuthRoles.ADMIN)))
				.thenReturn(FullUserDto.class);

		// anon can find id by email of diff user
		builder.when(endpoint(getFindByContactInformationUrl())
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

	// URLS

	private String loginUrl;
	private String signupUrl;

	private String resetPasswordUrl;
	private String resetPasswordViewUrl;
	private String findByContactInformationUrl;
	private String changeContactInformationUrl;
	private String changeContactInformationViewUrl;
	private String verifyUserUrl;
	private String resendVerificationContactInformationUrl;
	private String forgotPasswordUrl;
	private String changePasswordUrl;
	private String requestContactInformationChangeUrl;
	private String fetchNewAuthTokenUrl;
	private String testTokenUrl;


	@Override
	protected void initUrls() {
		super.initUrls();
		findByContactInformationUrl = getAuthProperties().getController().getFindByContactInformationUrl();

		loginUrl = getAuthProperties().getController().getLoginUrl();
		signupUrl = getAuthProperties().getController().getSignupUrl();

		resetPasswordUrl = getAuthProperties().getController().getResetPasswordUrl();
		resetPasswordViewUrl = getAuthProperties().getController().getResetPasswordViewUrl();
		changeContactInformationUrl = getAuthProperties().getController().getChangeContactInformationUrl();
		changeContactInformationViewUrl = getAuthProperties().getController().getChangeContactInformationViewUrl();
		verifyUserUrl = getAuthProperties().getController().getVerifyUserUrl();
		resendVerificationContactInformationUrl = getAuthProperties().getController().getResendVerifyContactInformationMsgUrl();
		forgotPasswordUrl = getAuthProperties().getController().getForgotPasswordUrl();
		changePasswordUrl = getAuthProperties().getController().getChangePasswordUrl();
		requestContactInformationChangeUrl = getAuthProperties().getController().getRequestContactInformationChangeUrl();
		fetchNewAuthTokenUrl = getAuthProperties().getController().getFetchNewAuthTokenUrl();
		testTokenUrl = getAuthProperties().getController().getTestTokenUrl();
	}


	//              REGISTER ENDPOINTS

	@Override
	protected void registerEndpoints() throws NoSuchMethodException {
		super.registerEndpoints();

		if (!getIgnoredEndPoints().contains(getFindByContactInformationUrl())){
			registerEndpoint(createFindByContactInformationRequestMappingInfo(),"fetchByContactInformation");
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
		if (!getIgnoredEndPoints().contains(getFindByContactInformationUrl())){
			registerEndpoint(createFindByContactInformationRequestMappingInfo(),"findByContactInformation");
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
		if (!getIgnoredEndPoints().contains(getTestTokenUrl())){
			registerEndpoint(createTestTokenRequestMappingInfo(),"testToken");
		}

	}


	protected RequestMappingInfo createSignupRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getSignupUrl())
				.methods(RequestMethod.POST)
				.consumes(MediaType.APPLICATION_JSON_VALUE)
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	protected RequestMappingInfo createTestTokenRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getTestTokenUrl())
				.methods(RequestMethod.GET)
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
				.produces(MediaType.APPLICATION_JSON_VALUE)
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
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.consumes(MediaType.APPLICATION_JSON_VALUE)
				.build();
	}


	protected RequestMappingInfo createFindByContactInformationRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getFindByContactInformationUrl())
				.methods(RequestMethod.GET)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(MediaType.APPLICATION_JSON_VALUE)
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
				.consumes(MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	protected RequestMappingInfo createChangeContactInformationRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getChangeContactInformationUrl())
				.methods(RequestMethod.POST)
//				.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(MediaType.APPLICATION_JSON_VALUE)
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
				.produces(MediaType.APPLICATION_JSON_VALUE)
				.build();
	}


	//				HELPERS


	protected ResponseEntity<Void> okWithAuthToken() throws EntityNotFoundException {
		HttpHeaders headers = new HttpHeaders();
		String token = authTokenService.createNewAuthToken();
		headers.add(HttpHeaders.AUTHORIZATION,token);
		return ResponseEntity.status(204).headers(headers).build();
	}

	protected <T> ResponseEntity<T> okWithAuthToken(T body) throws EntityNotFoundException {
		HttpHeaders headers = new HttpHeaders();
		String token = authTokenService.createNewAuthToken();
		headers.add(HttpHeaders.AUTHORIZATION,token);
		return ResponseEntity.status(200).headers(headers).body(body);
	}



	//              INJECT DEPENDENCIES

	@Secured
	@Lazy
	@Override
	@Autowired
	public void setCrudService(S crudService) {
		super.setCrudService(crudService);
	}
	@Lazy
	@Autowired
	public void setUnsecuredService(S Service) {
		this.unsecuredService = Service;
	}
	@Autowired public void setAuthProperties(AuthProperties authProperties) {
		this.authProperties = authProperties;
	}

	@Autowired public void setAuthTokenService(UserAuthTokenService authTokenService) {
		this.authTokenService = authTokenService;
	}

	@Autowired public void setPasswordService(PasswordService passwordService) {
		this.passwordService = passwordService;
	}
	@Autowired public void setSignupService(SignupService signupService) {
		this.signupService = signupService;
	}
	@Autowired public void setContactInformationService(ContactInformationService contactInformationService) {
		this.contactInformationService = contactInformationService;
	}
	@Autowired public void setVerificationService(VerificationService verificationService) {
		this.verificationService = verificationService;
	}
	@Autowired public void setAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
		this.authorizationTokenService = authorizationTokenService;
	}
}
