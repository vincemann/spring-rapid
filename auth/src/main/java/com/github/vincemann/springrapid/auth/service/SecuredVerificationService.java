package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.auth.util.UserUtils;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

public class SecuredVerificationService implements VerificationService{

    private VerificationService decorated;
    private AbstractUserRepository repository;
    private AclTemplate aclTemplate;

    public SecuredVerificationService(VerificationService decorated) {
        this.decorated = decorated;
    }

    @Override
    public AbstractUser makeUnverified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractUser makeVerified(AbstractUser user) throws BadEntityException, EntityNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Transactional
    @Override
    public AbstractUser resendVerificationMessage(String contactInformation) throws EntityNotFoundException, BadEntityException {
        AbstractUser user = UserUtils.findPresentByContactInformation(repository,contactInformation);
        aclTemplate.checkPermission(user, BasePermission.ADMINISTRATION);
        return decorated.resendVerificationMessage(contactInformation);
    }

    @Transactional
    @Override
    public AbstractUser verifyUser(String code) throws EntityNotFoundException, BadTokenException, BadEntityException {
        return decorated.verifyUser(code);
    }

    @Autowired
    public void setRepository(AbstractUserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setAclTemplate(AclTemplate aclTemplate) {
        this.aclTemplate = aclTemplate;
    }
}
