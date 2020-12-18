package com.github.vincemann.springrapid.auth.security.bruteforce;

import java.util.concurrent.ExecutionException;

public interface LoginAttemptService {

    public void loginSucceeded(String key);

    public void loginFailed(String key);

    public boolean isBlocked(String key);

    public void reset();

}
