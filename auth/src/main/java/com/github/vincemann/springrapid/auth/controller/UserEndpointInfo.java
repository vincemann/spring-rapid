package com.github.vincemann.springrapid.auth.controller;

import com.github.vincemann.springrapid.core.controller.CrudEndpointInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEndpointInfo extends CrudEndpointInfo {
    private boolean exposeSignup =true;
    private boolean exposeContext =true;
    private boolean exposeResendVerificationMail =true;
    private boolean exposeVerifyUser =true;
    private boolean exposeForgotPassword =true;
    private boolean exposeResetPassword =true;
    private boolean exposeResetPasswordView =true;
    private boolean exposeFetchByEmail =true;
    private boolean exposeChangePassword=true;
    private boolean exposeRequestEmailChange=true;
    private boolean exposeChangeEmail=true;
    private boolean exposeNewAuthToken =true;
    private boolean exposePing=true;
}
