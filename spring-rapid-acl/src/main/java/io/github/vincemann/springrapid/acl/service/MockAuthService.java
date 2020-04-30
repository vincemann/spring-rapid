package io.github.vincemann.springrapid.acl.service;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@ServiceComponent
public interface MockAuthService {
    String TEMP_USER_EMAIL = "tempUser@mockAuthService.com";
    String TEMP_USER_PASSWORD = "tempUserPassword123@";

    public void runAuthenticatedAs(Authentication authentication, Runnable runnable);

    public default void runAuthenticatedWith(Set<String> roles, Runnable runnable) {
        runAuthenticatedAs(new UsernamePasswordAuthenticationToken(
                TEMP_USER_EMAIL,
                TEMP_USER_PASSWORD,
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toSet())
                ),runnable);
    }

    public default void runAuthenticated(Runnable runnable){
        runAuthenticatedAs(new UsernamePasswordAuthenticationToken(
                TEMP_USER_EMAIL,
                TEMP_USER_PASSWORD
        ),runnable);
    }

    public void runAuthenticatedAsAdmin(Runnable privRunnable);

    public default void runAuthenticatedAs(String user, Runnable runnable){
        runAuthenticatedAs(new UsernamePasswordAuthenticationToken(
                user,
                TEMP_USER_PASSWORD
        ),runnable);
    }


}
