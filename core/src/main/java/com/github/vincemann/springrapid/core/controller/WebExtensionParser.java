package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface WebExtensionParser {

    void registerExtensions(WebExtension<?>... extensions);

    List<WebExtension<?>> parse(HttpServletRequest request, WebExtensionType type) throws BadEntityException;
}
