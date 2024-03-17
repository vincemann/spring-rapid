package com.github.vincemann.springrapid.authtest;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.coretest.util.RapidTestUtil;
import com.github.vincemann.springrapid.coretest.util.TestPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.when;

public class RapidAuthTestUtil {

    public static RapidPrincipal createPrincipal(AbstractUser user){
        String[] roles = (String[]) user.getRoles().toArray(new String[0]);
        RapidPrincipal principal = TestPrincipal.create(user.getContactInformation(), roles);
        return principal;
    }

    public static void authenticateAnon(){
        RapidPrincipal anonUser = RapidSecurityContext.getAnonUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                anonUser, anonUser.getPassword(), anonUser.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
    }

    public static void authenticate(AbstractUser user){
        RapidPrincipal principal = createPrincipal(user);
        if (user.getId() != null)
            principal.setId(principal.getId());
        SecurityContext securityContext = RapidTestUtil.createMockSecurityContext(principal);
        SecurityContextHolder.setContext(securityContext);
    }


}
