package io.github.vincemann.springlemon.test.auth;

import io.github.spring.lemon.auth.domain.dto.user.LemonUserDto;
import io.github.spring.lemon.auth.security.domain.LemonPrincipal;
import io.github.vincemann.springrapid.coretest.BeforeEachMethodInitializable;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@TestComponent
public class LemonMockAuthenticationTemplateImpl
        implements LemonMockAuthenticationTemplate, BeforeEachMethodInitializable {

    private Authentication authenticationMock;
    private SecurityContext securityContextMock;
    
    private boolean mocked;
    private SecurityContext realSecurityContext;
    private Authentication realAuthentication;


    @Override
    public boolean isMocked() {
        return mocked;
    }

    @Override
    //before each
    public void init() {
        setUpAuthMocks();
    }


    private void setUpAuthMocks() {
        realSecurityContext = SecurityContextHolder.getContext()==null
                ? SecurityContextHolder.createEmptyContext()
                : SecurityContextHolder.getContext();
        realAuthentication = realSecurityContext.getAuthentication();
        enableMocking();
    }

    @Override
    public void enableMocking(){
        authenticationMock = Mockito.spy(Authentication.class);
        // Mockito.whens() for your authorization object
        securityContextMock = Mockito.spy(SecurityContext.class);
        mocked =true;
    }

    @Override
    public void disableMocking(){
//        Mockito.reset(authenticationMock);
//        Mockito.reset(securityContextMock);
        SecurityContextHolder.setContext(realSecurityContext);
        realSecurityContext.setAuthentication(realAuthentication);
        mocked =false;
    }

    @Override
    public void mockAs(String email, String password, Set<String> roles, String id){
        if(!mocked){
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
        LemonPrincipal lemonPrincipal = new LemonPrincipal(lemonUserDto);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                lemonPrincipal,
                password,
                grantedAuthorities
        );
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(usernamePasswordAuthenticationToken);
        SecurityContextHolder.setContext(securityContextMock);
    }
}
