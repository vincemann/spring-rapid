package com.github.vincemann.springrapid.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordView;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.dto.user.FindForeignUserDto;
import com.github.vincemann.springrapid.auth.dto.user.FindOwnUserDto;
import com.github.vincemann.springrapid.auth.dto.user.FullUserDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.*;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.util.UserUtils;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
	private UserUtils userUtils;

	private PasswordService passwordService;
	private SignupService signupService;
	private ContactInformationService contactInformationService;
	private VerificationService verificationService;

	//              CONTROLLER METHODS


	@PostMapping(path = "/api/core/user/signup",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FindOwnUserDto> signup(@Valid @RequestBody SignupDto signupDto) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {
  		AbstractUser saved = signupService.signup(signupDto);
		FindOwnUserDto dto = getDtoMapper().mapToDto(saved, FindOwnUserDto.class);
		return okWithAuthToken(dto);
	}

	@GetMapping(path = "/api/core/user/resend-verification")
	public ResponseEntity<Void> resendVerificationMail(@RequestParam(value = "ci") String contactInformation) throws BadEntityException, EntityNotFoundException {
		verificationService.resendVerificationMessage(contactInformation);
		return okNoContent();
	}


	/**
	 * Verifies current-user -> send code per contactInformation
	 */
	@GetMapping(path = "/api/core/user/verify")
	public ResponseEntity<Void> verifyUser(@RequestParam(value = "code") String code) throws BadEntityException, EntityNotFoundException, BadTokenException {
		verificationService.verifyUser(code);
		return okWithAuthToken();
	}


	/**
	 * The forgot Password feature -> mail new password to contactInformation
	 */
	@GetMapping(path = "/api/core/user/forgot-password")
	public ResponseEntity<Void> forgotPassword(@RequestParam(value = "contactInformation") String contactInformation) throws EntityNotFoundException, BadEntityException {
		passwordService.forgotPassword(contactInformation);
		return okNoContent();
	}

	/**
	 * Resets password after it's forgotten
	 */
	@PostMapping(path = "/api/core/user/reset-password",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDto dto) throws  BadEntityException, EntityNotFoundException, BadTokenException {
		passwordService.resetPassword(dto);
		return okWithAuthToken();
	}

	@GetMapping("/api/core/user/show-reset-password")
	public String showResetPassword(@RequestParam("code") String code, Model model) {
		// Add the "code" attribute to the model, received from the request parameter
		model.addAttribute("code", code);
		// Add the "resetPasswordUrl" attribute to the model
		model.addAttribute("resetPasswordUrl", getResetPasswordUrl());
		// Add a new "resetPasswordDto" attribute to the model
		model.addAttribute("resetPasswordDto", new ResetPasswordView());
		// Return the view name
		return "reset-password";
	}


	@GetMapping("/api/core/user/change-password")
	public ResponseEntity<?> changePassword(@RequestParam String id, @Valid @RequestBody ChangePasswordDto dto) throws BadEntityException, EntityNotFoundException, IOException {
		passwordService.changePassword(dto);
		return okWithAuthToken();
	}


	@GetMapping("/api/core/user/request-change-ci")
	public ResponseEntity<Void> requestChangeContactInformation(@Valid @RequestBody RequestContactInformationChangeDto dto) throws EntityNotFoundException, BadEntityException {
		contactInformationService.requestContactInformationChange(dto);
		return okNoContent();
	}


	@GetMapping("/api/core/user/change-ci")
	public ResponseEntity<Void> changeContactInformation(@RequestParam String code) throws EntityNotFoundException, BadTokenException, AlreadyRegisteredException, BadEntityException {
		contactInformationService.changeContactInformation(code);
		return okWithAuthToken();
	}

	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
	@GetMapping("/api/core/user/new-token")
	public ResponseEntity<String> createNewAuthToken(@RequestParam Optional<String> contactInformation) throws BadEntityException, JsonProcessingException, EntityNotFoundException {

		String token;
		if (contactInformation.isEmpty()){
			token = userUtils.createNewAuthToken();
		}else {
			token = userUtils.createNewAuthToken(contactInformation.get());
		}
		// result = {token:asfsdfjsdjfnd}
		return ok(getJsonMapper().writeDto(MapUtils.mapOf("token", token)));
	}


	public ResponseEntity<String> findByContactInformation(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, BadEntityException, EntityNotFoundException {
		String contactInformation = readRequestParam(request, "contactInformation");
		log.debug("Fetching user by contactInformation: " + contactInformation);
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

	public String loginUrl;
	public String pingUrl;
	public String contextUrl;
	public String signupUrl;

	public String resetPasswordUrl;
	public String resetPasswordViewUrl;
	public String findByContactInformationUrl;
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
		findByContactInformationUrl = getAuthProperties().getController().getFindByContactInformationUrl();
	}


	//              REGISTER ENDPOINTS

	@Override
	protected void registerEndpoints() throws NoSuchMethodException {
		super.registerEndpoints();

		if (!getIgnoredEndPoints().contains(getFindByContactInformationUrl())){
			registerEndpoint(createFetchByContactInformationRequestMappingInfo(),"fetchByContactInformation");
		}
	}


	protected RequestMappingInfo createFetchByContactInformationRequestMappingInfo() {
		return RequestMappingInfo
				.paths(getFindByContactInformationUrl())
				.methods(RequestMethod.GET)
				//.consumes(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.produces(getMediaType())
				.build();
	}



	//				HELPERS


	protected ResponseEntity<Void> okWithAuthToken() throws EntityNotFoundException {
		HttpHeaders headers = new HttpHeaders();
		String token = userUtils.createNewAuthToken();
		headers.add(HttpHeaders.AUTHORIZATION,token);
		return ResponseEntity.status(204).headers(headers).build();
	}

	protected <T> ResponseEntity<T> okWithAuthToken(T body) throws EntityNotFoundException {
		HttpHeaders headers = new HttpHeaders();
		String token = userUtils.createNewAuthToken();
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



	@Autowired
	public void setAuthProperties(AuthProperties authProperties) {
		this.authProperties = authProperties;
	}

}
