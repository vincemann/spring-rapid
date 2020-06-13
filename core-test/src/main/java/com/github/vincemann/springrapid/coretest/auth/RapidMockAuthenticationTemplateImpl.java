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

    //todo hier vllt noch id integraten oder im controller anstatt nach id, nach email suchen?
    //und dann anhand email vergleichen ob principals OWN oder Foreign oder all ist?
    //es geht darum, dass wenn ich mockauthtemplate nutze, der currentUserIdProvider nicht mit mocked ist
    //vllt sollte ich den ganz rausnehmen und nur anhand von email gehen...
    // der ist eh schrott, noch mal schauen ob und wo der gebraucht wird
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
