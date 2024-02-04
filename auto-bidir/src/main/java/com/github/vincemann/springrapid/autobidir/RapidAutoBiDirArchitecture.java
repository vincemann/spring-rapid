package com.github.vincemann.springrapid.autobidir;

import org.aspectj.lang.annotation.Pointcut;

public class RapidAutoBiDirArchitecture {

    @Pointcut("@annotation(com.github.vincemann.springrapid.autobidir.EnableAutoBiDir))")
    public void autoBiDirEnabled(){}
}
