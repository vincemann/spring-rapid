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
    private boolean exposeFetchByContactInformation =true;
    private boolean exposeChangePassword=true;
    private boolean exposeRequestContactInformationChange=true;
    private boolean exposeChangeContactInformation=true;
//    private boolean exposeChangeContactInformationView=true;
    private boolean exposeNewAuthToken =true;
    private boolean exposePing=true;
}
