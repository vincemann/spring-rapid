package com.github.vincemann.springrapid.acl;

import java.io.Serializable;

public interface IdAware<Id extends Serializable> {
    Id getId();
}
