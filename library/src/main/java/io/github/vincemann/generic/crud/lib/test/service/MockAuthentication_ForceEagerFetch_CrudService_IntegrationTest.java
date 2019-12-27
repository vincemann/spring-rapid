package io.github.vincemann.generic.crud.lib.test.service;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Mocks Springs {@link SecurityContext} and {@link Authentication}, and lets user dynamically
 * set its own mocked SecurityContext/Authentication.
 *
 * Use this class when {@link org.springframework.security.test.context.support.WithMockUser} and similar Spring Mocking support
 * is not enough. (Usually when mocking dynamically is required)
 * @param <S>
 * @param <R>
 * @param <E>
 * @param <Id>
 */
public abstract class MockAuthentication_ForceEagerFetch_CrudService_IntegrationTest<
                S extends CrudService<E,Id,R>,
                R extends CrudRepository<E,Id>,
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
                > extends ForceEagerFetch_CrudServiceIntegrationTest<S, R, E, Id> {

    private Authentication authenticationMock;
    private SecurityContext securityContextMock;

    private S unsecuredCrudService;
    private S securedCrudService;

    @Autowired
    public void injectUnsecuredCrudService(S unsecuredCrudService) {
        //for simplicity we expect the same impl for unsecured and secured Crud service (in case user does not make use of this feature)
        //however, if he wants to use the feature, he can override the setCrudService method and inject his SecuredProxy impl
        this.unsecuredCrudService = unsecuredCrudService;
    }

    @Autowired
    @Override
    public void injectCrudService(S crudService) {
        //cache secured CrudService
        this.securedCrudService=crudService;
        super.injectCrudService(crudService);
    }

    @Override
    public final void setCrudService(S crudService) {
        super.setCrudService(crudService);
    }

    public void switchToUnsecuredCrudService(){
        setCrudService(unsecuredCrudService);
    }

    public void switchToSecuredCrudService(){
        setCrudService(securedCrudService);
    }


    @BeforeEach
    void setUpAuthMocks() {
        authenticationMock = Mockito.mock(Authentication.class);
        // Mockito.whens() for your authorization object
        securityContextMock = Mockito.mock(SecurityContext.class);
    }


    public void mockAuthenticationWithUser(String userName,String password, String... authorities){

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userName,
                password,
                grantedAuthorities
        );
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(usernamePasswordAuthenticationToken);
        SecurityContextHolder.setContext(securityContextMock);
    }
}

