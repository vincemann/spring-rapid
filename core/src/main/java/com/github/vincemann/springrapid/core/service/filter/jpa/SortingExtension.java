package com.github.vincemann.springrapid.core.service.filter.jpa;

import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import org.springframework.beans.support.SortDefinition;
import org.springframework.data.domain.Sort;

public interface SortingExtension extends WebExtension<Object>
{
    SortDefinition getSort();
}
