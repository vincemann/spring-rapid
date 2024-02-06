package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface WebExtensionParser {

    List<WebExtension<?>> parse(HttpServletRequest request, Set<WebExtension> extensions, WebExtensionType type) throws BadEntityException;
}
