package com.github.vincemann.springrapid.core.service.context;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores values over the lifetime of one service call.
 * When {@link com.github.vincemann.springrapid.core.proxy.ExtensionProxy} is called and first extension is called,
 * it is created and alive until this first method call returns (also alive for all sub service calls)
 *
 * For a shorter life time of one sub service call see {@link SubServiceCallContext}.
 */
@NoArgsConstructor
public class ServiceCallContext extends AbstractServiceCallContext{


}
