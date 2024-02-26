package com.github.vincemann.springrapid.core.proxy.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
/**
 * annotation meta data containing info of all proxies defined for specific class.
 * For example:
 *
 * @DefineProxy(name="acl", extensions = ...)
 * @DefineProxy(name="secured", extensions = ...)
 * class MyService{
 *     ...
 * }
 *
 * stores MyService.class, singleton instance of MyService and proxy definitions
 */
public class ProxyMetaData {
    private Class<?> proxiedClass;
    private Object rootProxied;
    private List<DefineProxy> proxyDefinitions;
}
