package com.github.vincemann.springrapid.acl.service.sec;

import com.github.vincemann.springrapid.acl.AclTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class SecuredServiceDecorator<T>{
    private T decorated;
    private AclTemplate aclTemplate;

    public SecuredServiceDecorator(T decorator) {
        this.decorated = decorator;
    }

    public T getDecorated() {
        return decorated;
    }

    public AclTemplate getAclTemplate() {
        return aclTemplate;
    }

    @Autowired
    public void setAclTemplate(AclTemplate aclTemplate) {
        this.aclTemplate = aclTemplate;
    }
}
