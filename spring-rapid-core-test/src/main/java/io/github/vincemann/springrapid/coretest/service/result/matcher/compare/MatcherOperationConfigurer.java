package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;


import io.github.vincemann.ezcompare.RapidEqualsBuilder;
import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;

public interface MatcherOperationConfigurer  {
    public interface DiffAssertion{
        public void go(RapidEqualsBuilder.Diff diff);
    }
    //menu options
    public ServiceResultMatcher assertDiff(DiffAssertion assertion);
    public ServiceResultMatcher assertEqual();
    public ServiceResultMatcher assertNotEqual();
}
