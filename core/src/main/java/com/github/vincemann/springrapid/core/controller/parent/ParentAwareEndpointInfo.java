package com.github.vincemann.springrapid.core.controller.parent;

import com.github.vincemann.springrapid.core.controller.CrudEndpointInfo;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ParentAwareEndpointInfo extends CrudEndpointInfo {
    private boolean exposeFindAllOfParent =true;

}
