package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.auth.model.AbstractUserRepository;
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

    private AbstractUserRepository userRepository;
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
        String ci = (String) claims.get("contactInformation");
        if (ci == null)
            throw new AuthenticationCredentialsNotFoundException("contactInformation claim of claims-set not found");
        try {
            Optional<AbstractUser<?>> byContactInformation = userRepository.findByContactInformation(ci);
            VerifyEntity.isPresent(byContactInformation,"User with contactInformation: "+ci+" not found");
            AbstractUser<?> user = byContactInformation.get();
            return authenticatedPrincipalFactory.create(user);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationCredentialsNotFoundException("User with in token encoded contactInformation: " + ci + " does not exist.", e);
        }
    }

    @Autowired
    public void setUserRepository(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
