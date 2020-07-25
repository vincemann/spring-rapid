package com.github.vincemann.springlemon.test.auth;

import com.google.common.collect.Sets;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonRole;
import com.github.vincemann.springrapid.acl.Role;
import com.github.vincemann.springrapid.coretest.auth.MockAuthenticationTemplate;

import java.util.Set;

/**
 * Offers some more lemon-specific functions then {@link MockAuthenticationTemplate} + implement lemon specific way of mocking auth in general.
 */
public interface LemonMockAuthenticationTemplate extends MockAuthenticationTemplate{
    String UNKNOWN_ID = "-1";


    public default Set<String> getDefaultAuthorities(){
        return Sets.newHashSet(LemonRole.GOOD_USER, Role.USER);
    }

    public default void mockAsAdmin(){
        mockAs("admin@mockAuthTemplate.com", Sets.newHashSet(Role.ADMIN,LemonRole.GOOD_ADMIN), UNKNOWN_ID);
    }

    public default void mockAs(String authenticatedEmail, Set<String> roles,String id){
        mockAs(authenticatedEmail,"password123@Strong", roles,id);
    }

    public default void mockAs(AbstractUser user){
        String id = user.getId()==null ? UNKNOWN_ID: user.getId().toString();
        int amountRoles = user.getRoles() ==null ? 0 : user.getRoles().size();
        if(amountRoles==0){
            mockAs(user.getEmail(),user.getPassword(), getDefaultAuthorities(),id);
        }else {
            mockAs(user.getEmail(), user.getPassword(), user.getRoles(),id);
        }
    }

    public void mockAs(String email, String password, Set<String> roles, String id);
}
