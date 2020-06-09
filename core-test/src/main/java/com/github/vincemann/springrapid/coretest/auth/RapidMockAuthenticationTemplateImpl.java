package com.github.vincemann.springrapid.coretest.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @see MockAuthenticationTemplate
 */
public class RapidMockAuthenticationTemplateImpl extends AbstractMockAuthenticationTemplate implements RapidMockAuthenticationTemplate{

    @Override
    public void mockAs(String email, String password, Set<String> roles){
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        User principal = new User(email,password,grantedAuthorities);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                principal,
                password,
                grantedAuthorities
        );

        mockAs(usernamePasswordAuthenticationToken);
    }
}
