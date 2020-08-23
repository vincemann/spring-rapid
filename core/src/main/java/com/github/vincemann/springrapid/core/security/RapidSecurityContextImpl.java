package com.github.vincemann.springrapid.core.security;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//hardcode use of UsernamePasswordToken and force to have all info regarding logged in user (also ip ect.) in typed Principal
//since all info is encapsulated there, there is no gain in giving control about different Authentication impls (realized via AuthFactories for example)
@Slf4j
public class RapidSecurityContextImpl<P extends RapidAuthenticatedPrincipal> implements RapidSecurityContext<P>, AopLoggable {

    private static final String TEMP_USER_NAME = "tempUserName@RapidSecurityContextImpl.com";
    private static final String TEMP_USER_PASSWORD = "tempUserPassword123@";

    private static final String TEMP_ADMIN_NAME = "tempAdminName@RapidSecurityContextImpl.com";
    private static final String TEMP_ADMIN_PASSWORD = "tempAdminPassword123@";

    @Override
    public P login(P principal) {
        P old = currentPrincipal();
        if (old!=null){
            log.warn("Principal: " + old + " was already logged in. This login will override old principal session");
        }
        Authentication auth = createToken(principal);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return old;
    }

    protected Authentication createToken(P principal) {
        return new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
    }

    protected Authentication createToken(String name, String password, Set<String> roles) {
        RapidAuthenticatedPrincipal principal = new RapidAuthenticatedPrincipal(name, password, roles);
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
        return (P) authentication.getPrincipal();
    }

    protected GrantedAuthority map(String role) {
        return new SimpleGrantedAuthority(role);
    }

    @Transactional
    @Override
    public void runAs(P principal, Runnable runnable) {
        runAs(createToken(principal),runnable);
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
                Sets.newHashSet(RapidRole.ADMIN)
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
        runnable.run();
        //restore
        log.debug("restoring old security context authentication: " + old);
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
