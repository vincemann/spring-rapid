package com.github.vincemann.springrapid.core.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Field;

public class RepositoryUtil {

    // todo is impl specific
    public static Class<?> getRepoType(JpaRepository simpleJpaRepository) {
        try {
            SimpleJpaRepository repo = AopTestUtils.getUltimateTargetObject(simpleJpaRepository);
            Field entityInformationField = ReflectionUtils.findField(SimpleJpaRepository.class, field -> field.getName().equals("entityInformation"));
            entityInformationField.setAccessible(true);
            JpaEntityInformation entityInformation = ((JpaEntityInformation) entityInformationField.get(repo));
            return entityInformation.getJavaType();
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Need SimpleRepository as impl for this util method");
        }
    }
}
