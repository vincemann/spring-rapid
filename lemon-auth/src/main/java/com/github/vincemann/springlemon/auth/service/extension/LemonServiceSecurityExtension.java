package com.github.vincemann.springlemon.auth.service.extension;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.AbstractUserRepository;
import com.github.vincemann.springlemon.auth.domain.LemonRole;
import com.github.vincemann.springlemon.auth.domain.dto.ChangePasswordForm;
import com.github.vincemann.springlemon.auth.domain.dto.RequestEmailChangeForm;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.service.SimpleLemonService;
import com.github.vincemann.springlemon.auth.util.LecwUtils;
import com.github.vincemann.springrapid.acl.proxy.SecurityServiceExtension;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.RapidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;


@Transactional
@Slf4j
public class LemonServiceSecurityExtension
        extends SecurityServiceExtension<SimpleLemonService>
        implements SimpleLemonServiceExtension<SimpleLemonService>{

    private AbstractUserRepository userRepository;

    @Autowired
    public LemonServiceSecurityExtension(AbstractUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        getSecurityChecker().checkRole(LemonRole.GOOD_ADMIN);
        return getNext().save(entity);
    }

    @Override
    public void resendVerificationMail(AbstractUser user) throws EntityNotFoundException {
        getSecurityChecker().checkPermission(user.getId(),getLast().getEntityClass(), getWritePermission());
        getNext().resendVerificationMail(user);
    }


    @Override
    public AbstractUser update(AbstractUser update, Boolean full) throws EntityNotFoundException, BadEntityException {
        getSecurityChecker().checkPermission(update.getId(),getLast().getEntityClass(), getWritePermission());
        Optional<AbstractUser> byId = userRepository.findById(update.getId());
        RapidUtils.checkPresent(byId,update.getId(),update.getClass());
        LemonUserDto currentUser = LecwUtils.currentUser();
        RapidUtils.checkNotNull(currentUser,"Authenticated user not found");
        checkRoleChangingPermissions(byId.get(),update,currentUser);
        getProxyController().overrideDefaultExtension();
        return getNext().update(update,full);
    }

    /**
     * Check current Users role and decide what role adjustments he can make.
     */
    protected void checkRoleChangingPermissions(AbstractUser<?> old, AbstractUser<?> newUser, LemonUserDto currentUser) {
        // Good admin tries to edit
        if (currentUser.isGoodAdmin() &&
                !currentUser.getId().equals(old.getId().toString())) {
            return;
        }else {
            //no update of roles possible
            if (!old.getRoles().equals(newUser.getRoles())){
                throw new AccessDeniedException("Only Admin can update Roles");
            }
//            newUser.setRoles(old.getRoles());
        }
    }




    //todo did not find method... problems?
    ////@LogInteraction(level = LogInteraction.Level.TRACE)
//    @CalledByProxy
//    public void postAuthorizeProcessUser(AbstractUser user, AbstractUser result){
//        //only include email if user has write permission
//        if(!hasWritePermission(user)){
//            result.setEmail(null);
//        }
//    }


    @Override
    public void forgotPassword(String email) throws EntityNotFoundException {
        //check if write permission over user
        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
        if(byEmail.isPresent()){
            getSecurityChecker().checkPermission(byEmail.get().getId(),getLast().getEntityClass(), getWritePermission());
        }else {
            //let service throw more detailed exception
        }
        getNext().forgotPassword(email);
    }


    @Override
    public String changePassword(AbstractUser user,  ChangePasswordForm changePasswordForm) throws EntityNotFoundException {
//        LexUtils.ensureFound(user);
        getSecurityChecker().checkPermission(user.getId(),getLast().getEntityClass(), getWritePermission());
        return getNext().changePassword(user,changePasswordForm);
    }


    @Override
    public void requestEmailChange(AbstractUser user,  RequestEmailChangeForm emailChangeForm) throws EntityNotFoundException {
//        LexUtils.ensureFound(userRepository.findById(user));
        getSecurityChecker().checkPermission(user.getId(),getLast().getEntityClass(),getWritePermission());
        getNext().requestEmailChange(user,emailChangeForm);
    }


    @Override
    public AbstractUser changeEmail(AbstractUser user, String changeEmailCode) throws EntityNotFoundException {
//        getSecurityChecker().checkAuthenticated();
        getSecurityChecker().checkPermission(user.getId(),getLast().getEntityClass(),getWritePermission());
        return getNext().changeEmail(user,changeEmailCode);
    }


    @Override
    public String fetchNewToken(Optional expirationMillis, Optional optionalUsername) {
        getSecurityChecker().checkAuthenticated();
        return getNext().fetchNewToken(expirationMillis,optionalUsername);
    }

    @Override
    public Map<String, String> fetchFullToken(String authHeader) {
        getSecurityChecker().checkAuthenticated();
        return getNext().fetchFullToken(authHeader);
    }



//    private boolean hasWritePermission(AbstractUser user){
//        try {
//            getSecurityChecker().checkPermission(user.getId(),user.getClass(), getWritePermission());
//            return true;
//        }catch (AccessDeniedException e){
//            return false;
//        }
//    }

    //    @CalledByProxy
//    public void preAuthorizeFindByEmail(String email) throws EntityNotFoundException {
//        //only include email if user has write permission
//        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
//        EntityUtils.checkPresent(byEmail,"No User found with email: " +email);
//        getSecurityChecker().checkPermission(byEmail.get().getId(),byEmail.get().getClass(), getWritePermission());
//    }

    //this is done by mapping to specific dto
//    @CalledByProxy
//    public void postAuthorizeFindByEmail(String email, AbstractUser result){
//        //only include email if user has write permission
//        Optional<AbstractUser> byEmail = userRepository.findByEmail(email);
//        byEmail.ifPresent(new Consumer<>() {
//            @Override
//            public void accept(AbstractUser o) {
//                AbstractUser detached = JpaUtils.detach(o);
//                if(!hasWritePermission(detached)){
//                    result.setEmail(null);
//                }
//            }
//        });
//
//    }
}
