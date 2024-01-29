package com.github.vincemann.springrapid.auth.sec.bruteforce;

public interface LoginAttemptService {

    public void loginSucceeded(String key);

    public void loginFailed(String key);

    public boolean isBlocked(String key);

    public void reset();

}
