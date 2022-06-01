package com.github.vincemann.springrapid.auth.bootstrap;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.core.bootstrap.DatabaseInitializer;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
/**
 * Adds admins from property file, if not already present in database.
 * @see AuthProperties#getAdmins()
 */
public class AdminInitializer extends DatabaseInitializer {

    private UserService<AbstractUser<?>,?> userService;
    private AuthProperties authProperties;
    private RapidSecurityContext<?> securityContext;


    @Override
    @Transactional
    public void init() {
        securityContext.runAsAdmin(
                () -> {
                    try {
                        addAdmins();
                    } catch (BadEntityException | AlreadyRegisteredException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected void addAdmins() throws BadEntityException, AlreadyRegisteredException {
        List<AuthProperties.Admin> admins = authProperties.getAdmins();
        for (AuthProperties.Admin admin : admins) {
            log.debug("registering admin:: " + admin.getContactInformation());

            // Check if the user already exists
            Optional<? extends AbstractUser<?>> byContactInformation = userService.findByContactInformation(admin.getContactInformation());
            if (byContactInformation.isEmpty()) {
                // Doesn't exist. So, create it.
                log.debug("admin does not exist yet, creating: " + admin);
                userService.signupAdmin(userService.newAdmin(admin));
            }else {
                log.debug("admin already exists. skipping.");
            }
        }
    }

    @Autowired
    @Acl
    public void injectUserService(UserService<AbstractUser<?>, ?> userService) {
        this.userService = userService;
    }

    @Autowired
    public void injectLemonProperties(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Autowired
    public void injectSecurityContext(RapidSecurityContext<?> securityContext) {
        this.securityContext = securityContext;
    }
}
