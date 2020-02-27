package io.github.vincemann.generic.crud.lib.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "web.active",havingValue = "true",matchIfMissing = true)
public @interface WebComponent {
}
