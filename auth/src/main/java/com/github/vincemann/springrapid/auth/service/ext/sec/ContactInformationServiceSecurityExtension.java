package com.github.vincemann.springrapid.auth.service.ext.sec;

import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.auth.dto.RequestContactInformationChangeDto;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.ContactInformationService;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.auth.service.token.JweTokenService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;

import java.io.Serializable;
import java.util.Optional;

public class ContactInformationServiceSecurityExtension extends SecurityExtension<ContactInformationService>
        implements ContactInformationService {

    private JweTokenService jweTokenService;
    private UserService userService;

    @Override
    public AbstractUser changeContactInformation(String code) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException, BadTokenException {

        JWTClaimsSet claims = jweTokenService.parseToken(code);
        Serializable userId = claims.getSubject();
        if (userId == null) {
            throw new BadEntityException("No user found with id: " + userId);
        }
        getAclTemplate().checkPermission(userId, userService.getEntityClass(), BasePermission.WRITE);
        return getNext().changeContactInformation(code);
    }

    @Override
    public void requestContactInformationChange(RequestContactInformationChangeDto dto) throws EntityNotFoundException, BadEntityException, AlreadyRegisteredException {
        Optional<AbstractUser> user = userService.findByContactInformation(dto.getOldContactInformation());
        VerifyEntity.isPresent(user,"User not found with old contact information");
        getAclTemplate().checkPermission(user.get(), BasePermission.WRITE);
        getNext().requestContactInformationChange(dto);
    }

    @Autowired
    public void setJweTokenService(JweTokenService jweTokenService) {
        this.jweTokenService = jweTokenService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
