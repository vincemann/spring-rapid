package com.github.vincemann.springrapid.auth.service;

import com.github.vincemann.springrapid.acl.AclTemplate;
import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;

import static com.github.vincemann.springrapid.auth.util.UserUtils.findPresentByContactInformation;

public class SecuredContactInformationService implements ContactInformationService{

    private ContactInformationService decorated;
    private JweTokenService jweTokenService;
    private AbstractUserRepository userRepository;

    private UserService userService;
    private AclTemplate aclTemplate;

    public SecuredContactInformationService(ContactInformationService decorated) {
        this.decorated = decorated;
    }

    @Transactional
    @Override
    public AbstractUser changeContactInformation(String code) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException, BadTokenException {
        JWTClaimsSet claims = jweTokenService.parseToken(code);
        Serializable userId = claims.getSubject();
        Assert.notNull(userId,"no userid (subject) found in code");
        aclTemplate.checkPermission(userId, userService.getEntityClass(), BasePermission.WRITE);
        return decorated.changeContactInformation(code);
    }

    @Transactional
    @Override
    public AbstractUser requestContactInformationChange(RequestContactInformationChangeDto dto) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException {
        AbstractUser user = findPresentByContactInformation(userRepository,dto.getOldContactInformation());
        aclTemplate.checkPermission(user, BasePermission.WRITE);
        return decorated.requestContactInformationChange(dto);
    }

    @Autowired
    public void setJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }


    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setAclTemplate(AclTemplate aclTemplate) {
        this.aclTemplate = aclTemplate;
    }
}
