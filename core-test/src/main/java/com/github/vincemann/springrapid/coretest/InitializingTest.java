package com.github.vincemann.springrapid.coretest;

import org.springframework.test.context.TestExecutionListeners;

/**
 * @see InitializingTestExecutionListener
 */
@TestExecutionListeners(listeners = InitializingTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class InitializingTest {

}
