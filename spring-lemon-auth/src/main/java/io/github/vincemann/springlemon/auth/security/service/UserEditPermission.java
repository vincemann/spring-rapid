package io.github.vincemann.springlemon.auth.security.service;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission(#user, 'WRITE')")
public @interface UserEditPermission {}