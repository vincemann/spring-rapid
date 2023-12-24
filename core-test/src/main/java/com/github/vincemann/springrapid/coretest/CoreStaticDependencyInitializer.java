package com.github.vincemann.springrapid.coretest;

import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import com.github.vincemann.springrapid.core.util.JpaUtils;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

public class CoreStaticDependencyInitializer implements StaticDependencyInitializer{

    private EntityManager entityManager;
    private CrudServiceLocator crudServiceLocator;

    @Autowired(required = false)
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired(required = false)
    public void setCrudServiceLocator(CrudServiceLocator crudServiceLocator) {
        this.crudServiceLocator = crudServiceLocator;
    }

    @Override
    public void initializeStaticDependencies() {
        // beans might be null when only web layer is tested
        if (crudServiceLocator != null){
            EntityLocator.setCrudServiceLocator(crudServiceLocator);
        }
        if (entityManager != null){
            JpaUtils.setEntityManager(entityManager);
            LazyToStringUtil.setEntityManager(entityManager);
        }
    }
}
