package com.github.vincemann.springrapid.acl.framework;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LenientPermissionGrantingStrategyTest {

    @Mock
    AuditLogger auditLogger;
    @InjectMocks
    LenientPermissionGrantingStrategy permissionGrantingStrategy;
    @Mock
    AccessControlEntry entry;
    Permission readPermission;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(entry.isGranting())
                .thenReturn(true);
        readPermission = BasePermission.READ;
    }

    @Test
    public void permissionMatches_but_nonGrantingAce_shouldDeny(){
        when(entry.isGranting())
                .thenReturn(false);
        when(entry.getPermission())
                .thenReturn(readPermission);
        boolean granted = permissionGrantingStrategy.isGranted(entry, readPermission);

        Assertions.assertFalse(granted);
    }

    @Test
    public void permissionMatches_shouldAllow(){
        when(entry.getPermission())
                .thenReturn(readPermission);

        boolean granted = permissionGrantingStrategy.isGranted(entry, readPermission);

        Assertions.assertTrue(granted);


    }

    @Test
    public void requestLowerPermission_shouldAllow(){
        when(entry.getPermission())
                .thenReturn(BasePermission.ADMINISTRATION);

        boolean granted = permissionGrantingStrategy.isGranted(entry, readPermission);

        Assertions.assertTrue(granted);
    }


    @Test
    public void requestHigherPermission_shouldDeny(){
        when(entry.getPermission())
                .thenReturn(readPermission);

        boolean granted = permissionGrantingStrategy.isGranted(entry, BasePermission.CREATE);

        Assertions.assertFalse(granted);
    }





}