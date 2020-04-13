package io.github.vincemann.springrapid.coretest.util;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.util.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * Merge two Entities by copying all non null values from origin to dst.
 */
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
