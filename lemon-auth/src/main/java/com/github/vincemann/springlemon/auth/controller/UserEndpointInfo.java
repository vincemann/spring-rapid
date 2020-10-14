package com.github.vincemann.springlemon.auth.controller;

import com.github.vincemann.springrapid.core.controller.CrudEndpointInfo;

public class UserEndpointInfo extends CrudEndpointInfo {
    private boolean exposeSignup =true;
    private boolean exposeContext =true;
    private boolean exposeResendVerificationMail =true;
    private boolean exposeVerifyUser =true;
    private boolean exposeForgotPassword =true;
    private boolean exposeResetPassword =true;
    private boolean exposeFetchUserByEmail=true;
    private boolean exposeChangePassword=true;
    private boolean exposeRequestEmailChange=true;
    private boolean exposeChangeEmail=true;
    private boolean exposeFetchNewAuthToken=true;
    private boolean exposePing=true;
}
