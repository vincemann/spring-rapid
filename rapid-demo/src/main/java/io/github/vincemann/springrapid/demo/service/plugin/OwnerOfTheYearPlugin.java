package io.github.vincemann.springrapid.demo.service.plugin;

import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.core.config.layers.component.ServiceComponent;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;

import java.util.Optional;

@ServiceComponent
public class OwnerOfTheYearPlugin extends CrudServicePlugin<Owner,Long> {


    public void onAfterFindOwnerOfTheYear(Optional<Owner> owner){
        System.out.println("onAfterFindOwnerOfTheYear Hookmethod called");
        System.out.println("Owner of the year : " + owner);
    }
}
