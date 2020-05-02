package io.github.vincemann.springrapid.core.bootstrap;


import lombok.Getter;
import lombok.Setter;

/**
 * Populates database with needed Data.
 * Usually called after application context construction or before Tests.
 */
@Getter
@Setter
public abstract class Initializer {
    public abstract void init();
}
