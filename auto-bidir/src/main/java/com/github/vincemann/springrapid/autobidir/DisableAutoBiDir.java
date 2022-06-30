package com.github.vincemann.springrapid.autobidir;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** service needs to be annoteted with this annotation.
 * Repo will be automatically disabled as well after first call.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableAutoBiDir {
}
