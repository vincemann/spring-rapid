package com.github.vincemann.springrapid.autobidir;


import java.lang.annotation.*;

/** service needs to be annoteted with this annotation.
 * Repo will be automatically disabled as well after first call.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisableAutoBiDir {
}
