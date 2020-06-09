package com.github.vincemann.springlemon.auth.bootstrap;

import com.github.vincemann.springlemon.auth.properties.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springrapid.acl.service.AclManaging;
import com.github.vincemann.springrapid.acl.service.MockAuthService;
import com.github.vincemann.springrapid.core.bootstrap.Initializer;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        mockAuthService.runAuthenticatedAsAdmin(
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
            AbstractUser<?> byEmail = lemonService.findByEmail(admin);
            if (byEmail == null) {
                // Doesn't exist. So, create it.
                LemonProperties.Admin toCreate = new LemonProperties.Admin(admin, adminPasswords.get(index));
                log.debug("admin does not exist yet, creating: " + toCreate);
                lemonService.createAdminUser(toCreate);
            }else {
                log.debug("admin already existing.");
            }
            index++;
        }
    }
}
