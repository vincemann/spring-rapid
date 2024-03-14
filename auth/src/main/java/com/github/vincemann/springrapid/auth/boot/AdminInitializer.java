package com.github.vincemann.springrapid.auth.boot;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.log.LogMessage;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Adds admins from property file, if not already present in database.
 * @see AuthProperties#getAdmins()
 */
public class AdminInitializer implements CommandLineRunner {

    private final Log log = LogFactory.getLog(AdminInitializer.class);

    private UserService userService;
    private AuthProperties authProperties;


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        signupAdmins();
    }


    public void signupAdmins() throws BadEntityException, AlreadyRegisteredException, EntityNotFoundException {
        List<AuthProperties.Admin> admins = authProperties.getAdmins();
        for (AuthProperties.Admin admin : admins) {
            log.debug(LogMessage.format("registering admin: %s ",admin.getContactInformation()));

            // Check if the user already exists
            Optional<AbstractUser<Serializable>> saved = userService.findByContactInformation(admin.getContactInformation());
            if (saved.isPresent()) {
                log.debug("admin already exists.");
                Boolean replace = admin.getReplace();
                if (replace == null){
                    log.debug("replace value for admin not set, default to replacing");
                    replace = Boolean.TRUE;
                }
                if (replace){
                    log.debug("replacing...");
                    userService.deleteById(saved.get().getId());
                }else {
                    log.debug("keeping old admin");
                    continue;
                }
            }
            // Doesn't exist. So, create it.
            log.debug("creating: " + admin);
            userService.create(userService.createAdmin(admin));
        }
    }

    @Autowired
    @Root
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthProperties(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

}
