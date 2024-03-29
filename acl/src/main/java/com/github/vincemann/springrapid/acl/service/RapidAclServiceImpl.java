package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.acl.util.AclUtils;
import com.github.vincemann.springrapid.core.model.IdAwareEntity;
import com.github.vincemann.springrapid.core.sec.AuthorizationUtils;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.sec.Roles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.acl.service.AceFilters.*;

/**
 * Api for managing acl data.
 * All methods executed as system user with role {@link Roles#SYSTEM}
 * -> make sure only called by trusted code internally
 *
 * Adds some features for conditional inheritance and provides api for simple use cases
 */
@Transactional
public class RapidAclServiceImpl implements RapidAclService {

    private final Log log = LogFactory.getLog(getClass());

    private MutableAclService aclService;


    public RapidAclServiceImpl(MutableAclService aclService) {
        this.aclService = aclService;
    }

    @Override
    public void grantRolePermissionForEntity(String role, IdAwareEntity<?> entity, Permission... permissions) {
        RapidSecurityContext.executeAsSystemUser( () -> {
            final Sid sid = new GrantedAuthoritySid(role);
            addPermissionsForSid(entity, sid, permissions);
        });
    }

    @Override
    public void revokeRolesPermissionForEntity(String role, IdAwareEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            final Sid sid = new GrantedAuthoritySid(role);
            deletePermissionForSid(entity, sid, ignoreNotFound, permissions);
        });
    }


    @Override
    public void grantUserPermissionForEntity(String user, IdAwareEntity<?> entity, Permission... permissions) {
        RapidSecurityContext.executeAsSystemUser( () -> {
            final Sid sid = new PrincipalSid(user);
            addPermissionsForSid(entity, sid, permissions);
        });
    }

    @Override
    public void revokeAuthenticatedPermissionForEntity(IdAwareEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        revokeUsersPermissionForEntity(findAuthenticatedName(), entity,ignoreNotFound, permissions);
    }

    @Override
    public void revokeUsersPermissionForEntity(String user, IdAwareEntity<?> entity, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            final Sid sid = new PrincipalSid(user);
            deletePermissionForSid(entity, sid, ignoreNotFound, permissions);
        });
    }

    @Override
    public void grantAuthenticatedPermissionForEntity(IdAwareEntity<?> entity, Permission... permissions) {
        grantUserPermissionForEntity(findAuthenticatedName(), entity, permissions);
    }


    @Override
    public void deleteAclOfEntity(Class<? extends IdAwareEntity> clazz, Serializable id, boolean deleteCascade) {
        RapidSecurityContext.executeAsSystemUser( () -> {
            ObjectIdentity oi = new ObjectIdentityImpl(clazz, id);
            aclService.deleteAcl(oi, deleteCascade);
        });

    }

    @Override
    public void deleteAclOfEntity(IdAwareEntity<?> entity, boolean deleteCascade) {
        RapidSecurityContext.executeAsSystemUser( () -> {
            ObjectIdentity oi = new ObjectIdentityImpl(entity.getClass(), entity.getId());
            aclService.deleteAcl(oi, deleteCascade);
        });
    }

    protected String findAuthenticatedName() {
        AuthorizationUtils.assertAuthenticated();
        return RapidSecurityContext.getName();
    }

    protected MutableAcl findAcl(ObjectIdentity oi) throws AclNotFoundException {
        try {
            return (MutableAcl) aclService.readAclById(oi);
        } catch (NotFoundException nfe) {
            throw new AclNotFoundException("Acl not found for oi: " + AclUtils.objectIdentityToString(oi));
        }
    }

    protected MutableAcl findOrCreateAcl(ObjectIdentity oi) {
        try {
            return (MutableAcl) aclService.readAclById(oi);
        } catch (NotFoundException nfe) {
            if (log.isDebugEnabled())
                log.debug("Acl not found for oi: " + AclUtils.objectIdentityToString(oi) + ", creating new");
           return aclService.createAcl(oi);
        }
    }

    @Override
    public void inheritAces(IdAwareEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            AclCascadeInfo info = getParentInfo(parent, infos);
            if (info == null)
                return;
            Predicate filter = info.getSourceFilter();
            if (filter != null) {
                if (!filter.test(parent)) {
                    return;
                }
            }
            Collection<? extends IdAwareEntity> children = getAclChildren(parent, info);
            for (IdAwareEntity<?> child : children) {
                copyParentAces(child, parent, info.getAceFilter());
                // recursion
                inheritAces(child, infos);
            }
        });
    }

    @Override
    public void removeAces(IdAwareEntity<?> parent, List<AclCascadeInfo> infos) throws AclNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            AclCascadeInfo info = getParentInfo(parent, infos);
            if (info == null)
                return;
            Predicate filter = info.getSourceFilter();
            if (filter != null) {
                if (!filter.test(parent)) {
                    return;
                }
            }
            Collection<? extends IdAwareEntity> children = getAclChildren(parent, info);
            for (IdAwareEntity child : children) {
                // cannot work with check if all acl entries deleted, bc I cant know how many should be deleted
                removeAces(child, info.getAceFilter());
                // recursion
                removeAces(child, infos);
            }
        });
    }

    @Override
    public void inheritAces(Collection<? extends IdAwareEntity<?>> parents, List<AclCascadeInfo> infos) throws AclNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            for (IdAwareEntity<?> parent : parents) {
                inheritAces(parent, infos);
            }
        });
    }

    protected Collection<? extends IdAwareEntity> getAclChildren(IdAwareEntity<?> parent, AclCascadeInfo info) {
        Collection<? extends IdAwareEntity> children = info.getTargetCollection(parent);
        Predicate filter = info.getTargetFilter();
        if (filter != null)
            return (Collection<? extends IdAwareEntity>) children.stream().filter(filter).collect(Collectors.toList());
        else
            return children;
    }

    @Override
    public void updateEntriesInheriting(boolean value, IdAwareEntity<?> child, IdAwareEntity<?> parent) throws AclNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            ObjectIdentity childOi = new ObjectIdentityImpl(child.getClass(), child.getId());
            ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());

            MutableAcl childAcl = findOrCreateAcl(childOi);
            MutableAcl parentAcl = findAcl(parentOi);

            if (value) {
                childAcl.setParent(parentAcl);
            } else {
                childAcl.setParent(null);
            }
            childAcl.setEntriesInheriting(value);
            aclService.updateAcl(childAcl);
        });
    }

    protected AclCascadeInfo getParentInfo(IdAwareEntity<?> parent, List<AclCascadeInfo> infos) {
        // first info is always parent
        Optional<AclCascadeInfo> info = infos.stream()
                .filter(i -> i.matches(parent.getClass()))
                .findFirst();
        return info.orElse(null);
//            throw new IllegalArgumentException("Cannot find matching info from supplied info list");
    }

    @Override
    public void copyParentAces(IdAwareEntity<?> child, IdAwareEntity<?> parent, Predicate<AccessControlEntry> aceFilter) throws AclNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            ObjectIdentity childOi = new ObjectIdentityImpl(child.getClass(), child.getId());
            ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());

            MutableAcl childAcl = findOrCreateAcl(childOi);
            MutableAcl parentAcl = findAcl(parentOi);

            if (log.isTraceEnabled()) {
                log.trace("child acl of entity before copying: ");
//            log.debug(AclUtils.aclToString(childAcl));
                AclUtils.logAcl(childAcl, log);
            }

            if (log.isTraceEnabled()) {

                log.trace("inheriting from parent acl: ");
                AclUtils.logAcl(parentAcl, log);
            }


            copyMatchingAces(parentAcl, childAcl, aceFilter);


            if (log.isDebugEnabled()) {
                log.debug("updated acl:");
                AclUtils.logAcl(childAcl, log);
            }
            aclService.updateAcl(childAcl);
        });
    }

    @Override
    public void copyParentAces(IdAwareEntity<?> child, IdAwareEntity<?> parent, AceFilterMapping... filterMappings) throws AclNotFoundException {
        RapidSecurityContext.executeAsSystemUser( () -> {
            ObjectIdentity childOi = new ObjectIdentityImpl(child.getClass(), child.getId());
            ObjectIdentity parentOi = new ObjectIdentityImpl(parent.getClass(), parent.getId());

            MutableAcl childAcl = findOrCreateAcl(childOi);
            MutableAcl parentAcl = findAcl(parentOi);

            if (log.isTraceEnabled()) {
                log.trace("child acl of entity before copying: ");
//            log.debug(AclUtils.aclToString(childAcl));
                AclUtils.logAcl(childAcl, log);
            }

            if (log.isTraceEnabled()) {

                log.trace("inheriting from parent acl: ");
                AclUtils.logAcl(parentAcl, log);
            }


            copyMatchingAces(parentAcl, childAcl, filterMappings);


            if (log.isDebugEnabled()) {
                log.debug("updated acl:");
                AclUtils.logAcl(childAcl, log);
            }
            aclService.updateAcl(childAcl);
        });
    }

    @Override
    public int removeAces(IdAwareEntity<?> target, Predicate<AccessControlEntry> aceFilter) throws AclNotFoundException {
        return RapidSecurityContext.executeAsSystemUser( () -> {
            ObjectIdentity oi = new ObjectIdentityImpl(target.getClass(), target.getId());

            MutableAcl acl = findAcl(oi);

            if (log.isTraceEnabled()) {
                log.debug("acl of entity before removing: ");
                AclUtils.logAcl(acl, log);
            }

            int removed = removeAces(acl, aceFilter);

            MutableAcl updated = aclService.updateAcl(acl);

            if (log.isDebugEnabled()) {
                log.debug("updated acl: ");
                AclUtils.logAcl(updated, log);
            }
            return removed;
        });
    }

    /**
     * Copy all aces from parentAcl to childAcl that match aceFilter
     * and are not already present in childAcl.
     */
    protected void copyMatchingAces(MutableAcl parentAcl, MutableAcl childAcl, Predicate<AccessControlEntry> aceFilter) {
        for (AccessControlEntry ace : parentAcl.getEntries()) {
            if (aceFilter.test(ace)) {
                if (!AclUtils.isAcePresent(ace, childAcl))
                    childAcl.insertAce(childAcl.getEntries().size(), ace.getPermission(), ace.getSid(), ace.isGranting());
            }
        }
    }

    protected void copyMatchingAces(MutableAcl parentAcl, MutableAcl childAcl, AceFilterMapping... filterMappings) {
        for (AccessControlEntry ace : parentAcl.getEntries()) {
            for (AceFilterMapping filterMapping : filterMappings) {
                if (filterMapping.getFilter().test(ace)){
                    if (!AclUtils.isAcePresent(ace, childAcl)){
                        Permission permission = filterMapping.getPermission() == null ? ace.getPermission() : filterMapping.getPermission();
                        Sid sid = (filterMapping.getSid() == null) ? ace.getSid() : filterMapping.getSid();
                        childAcl.insertAce(childAcl.getEntries().size(), permission, sid, ace.isGranting());
                    }
                }
            }
        }
    }


    protected void addPermissionsForSid(IdAwareEntity<?> targetObj, Sid sid, Permission... permissions) {
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        if (log.isDebugEnabled())
            log.debug("sid: " + AclUtils.sidToString(sid) + " will gain permissions: "
                    + AclUtils.permissionsToString(permissions)
                    + " over entity: " + AclUtils.objectIdentityToString(oi));

        MutableAcl acl = findOrCreateAcl(oi);
        if (log.isTraceEnabled()) {
            log.trace("acl of entity before adding:");
            AclUtils.logAcl(acl, log);
        }

        for (Permission permission : permissions) {
            if (AclUtils.isAcePresent(permission, sid, acl))
                log.warn("ace is already present in acl: " + AclUtils.sidToString(sid) + " - " + AclUtils.permissionToString(permission) + ". Skipping");
            else
                acl.insertAce(acl.getEntries().size(), permission, sid, true);
        }
        MutableAcl updated = aclService.updateAcl(acl);

        if (log.isDebugEnabled()) {
            log.debug("updated acl:");
            AclUtils.logAcl(updated, log);
        }
    }

    protected void deletePermissionForSid(IdAwareEntity<?> targetObj, Sid sid, boolean ignoreNotFound, Permission... permissions) throws AclNotFoundException, AceNotFoundException {
        if (log.isDebugEnabled())
            log.debug("sid: " + AclUtils.sidToString(sid) + " will loose permission: " + AclUtils.permissionsToString(permissions) + " over entity: " + targetObj);

        boolean principalsOnly = !(sid instanceof GrantedAuthoritySid);

        Predicate<AccessControlEntry> filter = sids(sid)
                .and(permissions(permissions));
        if (principalsOnly)
            filter = filter.and(principalsOnly());

        int removed = removeAces(targetObj, filter);

        if (removed != permissions.length && !ignoreNotFound)
            throw new AceNotFoundException("Cant remove permissions: " + AclUtils.permissionsToString(permissions) + " for sid: " + AclUtils.sidToString(sid) + " on target: " + targetObj + ", bc not all matching aces found");
    }

    protected int removeAces(MutableAcl acl, Predicate<AccessControlEntry> aceFilter) {
        // create new list here, so I dont create concurrent modification down below - there is no exception, but it does not seem safe
        List<AccessControlEntry> aces = new ArrayList<>(acl.getEntries());
        Iterator<AccessControlEntry> aceIterator = aces.iterator();
        int removed = 0;
        int index = 0;
        while (aceIterator.hasNext()) {
            AccessControlEntry ace = aceIterator.next();
            if (aceFilter.test(ace)) {
                acl.deleteAce(index - removed);
                removed++;
            }
            index++;
        }
        return removed;
    }


    @Autowired
    public void setAclService(MutableAclService aclService) {
        this.aclService = aclService;
    }

}