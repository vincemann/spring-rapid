package com.github.vincemann.springrapid.entityrelationship.controller;

import com.github.vincemann.springrapid.core.controller.rapid.mergeUpdate.MergeUpdateStrategyImpl;

import java.lang.reflect.Field;
import java.util.Map;



public class IdAwareMergeUpdateStrategy extends MergeUpdateStrategyImpl {
    public static final String ID_SUFFIX = "Id";
    public static final String IDS_SUFFIX = "Ids";


    @Override
    protected Field resolve(String property, Map<String, Field> entityFields) {
        Field entityField = entityFields.get(property);
        if (entityField == null) {
            if (property.endsWith(ID_SUFFIX)) {
                entityField = entityFields.get(property.substring(0, property.length() - ID_SUFFIX.length()));
            }
            else if (property.endsWith(IDS_SUFFIX)) {
                entityField = entityFields.get(property.substring(0, property.length() - IDS_SUFFIX.length()) +"s");
            }
        }
        return entityField;
    }


}
