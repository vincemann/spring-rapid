package com.github.vincemann.springrapid.core.security;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Type-safe Wrapper for Springs {@link SecurityContext#getAuthentication()}, with focus of putting all info into the {@link Authentication#getPrincipal()}.
 *
 *
 *
 * Implementation Details:
 * Hardcode use of UsernamePasswordToken and force to have all info regarding logged in user (also {@link Authentication#getDetails()}) in typed Principal.
 * Since all info is encapsulated there, there is no gain in giving control about different Authentication impls (realized via AuthenticationFactories for example)
 **/
@Slf4j
public abstract class AbstractRapidSecurityContext<P extends RapidAuthenticatedPrincipal>
        implements RapidSecurityContext<P>
{

    public static final String TEMP_USER_NAME = "tempUserName@RapidSecurityContextImpl.com";
    public static final String TEMP_USER_PASSWORD = "tempUserPassword123@";

    public static final String TEMP_ADMIN_NAME = "tempAdminName@RapidSecurityContextImpl.com";
    public static final String TEMP_ADMIN_PASSWORD = "tempAdminPassword123@";

    //is always invalid
    private static final String TEMP_ID = "-1";

    @Override
    public P login(P principal) {
        P old = currentPrincipal();
        if (old != null) {
            log.warn("Principal: " + old + " was already logged in. This login will override old principals session");
        }
        Authentication auth = createToken(principal);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return old;
    }


    protected Authentication createToken(P principal) {
        return new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
    }

    protected Authentication createToken(String name, String password, Set<String> roles) {
        RapidAuthenticatedPrincipal principal = new RapidAuthenticatedPrincipal(name, password, roles, TEMP_ID);
        return new UsernamePasswordAuthenticationToken(
                principal,
                password,
                roles.stream().map(this::map).collect(Collectors.toSet()));
    }

    @Override
    public P currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        try {
            return (P) authentication.getPrincipal();
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Security Context is malformed. Create with RapidSecurityContext.login(...)");
        }
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    protected GrantedAuthority map(String role) {
        return new SimpleGrantedAuthority(role);
    }

    @Transactional
    @Override
    public void runAs(P principal, Runnable runnable) {
        runAs(createToken(principal), runnable);
    }

    @Transactional
    public void runWithRoles(Set<String> roles, Runnable runnable) {
        runAs(createToken(
                TEMP_USER_NAME,
                TEMP_USER_PASSWORD,
                roles
        ), runnable);
    }

    @Transactional
    public void runAuthenticated(Runnable runnable) {
        runAs(createToken(
                TEMP_USER_NAME,
                TEMP_USER_PASSWORD,
                new HashSet<>()
        ), runnable);
    }

    @Transactional
    public void runAsAdmin(Runnable privRunnable) {
        runAs(createToken(TEMP_ADMIN_NAME,
                TEMP_ADMIN_PASSWORD,
                Sets.newHashSet(Roles.ADMIN)
        ), privRunnable);
    }

    @Transactional
    public void runWithName(String name, Runnable runnable) {
        runAs(createToken(
                name,
                TEMP_USER_PASSWORD,
                new HashSet<>()
        ), runnable);
    }

    protected void runAs(Authentication token, Runnable runnable) {
        Authentication old = SecurityContextHolder.getContext().getAuthentication();
        log.debug("saving old security context authentication: " + old);
        SecurityContextHolder.getContext().setAuthentication(token);
        runnable.run();
        //restore
        log.debug("restoring old security context authentication: " + old);
        SecurityContextHolder.getContext().setAuthentication(old);

    }
}
