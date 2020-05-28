package io.github.vincemann.springrapid.compare.template;

import io.github.vincemann.springrapid.compare.refeq.RapidEqualsBuilder;

public interface OperationConfigurer {
    public boolean isEqual();
    public boolean isNotEqual();
    public void assertEqual();
    public RapidEqualsBuilder.MinimalDiff assertNotEqual();
}
