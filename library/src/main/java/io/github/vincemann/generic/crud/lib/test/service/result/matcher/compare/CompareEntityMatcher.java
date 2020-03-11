package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CompareEntityMatcher {
    private CompareEntityMatcherContext compareEntityContext;
}