package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;

public class ExtensionUtils {

    public static String name(Class<? extends AbstractServiceExtension> clazz){
        return clazz.getSimpleName();
    }
}
