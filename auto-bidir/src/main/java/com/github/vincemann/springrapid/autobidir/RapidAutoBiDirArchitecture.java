package com.github.vincemann.springrapid.autobidir;

import org.aspectj.lang.annotation.Pointcut;

public class RapidAutoBiDirArchitecture {

    // wont work, always need to check for unproxied instance and then check meta data
//    @Pointcut("@annotation(com.github.vincemann.springrapid.autobidir.EnableAutoBiDir))")
//    public void autoBiDirEnabled(){}
}
