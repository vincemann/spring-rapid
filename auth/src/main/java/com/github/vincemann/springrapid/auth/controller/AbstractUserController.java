package com.github.vincemann.springrapid.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.dto.ChangePasswordDto;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordDto;
import com.github.vincemann.springrapid.auth.dto.ResetPasswordView;
import com.github.vincemann.springrapid.auth.jwt.AuthorizationTokenService;
import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.auth.service.*;
import com.github.vincemann.springrapid.auth.util.MapUtils;
import com.github.vincemann.springrapid.auth.val.InsufficientPasswordStrengthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.pattern.PathPatternParser;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.io.IOException;
import java.util.Optional;


public abstract class AbstractUserController<S extends UserService<?,?>>
        extends AbstractController implements InitializingBean {

    private final Log log = LogFactory.getLog(getClass());


    private AuthProperties authProperties;
    private UserAuthTokenService authTokenService;
    private PasswordService passwordService;
    private ContactInformationService contactInformationService;
    private VerificationService verificationService;
    private AuthorizationTokenService authorizationTokenService;

    private S userService;
    private ThymeleafViewResolver viewResolver;

    //              CONTROLLER METHODS

    public ResponseEntity<Void> resendVerificationMessage(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
        String contactInformation = readRequestParam(request, "ci");
        log.debug(LogMessage.format("received resend verification msg request for: %s", contactInformation));
        verificationService.resendVerificationMessage(contactInformation);
        return okNoContent();
    }


    /**
     * Verifies current-user -> send code per contactInformation
     */
    public ResponseEntity<Void> verifyUser(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, BadTokenException {
        String code = readRequestParam(request, "code");
        log.debug(LogMessage.format("received verify user request with code: %s", code));
        AbstractUser updated = verificationService.verifyUser(code);
        return okWithToken(updated.getContactInformation());
    }


    /**
     * The forgot Password feature -> mail new password to contactInformation
     */
    public ResponseEntity<Void> forgotPassword(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException {
        String contactInformation = readRequestParam(request, "ci");
        log.debug(LogMessage.format("received forgot password request for: %s", contactInformation));
        try {
            passwordService.forgotPassword(contactInformation);
        }catch (EntityNotFoundException e){
            // dont allow to see if user exists
            log.debug("entity not found, but still sending 204 to avoid scanning if user is registered",e);
            return okNoContent();
        }

        return okNoContent();
    }

    /**
     * Resets password after it's forgotten
     */
    public ResponseEntity<Void> resetPassword(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, BadTokenException, IOException, InsufficientPasswordStrengthException {
        log.debug("received reset password request");
        String body = readBody(request);
        ResetPasswordDto dto = getObjectMapper().readValue(body, ResetPasswordDto.class);
        validateDto(dto);
        AbstractUser updated = passwordService.resetPassword(dto);
        return okWithToken(updated.getContactInformation());
    }

    public ResponseEntity<?> showResetPassword(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        String code = readRequestParam(request, "code");
        log.debug(LogMessage.format("received show reset password request with code: %s", code));
        model.addAttribute("resetPasswordUrl", getResetPasswordUrl());
        model.addAttribute("resetPasswordDto", new ResetPasswordView());
        // Manually resolving the view
        View view = viewResolver.resolveViewName("reset-password", LocaleContextHolder.getLocale());
        if (view != null) {
            view.render(model.asMap(), request, response);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }



    public ResponseEntity<Void> changePassword(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException, IOException, InsufficientPasswordStrengthException {
        log.debug("received change password request");
        String body = readBody(request);
        ChangePasswordDto dto = getObjectMapper().readValue(body, ChangePasswordDto.class);
        validateDto(dto);
        AbstractUser updated = passwordService.changePassword(dto);
        return okWithToken(updated.getContactInformation());
    }


    public ResponseEntity<Void> requestContactInformationChange(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException, IOException {
        log.debug("received request contact information change request");
        String body = readBody(request);
        RequestContactInformationChangeDto dto = getObjectMapper().readValue(body, RequestContactInformationChangeDto.class);
        validateDto(dto);
        contactInformationService.requestContactInformationChange(dto);
        return okNoContent();
    }

    public ResponseEntity<Void> blockUser(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, EntityNotFoundException {
        String contactInformation = readRequestParam(request, "ci");
        log.debug(LogMessage.format("received block user request for: %s", contactInformation));
        userService.blockUser(contactInformation);
        return okNoContent();
    }

    public ResponseEntity<Void> changeContactInformation(HttpServletRequest request, HttpServletResponse response) throws EntityNotFoundException, BadTokenException, AlreadyRegisteredException, BadEntityException {
        String code = readRequestParam(request, "code");
        log.debug(LogMessage.format("received change contact information request with code: %s", code));
        AbstractUser updated = contactInformationService.changeContactInformation(code);
        return okWithToken(updated.getContactInformation());
    }

    /**
     * Fetch a new token - for session sliding, switch user etc.
     */
    public ResponseEntity<String> createNewAuthToken(HttpServletRequest request, HttpServletResponse response) throws BadEntityException, JsonProcessingException, EntityNotFoundException {
        Optional<String> contactInformation = readOptionalRequestParam(request, "ci");
        log.debug(LogMessage.format("received create new auth token request for: %s", contactInformation));

        String token;
        if (contactInformation.isEmpty()) {
            token = authTokenService.createNewAuthToken();
        } else {
            token = authTokenService.createNewAuthToken(contactInformation.get());
        }
        // result = {token:asfsdfjsdjfnd}
        return ok(getObjectMapper().writeValueAsString(MapUtils.mapOf("token", token)));
    }

    public ResponseEntity<Void> testToken(HttpServletRequest request, HttpServletResponse response) {
        log.debug("received test token request");
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            authorizationTokenService.parseToken(authHeader);
            return okNoContent();
        } catch (BadTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    // URLS

    private String loginUrl;
    private String resetPasswordUrl;
    private String resetPasswordViewUrl;
    private String changeContactInformationUrl;
    private String changeContactInformationViewUrl;
    private String verifyUserUrl;
    private String resendVerificationMessageUrl;
    private String forgotPasswordUrl;
    private String changePasswordUrl;
    private String requestContactInformationChangeUrl;
    private String fetchNewAuthTokenUrl;
    private String testTokenUrl;
    private String blockUserUrl;


    @Override
    public void afterPropertiesSet() throws Exception {
        initUrls();
    }

    protected void initUrls() {
        loginUrl = getAuthProperties().getLoginUrl();

        resetPasswordUrl = getAuthProperties().getController().getResetPasswordUrl();
        resetPasswordViewUrl = getAuthProperties().getController().getResetPasswordViewUrl();
        changeContactInformationUrl = getAuthProperties().getController().getChangeContactInformationUrl();
        changeContactInformationViewUrl = getAuthProperties().getController().getChangeContactInformationViewUrl();
        verifyUserUrl = getAuthProperties().getController().getVerifyUserUrl();
        resendVerificationMessageUrl = getAuthProperties().getController().getResendVerifyContactInformationMsgUrl();
        forgotPasswordUrl = getAuthProperties().getController().getForgotPasswordUrl();
        changePasswordUrl = getAuthProperties().getController().getChangePasswordUrl();
        requestContactInformationChangeUrl = getAuthProperties().getController().getRequestContactInformationChangeUrl();
        fetchNewAuthTokenUrl = getAuthProperties().getController().getFetchNewAuthTokenUrl();
        testTokenUrl = getAuthProperties().getController().getTestTokenUrl();
        blockUserUrl = getAuthProperties().getController().getBlockUserUrl();
    }


    //              REGISTER ENDPOINTS

    @Override
    protected void registerEndpoints() throws NoSuchMethodException {
        if (!getIgnoredEndPoints().contains(getResendVerificationMessageUrl())) {
            registerEndpoint(createResendVerificationContactInformationRequestMappingInfo(), "resendVerificationMessage");
        }
        if (!getIgnoredEndPoints().contains(getVerifyUserUrl())) {
            registerEndpoint(createVerifyUserRequestMappingInfo(), "verifyUser");
        }
        if (!getIgnoredEndPoints().contains(getForgotPasswordUrl())) {
            registerEndpoint(createForgotPasswordRequestMappingInfo(), "forgotPassword");
        }
        if (!getIgnoredEndPoints().contains(getResetPasswordViewUrl())) {
            registerViewEndpoint(createResetPasswordViewRequestMappingInfo(), "showResetPassword");
        }
        if (!getIgnoredEndPoints().contains(getResetPasswordUrl())) {
            registerEndpoint(createResetPasswordRequestMappingInfo(), "resetPassword");
        }
        if (!getIgnoredEndPoints().contains(getChangePasswordUrl())) {
            registerEndpoint(createChangePasswordRequestMappingInfo(), "changePassword");
        }
        if (!getIgnoredEndPoints().contains(getRequestContactInformationChangeUrl())) {
            registerEndpoint(createRequestContactInformationChangeRequestMappingInfo(), "requestContactInformationChange");
        }
        if (!getIgnoredEndPoints().contains(getChangeContactInformationUrl())) {
            registerEndpoint(createChangeContactInformationRequestMappingInfo(), "changeContactInformation");
        }

//		if (getEndpointInfo().isExposeChangeContactInformationView()){
//			registerViewEndpoint(createChangeContactInformationRequestViewMappingInfo(),"showChangeContactInformation");
//		}
        if (!getIgnoredEndPoints().contains(getFetchNewAuthTokenUrl())) {
            registerEndpoint(createNewAuthTokenRequestMappingInfo(), "createNewAuthToken");
        }
        if (!getIgnoredEndPoints().contains(getTestTokenUrl())) {
            registerEndpoint(createTestTokenRequestMappingInfo(), "testToken");
        }

        if (!getIgnoredEndPoints().contains(getBlockUserUrl())) {
            registerEndpoint(createBlockUserRequestMappingInfo(), "blockUser");
        }

    }


    protected RequestMappingInfo createTestTokenRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getTestTokenUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.GET)
                .build();
    }


    protected RequestMappingInfo createResendVerificationContactInformationRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getResendVerificationMessageUrl())
                        .options(withPathPatternParser())
                .methods(RequestMethod.POST)
                .build();
    }

    protected RequestMappingInfo createVerifyUserRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getVerifyUserUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.GET)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    protected RequestMappingInfo createForgotPasswordRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getForgotPasswordUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.POST)
                .build();
    }

    protected RequestMappingInfo createResetPasswordViewRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getResetPasswordViewUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.GET)
                .build();
    }

    protected RequestMappingInfo createResetPasswordRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getResetPasswordUrl())
                .methods(RequestMethod.POST)
                .options(withPathPatternParser())
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }


    protected RequestMappingInfo createChangePasswordRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getChangePasswordUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.POST)
                .build();
    }


    protected RequestMappingInfo createRequestContactInformationChangeRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getRequestContactInformationChangeUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.POST)
                .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    protected RequestMappingInfo createChangeContactInformationRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getChangeContactInformationUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.POST)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

//	protected RequestMappingInfo createChangeContactInformationRequestViewMappingInfo() {
//		return RequestMappingInfo
//				.paths(getChangeContactInformationViewUrl())
                //.options(withPathPatternParser())
//				.methods(RequestMethod.GET)
//				.produces(getMediaType())
//				.build();
//	}

    protected RequestMappingInfo createNewAuthTokenRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getFetchNewAuthTokenUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.POST)
                .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .build();
    }

    private RequestMappingInfo createBlockUserRequestMappingInfo() {
        return RequestMappingInfo
                .paths(getBlockUserUrl())
                .options(withPathPatternParser())
                .methods(RequestMethod.GET)
                .build();
    }

    private RequestMappingInfo.BuilderConfiguration withPathPatternParser() {
        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());
        return options;
    }

    //				HELPERS


    protected ResponseEntity<Void> okWithToken(String contactInformation) throws EntityNotFoundException, BadEntityException {
        HttpHeaders headers = new HttpHeaders();
        String token = authTokenService.createNewAuthToken(contactInformation);
        headers.add(HttpHeaders.AUTHORIZATION, token);
        return ResponseEntity.status(204).headers(headers).build();
    }

    protected <T> ResponseEntity<T> okWithToken(T body, String contactInformation) throws EntityNotFoundException, BadEntityException {
        HttpHeaders headers = new HttpHeaders();
        String token = authTokenService.createNewAuthToken(contactInformation);
        headers.add(HttpHeaders.AUTHORIZATION, token);
        return ResponseEntity.status(200).headers(headers).body(body);
    }

    public AuthProperties getAuthProperties() {
        return authProperties;
    }

    public UserAuthTokenService getAuthTokenService() {
        return authTokenService;
    }

    public PasswordService getPasswordService() {
        return passwordService;
    }


    public ContactInformationService getContactInformationService() {
        return contactInformationService;
    }

    public VerificationService getVerificationService() {
        return verificationService;
    }

    public AuthorizationTokenService getAuthorizationTokenService() {
        return authorizationTokenService;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getResetPasswordUrl() {
        return resetPasswordUrl;
    }

    public String getResetPasswordViewUrl() {
        return resetPasswordViewUrl;
    }


    public String getChangeContactInformationUrl() {
        return changeContactInformationUrl;
    }

    public String getChangeContactInformationViewUrl() {
        return changeContactInformationViewUrl;
    }

    public String getVerifyUserUrl() {
        return verifyUserUrl;
    }

    public String getResendVerificationMessageUrl() {
        return resendVerificationMessageUrl;
    }

    public String getForgotPasswordUrl() {
        return forgotPasswordUrl;
    }

    public String getChangePasswordUrl() {
        return changePasswordUrl;
    }

    public String getRequestContactInformationChangeUrl() {
        return requestContactInformationChangeUrl;
    }

    public String getFetchNewAuthTokenUrl() {
        return fetchNewAuthTokenUrl;
    }

    public String getTestTokenUrl() {
        return testTokenUrl;
    }

    public String getBlockUserUrl() {
        return blockUserUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setResetPasswordUrl(String resetPasswordUrl) {
        this.resetPasswordUrl = resetPasswordUrl;
    }

    public void setResetPasswordViewUrl(String resetPasswordViewUrl) {
        this.resetPasswordViewUrl = resetPasswordViewUrl;
    }

    public void setChangeContactInformationUrl(String changeContactInformationUrl) {
        this.changeContactInformationUrl = changeContactInformationUrl;
    }

    public void setChangeContactInformationViewUrl(String changeContactInformationViewUrl) {
        this.changeContactInformationViewUrl = changeContactInformationViewUrl;
    }

    public void setVerifyUserUrl(String verifyUserUrl) {
        this.verifyUserUrl = verifyUserUrl;
    }

    public void setResendVerificationMessageUrl(String resendVerificationMessageUrl) {
        this.resendVerificationMessageUrl = resendVerificationMessageUrl;
    }

    public void setForgotPasswordUrl(String forgotPasswordUrl) {
        this.forgotPasswordUrl = forgotPasswordUrl;
    }

    public void setChangePasswordUrl(String changePasswordUrl) {
        this.changePasswordUrl = changePasswordUrl;
    }

    public void setRequestContactInformationChangeUrl(String requestContactInformationChangeUrl) {
        this.requestContactInformationChangeUrl = requestContactInformationChangeUrl;
    }

    public void setFetchNewAuthTokenUrl(String fetchNewAuthTokenUrl) {
        this.fetchNewAuthTokenUrl = fetchNewAuthTokenUrl;
    }

    public void setTestTokenUrl(String testTokenUrl) {
        this.testTokenUrl = testTokenUrl;
    }

    public void setBlockUserUrl(String blockUserUrl) {
        this.blockUserUrl = blockUserUrl;
    }

    public S getUserService() {
        return userService;
    }

    //              INJECT DEPENDENCIES


    @Autowired
    public void setViewResolver(ThymeleafViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }
    @Autowired
    public void setUserService(S userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthProperties(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Autowired
    public void setUserAuthTokenService(UserAuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @Autowired
    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Autowired
    public void setContactInformationService(ContactInformationService contactInformationService) {
        this.contactInformationService = contactInformationService;
    }

    @Autowired
    public void setVerificationService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Autowired
    public void setAuthorizationTokenService(AuthorizationTokenService authorizationTokenService) {
        this.authorizationTokenService = authorizationTokenService;
    }
}
