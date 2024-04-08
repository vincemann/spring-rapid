package com.github.vincemann.springrapid.auth;

import java.io.Serializable;

public interface IdAware<Id extends Serializable> {
    Id getId();
}
