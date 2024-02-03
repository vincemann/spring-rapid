package com.github.vincemann.springrapid.core.service.context;

import lombok.NoArgsConstructor;

/**
 * service sub call scoped context used for sharing key-value pairs.
 * one context is created for each service call (with sub calls).
 * Lives until current (sub-) service call returns, then context of caller, if any, is restored.
 *
 * Is only used as a last resort for inter service component communication.
 */
@NoArgsConstructor
public class SubServiceCallContext extends AbstractServiceCallContext {


}
