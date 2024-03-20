package com.github.vincemann.springrapid.auth.util;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.sec.AuthorizationUtils;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

public abstract class UserUtils {

    private UserUtils(){}

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
        Optional<T> userByContactInformation = (Optional<T>) userRepository.findByContactInformation(name);
        try {
            VerifyEntity.isPresent(userByContactInformation,"user with contactInformation: " + name+ " could not be found");
        } catch (EntityNotFoundException e) {
            throw new AccessDeniedException("user with contactInformation: " + name+ " could not be found",e);
        }
        return userByContactInformation.get();
    }


}
