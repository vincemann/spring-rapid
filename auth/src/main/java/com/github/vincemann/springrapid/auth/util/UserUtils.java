package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.sec.AuthorizationUtils;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.util.RepositoryUtil;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;

public abstract class UserUtils {

    private UserUtils(){}

    public static AbstractUser extractUserFromClaims(IdConverter idConverter, AbstractUserRepository userRepository, JWTClaimsSet claims) throws EntityNotFoundException {
        Serializable id = idConverter.toId(claims.getSubject());
        Assert.notNull(id);
        // fetch the user
        return (AbstractUser) RepositoryUtil.findPresentById(userRepository,(id));
    }

    public static <T extends AbstractUser> T findPresentByContactInformation(AbstractUserRepository repository, String contactInformation) throws EntityNotFoundException {
        Optional<T> user = repository.findByContactInformation(contactInformation);
        VerifyEntity.isPresent(user,"no entity found with contact information : " + contactInformation
                + ", managed by: " + repository.getClass().getSimpleName());
        return user.get();
    }

    public static <T extends AbstractUser> T findPresentByContactInformation(UserService service, String contactInformation) throws EntityNotFoundException {
        Optional<T> user = service.findByContactInformation(contactInformation);
        VerifyEntity.isPresent(user,"no entity found with contact information : " + contactInformation
                + ", managed by: " + service.getClass().getSimpleName());
        return user.get();
    }

    public static <T extends AbstractUser> T findAuthenticatedUser(AbstractUserRepository userRepository){
        AuthorizationUtils.assertAuthenticated();
        String name = RapidSecurityContext.getName();
        Optional<T> user = (Optional<T>) userRepository.findByContactInformation(name);
        try {
            VerifyEntity.isPresent(user,"user with contactInformation: " + name+ " could not be found");
        } catch (EntityNotFoundException e) {
            throw new AccessDeniedException("user with contactInformation: " + name+ " could not be found",e);
        }
        return user.get();
    }


}
