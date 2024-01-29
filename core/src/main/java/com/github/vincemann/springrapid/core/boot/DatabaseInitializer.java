package com.github.vincemann.springrapid.core.boot;


import lombok.Getter;
import lombok.Setter;

/**
 * Populates database with needed Data.
 * Usually called after application context construction or before Tests.
 */
@Getter
@Setter
public abstract class DatabaseInitializer {
    public abstract void init();
}
