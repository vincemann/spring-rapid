package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.service.filter.UrlExtension;
import org.springframework.data.domain.Sort;

public interface EntitySortingStrategy extends UrlExtension
{
    Sort getSort();
}
