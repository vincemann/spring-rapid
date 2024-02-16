package com.github.vincemann.springrapid.acldemo.service.ext.sec;

import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.service.ext.sec.NeedCreatePermissionOnParentForCreateExtension;
import com.github.vincemann.springrapid.core.DefaultExtension;
import com.github.vincemann.springrapid.core.service.CrudService;

@DefaultExtension(qualifier = Secured.class, service = CrudService.class)
public class MyNeedCreatePermissionOnParentForCreateExtension extends NeedCreatePermissionOnParentForCreateExtension {
}
