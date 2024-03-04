package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.util.MapUtils;

import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Only stores contactInformation in token and fetches user args for principal lazily
 */
@Transactional
public class JwtPrincipalConverterImpl implements JwtPrincipalConverter {

    private UserService userService;
    private AuthenticatedPrincipalFactory authenticatedPrincipalFactory;

    @Autowired
    public void setAuthenticatedPrincipalFactory(AuthenticatedPrincipalFactory authenticatedPrincipalFactory) {
        this.authenticatedPrincipalFactory = authenticatedPrincipalFactory;
    }

    @Override
    public Map<String,Object> toClaims(RapidPrincipal user) {
        return MapUtils.mapOf("contactInformation",user.getName());
    }


    @Override
    public RapidPrincipal toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException {
        String contactInformation = (String) claims.get("contactInformation");
        if (contactInformation == null)
            throw new AuthenticationCredentialsNotFoundException("contactInformation claim of claims-set not found");
        try {
            Optional<AbstractUser<?>> byContactInformation = userService.findByContactInformation(contactInformation);
            VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+contactInformation+" not found");
            AbstractUser<?> user = byContactInformation.get();
//            return new RapidAuthAuthenticatedPrincipal(user);
            return authenticatedPrincipalFactory.create(user);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("User with in token encoded contactInformation: " + contactInformation + " does not exist.", e);
        }
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
