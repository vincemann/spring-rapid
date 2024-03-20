package com.github.vincemann.springrapid.core.util;

import com.github.hervian.reflection.Types;

import javax.validation.Validator;

public class ValidationUtils {

    public static void validate(Validator validator,Object entity, Types.Supplier<?> supplier){
        validator.validateProperty(entity,MethodNameUtil.propertyName(supplier));
    }
}
