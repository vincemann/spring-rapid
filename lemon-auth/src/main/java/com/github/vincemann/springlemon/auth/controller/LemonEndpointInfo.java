package com.github.vincemann.springlemon.auth.controller;

import com.github.vincemann.springrapid.core.controller.CrudEndpointInfo;

public class LemonEndpointInfo extends CrudEndpointInfo {
    private boolean exposeSignup =true;
    private boolean expose =true;
    private boolean exposeUpdate =true;
    private boolean exposeDelete =true;
    private boolean exposeFindAll =true;
}
