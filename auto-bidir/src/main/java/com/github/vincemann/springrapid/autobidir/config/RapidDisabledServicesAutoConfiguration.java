package com.github.vincemann.springrapid.autobidir.config;

import com.github.vincemann.springrapid.autobidir.AutoBiDirUtils;
import com.github.vincemann.springrapid.autobidir.DisableAutoBiDir;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.core.util.RepositoryUtil;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.util.AopTestUtils;

import java.util.Map;

@Configuration
public class RapidDisabledServicesAutoConfiguration implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String,Object> beans = applicationContext.getBeansWithAnnotation(DisableAutoBiDir.class);
        for (Map.Entry<String, Object> nameBeanEntry : beans.entrySet()) {
            Object bean = AopTestUtils.getUltimateTargetObject(nameBeanEntry.getValue());
//            AutoBiDirUtils.bannedBeans.add(bean);
            if (bean instanceof SimpleJpaRepository){
                AutoBiDirUtils.bannedRepoEntityTypes.add(RepositoryUtil.getRepoType(((SimpleJpaRepository<?, ?>) bean)));
            }else if (bean instanceof CrudService){
                AutoBiDirUtils.bannedRepoEntityTypes.add(((CrudService<?, ?>) bean).getEntityClass());
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
