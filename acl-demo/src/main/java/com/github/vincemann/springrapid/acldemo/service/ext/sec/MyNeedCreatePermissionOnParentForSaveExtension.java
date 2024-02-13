package com.github.vincemann.springrapid.acldemo.service.ext.sec;

import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForSaveExtension;
import com.github.vincemann.springrapid.core.DefaultExtension;
import org.springframework.stereotype.Component;

@DefaultExtension(qualifier = Secured.class)
public class MyNeedCreatePermissionOnParentForSaveExtension extends NeedCreatePermissionOnParentForSaveExtension {
}
