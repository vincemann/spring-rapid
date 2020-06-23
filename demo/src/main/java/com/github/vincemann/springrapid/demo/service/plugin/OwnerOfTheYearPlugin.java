package com.github.vincemann.springrapid.demo.service.plugin;

import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.proxy.CrudServicePlugin;

import java.util.Optional;

@ServiceComponent
public class OwnerOfTheYearPlugin extends CrudServicePlugin<Owner,Long> {


    public void onAfterFindOwnerOfTheYear(Optional<Owner> owner){
        System.out.println("onAfterFindOwnerOfTheYear Hookmethod called");
        System.out.println("Owner of the year : " + owner);
    }
}
