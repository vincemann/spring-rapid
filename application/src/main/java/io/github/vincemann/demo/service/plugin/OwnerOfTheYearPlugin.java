package io.github.vincemann.demo.service.plugin;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePlugin;
import org.springframework.stereotype.Component;

import java.util.Optional;

public class OwnerOfTheYearPlugin extends CrudServicePlugin<Owner,Long> {


    public void onAfterFindOwnerOfTheYear(Optional<Owner> owner){
        System.out.println("onAfterFindOwnerOfTheYear Hookmethod called");
        System.out.println("Owner of the year : " + owner);
    }
}
