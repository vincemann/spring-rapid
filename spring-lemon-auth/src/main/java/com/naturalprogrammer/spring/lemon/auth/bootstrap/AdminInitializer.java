package com.naturalprogrammer.spring.lemon.auth.bootstrap;

import com.google.common.collect.Lists;
import com.naturalprogrammer.spring.lemon.auth.properties.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.acl.service.AclManaging;
import io.github.vincemann.springrapid.acl.service.MockAuthService;
import io.github.vincemann.springrapid.core.bootstrap.Initializer;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
/**
 * Adds admins from property file, if not already present in database.
 * Also adds lemon admin
 * @see LemonProperties#getAdmin()
 */
//todo backend muss noch order3 und profile dev,prod machen
public class AdminInitializer extends Initializer {

    @Value("#{'${database.init.admin.emails}'.split(',')}")
    private List<String> adminEmails;
    @Value("#{'${database.init.admin.passwords}'.split(',')}")
    private List<String> adminPasswords;

    //private SchoolService schoolService;
    private MockAuthService mockAuthService;
    private UserDetailsService userDetailsService;
    private LemonService<?,?,?> lemonService;
    private LemonProperties lemonProperties;

    @Autowired
    public AdminInitializer(@AclManaging LemonService<?,?,?> lemonService,
                            MockAuthService mockAuthService,
                            UserDetailsService userDetailsService,
                            LemonProperties lemonProperties) {
        this.lemonService = lemonService;
        this.mockAuthService = mockAuthService;
        this.userDetailsService = userDetailsService;
        this.lemonProperties = lemonProperties;
    }

    @Override
    @Transactional
    public void init() {
        Assert.isTrue(!adminEmails.isEmpty());
        Assert.isTrue(!adminPasswords.isEmpty());
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                adminEmails.get(0),
                adminPasswords.get(0),
                Lists.newArrayList(
                        new SimpleGrantedAuthority(Role.ADMIN)
                )
        );
        mockAuthService.runAuthenticatedAs(adminAuth,
                () -> {
                    try {
                        addAdmins();
                    } catch (BadEntityException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void addAdmins() throws BadEntityException {
        //add lemon admin
        LemonProperties.Admin lemonAdmin = lemonProperties.getAdmin();
        adminEmails.add(lemonAdmin.getEmail());
        adminPasswords.add(lemonAdmin.getPassword());
        //add rapid admins
        int index = 0;
        for (String admin : adminEmails) {
            log.debug("registering admin:: " + admin);

            // Check if the user already exists
            AbstractUser<?> byEmail = lemonService
                    .findByEmail(admin);
            if (byEmail != null) {
                // Doesn't exist. So, create it.
                lemonService.createAdminUser(
                        new LemonProperties.Admin(admin, adminPasswords.get(index))
                );
            }
            index++;
        }
    }
}
