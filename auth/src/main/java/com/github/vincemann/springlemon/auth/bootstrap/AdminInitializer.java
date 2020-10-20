package com.github.vincemann.springlemon.auth.bootstrap;

import com.github.vincemann.springlemon.auth.LemonProperties;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springrapid.acl.proxy.AclManaging;
import com.github.vincemann.springrapid.core.bootstrap.DatabaseDataInitializer;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Slf4j
/**
 * Adds admins from property file, if not already present in database.
 * @see LemonProperties#getAdmins()
 */
public class AdminInitializer extends DatabaseDataInitializer {

//    @Value("#{'${database.init.admin.emails}'.split(',')}")
//    private List<String> adminEmails;
//    @Value("#{'${database.init.admin.passwords}'.split(',')}")
//    private List<String> adminPasswords;

    private UserService<?,?> userService;
    private LemonProperties lemonProperties;
    private RapidSecurityContext<?> securityContext;


    @Override
    @Transactional
    public void init() {
//        Assert.isTrue(!adminEmails.isEmpty());
//        Assert.isTrue(!adminPasswords.isEmpty());
        securityContext.runAsAdmin(
                () -> {
                    try {
                        addAdmins();
                    } catch (BadEntityException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected void addAdmins() throws BadEntityException{
        List<LemonProperties.Admin> admins = lemonProperties.getAdmins();
//        int index = 0;
        for (LemonProperties.Admin admin : admins) {
            log.debug("registering admin:: " + admin.getEmail());

            // Check if the user already exists
            Optional<? extends AbstractUser<?>> byEmail = userService.findByEmail(admin.getEmail());
            if (byEmail.isPresent()) {
                // Doesn't exist. So, create it.
                log.debug("admin does not exist yet, creating: " + admin);
                userService.createAdminUser(admin);
            }else {
                log.debug("admin already exists. skipping.");
            }
//            index++;
        }
    }

    @Autowired
    @AclManaging
    public void injectUserService(UserService<?, ?> userService) {
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
