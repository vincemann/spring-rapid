package com.github.vincemann.springrapid.core.service.context;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * service call scoped context used for caching and sharing key-value pairs.
 * one context is created for most outer service call.
 * Lives until outer service call returns.
 * for more narrow scope see {@link SubServiceCallContext}
 */
@NoArgsConstructor
public class ServiceCallContext extends AbstractServiceCallContext{



}
