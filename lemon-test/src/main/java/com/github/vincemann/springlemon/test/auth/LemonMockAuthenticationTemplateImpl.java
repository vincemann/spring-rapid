package com.github.vincemann.springlemon.test.auth;

import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springrapid.coretest.BeforeEachMethodInitializable;
import com.github.vincemann.springrapid.coretest.auth.AbstractMockAuthenticationTemplate;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class LemonMockAuthenticationTemplateImpl extends AbstractMockAuthenticationTemplate
        implements LemonMockAuthenticationTemplate, BeforeEachMethodInitializable {


    @Override
    //before each
    public void init() {
        setUpAuthMocks();
    }


    @Override
    public void mockAs(String email, String password, Set<String> roles, String id){
        if(!isMocked()){
            enableMocking();
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        LemonUserDto lemonUserDto = new LemonUserDto();
        lemonUserDto.setEmail(email);
        lemonUserDto.setPassword(password);
        lemonUserDto.setRoles(roles);
        lemonUserDto.setId(id);
        LemonAuthenticatedPrincipal lemonPrincipal = new LemonAuthenticatedPrincipal(lemonUserDto);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                lemonPrincipal,
                password,
                grantedAuthorities
        );
        Mockito.when(getSecurityContextMock().getAuthentication()).thenReturn(usernamePasswordAuthenticationToken);
        SecurityContextHolder.setContext(getSecurityContextMock());
    }
}
