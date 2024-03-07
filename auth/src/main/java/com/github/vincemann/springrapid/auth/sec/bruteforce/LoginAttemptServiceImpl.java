package com.github.vincemann.springrapid.auth.sec.bruteforce;

import com.github.vincemann.springrapid.auth.AuthProperties;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Keeps track of how many invalid login attempts each ip has.
 */
public class LoginAttemptServiceImpl implements LoginAttemptService{

    private LoadingCache<String, Integer> attemptsCache;
    private AuthProperties authProperties;

    public LoginAttemptServiceImpl() {
        super();
        attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= authProperties.getMaxLoginAttempts();
        } catch (ExecutionException e) {
            return false;
        }
    }

    @Autowired
    public void setAuthProperties(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public void reset() {
        attemptsCache.invalidateAll();
    }
}
