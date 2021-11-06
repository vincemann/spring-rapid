package com.github.vincemann.springrapid.coredemo.service;

import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.coredemo.model.LazyItem;
import com.github.vincemann.springrapid.coredemo.repo.LazyItemRepository;

public interface LazyItemService extends CrudService<LazyItem,Long> {
}
