package com.naturalprogrammer.spring.lemon.auth.bootstrap;

import com.google.common.collect.Lists;
import com.naturalprogrammer.spring.lemon.auth.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.acl.service.AclManaging;
import io.github.vincemann.springrapid.acl.service.RunAsUserService;
import io.github.vincemann.springrapid.core.bootstrap.DatabaseDataInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Component
/**
 * Adds admins from property file, if not already present in database
 * Also adds lemon admin
 * @see LemonProperties#getAdmin()
 */
//todo backend muss noch order3 und profile dev,prod machen
public class AdminDatabaseDataInitializer extends DatabaseDataInitializer {

    @Value("#{'${database.init.admin.usernames}'.split(',')}")
    private List<String> adminUsername;
    @Value("#{'${database.init.admin.passwords}'.split(',')}")
    private List<String> adminPasswords;

    //private SchoolService schoolService;
    private RunAsUserService runAsUserService;
    private UserDetailsService userDetailsService;
    private LemonService lemonService;
    private LemonProperties lemonProperties;

    @Autowired
    public AdminDatabaseDataInitializer(@AclManaging LemonService lemonService,
                                        RunAsUserService runAsUserService,
                                        UserDetailsService userDetailsService,
                                        LemonProperties lemonProperties) {
        this.lemonService=lemonService;
        this.runAsUserService = runAsUserService;
        this.userDetailsService = userDetailsService;
        this.lemonProperties = lemonProperties;
    }

    @Override
    @Transactional
    public void loadInitData()  {
        Assert.isTrue(!adminUsername.isEmpty());
        Assert.isTrue(!adminPasswords.isEmpty());
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                adminUsername.get(0),
                adminPasswords.get(0),
                Lists.newArrayList(
                        new SimpleGrantedAuthority(Role.ADMIN)
                )
        );
        addAdmins();
        runAsUserService.runAuthenticatedAs(adminAuth, this::addAdmins);
        setInitialized(true);
    }

    private void addAdmins() {
        //add lemon admin
        LemonProperties.Admin lemonAdmin = lemonProperties.getAdmin();
        adminUsername.add(lemonAdmin.getUsername());
        adminPasswords.add(lemonAdmin.getPassword());
        //add rapid admins
        int index = 0;
        for (String admin : adminUsername) {
            log.debug("registering admin:: " + admin);
            try {
                // Check if the user already exists
                userDetailsService
                        .loadUserByUsername(admin);

            } catch (UsernameNotFoundException e) {

                // Doesn't exist. So, create it.

                lemonService.createAdminUser(
                        new LemonProperties.Admin(admin, adminPasswords.get(index))
                );
            }
            index++;
        }
    }
}
