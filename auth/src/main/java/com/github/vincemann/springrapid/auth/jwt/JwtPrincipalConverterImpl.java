package com.github.vincemann.springrapid.auth.jwt;

import com.github.vincemann.springrapid.auth.AbstractUserRepository;
import com.github.vincemann.springrapid.auth.AbstractUser;
import com.github.vincemann.springrapid.auth.AuthenticatedPrincipalFactory;
import com.github.vincemann.springrapid.auth.util.MapUtils;

import com.github.vincemann.springrapid.auth.AuthPrincipal;
import com.github.vincemann.springrapid.auth.ex.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
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
    public Map<String,Object> toClaims(AuthPrincipal user) {
        return MapUtils.mapOf("contactInformation",user.getName());
    }


    @Override
    public AuthPrincipal toPrincipal(Map<String,Object> claims) throws AuthenticationCredentialsNotFoundException {
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
