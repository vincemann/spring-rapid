package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtils;
import io.github.vincemann.generic.crud.lib.util.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;

public class CopyNonNullValuesEntityMerger {

    public static IdentifiableEntity merge(IdentifiableEntity origin, IdentifiableEntity dst) {
        try {
            BeanUtilsBean beanUtilsBean = NullAwareBeanUtilsBean.getInstance();
            Object clone = beanUtilsBean.cloneBean(dst);
            NullAwareBeanUtils.copyProperties(clone,origin);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dst;
    }
}
