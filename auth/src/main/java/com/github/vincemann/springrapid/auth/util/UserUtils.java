package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.RapidSecurityContext;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.IdConverter;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;

public abstract class UserUtils {

    private UserUtils(){}

    public static AbstractUser extractUserFromClaims(IdConverter idConverter, AbstractUserRepository userRepository, JWTClaimsSet claims) throws EntityNotFoundException {
        Serializable id = idConverter.toId(claims.getSubject());
        Assert.notNull(id,"user id must not be null");
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
        String ci = RapidSecurityContext.getName();
        if (ci == null)
            throw new AccessDeniedException("no authenticated user");
        Optional<T> user = (Optional<T>) userRepository.findByContactInformation(ci);
        if (user.isEmpty())
            throw new AccessDeniedException("user with contactInformation: " + ci+ " could not be found");
        return user.get();
    }


}
