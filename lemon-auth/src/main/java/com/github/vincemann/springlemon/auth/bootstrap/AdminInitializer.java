package com.github.vincemann.springlemon.auth.bootstrap;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springrapid.acl.proxy.AclManaging;
import com.github.vincemann.springrapid.core.bootstrap.DatabaseDataInitializer;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
/**
 * Adds admins from property file, if not already present in database.
 * Also adds lemon admin
 * @see LemonProperties#getAdmin()
 */
public class AdminInitializer extends DatabaseDataInitializer {

    @Value("#{'${database.init.admin.emails}'.split(',')}")
    private List<String> adminEmails;
    @Value("#{'${database.init.admin.passwords}'.split(',')}")
    private List<String> adminPasswords;

    private UserService<?,?,?> userService;
    private LemonProperties lemonProperties;
    private RapidSecurityContext<?> securityContext;


    @Override
    @Transactional
    public void init() {
        Assert.isTrue(!adminEmails.isEmpty());
        Assert.isTrue(!adminPasswords.isEmpty());
        securityContext.runAsAdmin(
                () -> {
                    try {
                        addAdmins();
                    } catch (BadEntityException | EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void addAdmins() throws BadEntityException, EntityNotFoundException {
        //add lemon admin
        LemonProperties.Admin lemonAdmin = lemonProperties.getAdmin();
        adminEmails.add(lemonAdmin.getEmail());
        adminPasswords.add(lemonAdmin.getPassword());
        //add rapid admins
        int index = 0;
        for (String admin : adminEmails) {
            log.debug("registering admin:: " + admin);

            // Check if the user already exists
            AbstractUser<?> byEmail = userService.findByEmail(admin);
            if (byEmail == null) {
                // Doesn't exist. So, create it.
                LemonProperties.Admin toCreate = new LemonProperties.Admin(admin, adminPasswords.get(index));
                log.debug("admin does not exist yet, creating: " + toCreate);
                userService.createAdminUser(toCreate);
            }else {
                log.debug("admin already existing.");
            }
            index++;
        }
    }

    @Autowired
    @AclManaging
    public void injectUserService(UserService<?, ?, ?> userService) {
        this.userService = userService;
    }

    @Autowired
    public void injectLemonProperties(LemonProperties lemonProperties) {
        this.lemonProperties = lemonProperties;
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<?> securityContext) {
        this.securityContext = securityContext;
    }
}
