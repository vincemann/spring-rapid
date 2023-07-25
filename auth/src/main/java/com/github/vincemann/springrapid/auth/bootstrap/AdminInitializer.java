package com.github.vincemann.springrapid.auth.bootstrap;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.core.bootstrap.DatabaseInitializer;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Slf4j
/**
 * Adds admins from property file, if not already present in database.
 * @see AuthProperties#getAdmins()
 */
public class AdminInitializer extends DatabaseInitializer {

    private UserService<AbstractUser<Serializable>, Serializable> userService;
    private AuthProperties authProperties;
    private RapidSecurityContext<?> securityContext;


    @Override
    @Transactional
    public void init() {
        securityContext.runAsAdmin(
                () -> {
                    try {
                        addAdmins();
                    } catch (BadEntityException | AlreadyRegisteredException | EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected void addAdmins() throws BadEntityException, AlreadyRegisteredException, EntityNotFoundException {
        List<AuthProperties.Admin> admins = authProperties.getAdmins();
        for (AuthProperties.Admin admin : admins) {
            log.debug("registering admin:: " + admin.getContactInformation());

            // Check if the user already exists
            Optional<AbstractUser<Serializable>> byContactInformation = userService.findByContactInformation(admin.getContactInformation());
            if (byContactInformation.isPresent()) {
                log.debug("admin already exists.");
                Boolean replace = admin.getReplace();
                if (replace == null){
                    log.debug("replace value for admin not set, default to replacing");
                    replace = Boolean.TRUE;
                }
                if (replace){
                    log.debug("replacing...");
                    userService.deleteById(byContactInformation.get().getId());
                }else {
                    log.debug("keep old admin");
                    continue;
                }
            }
            // Doesn't exist. So, create it.
            log.debug("creating: " + admin);
            userService.signupAdmin(userService.newAdmin(admin));
        }
    }

    @Autowired
    @Acl
    public void injectUserService(UserService<AbstractUser<Serializable>, Serializable> userService) {
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
