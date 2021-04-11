package com.github.vincemann.springrapid.auth.security.bruteforce;

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
public class RapidLoginAttemptService implements LoginAttemptService{

    private LoadingCache<String, Integer> attemptsCache;
    private AuthProperties authProperties;

    public RapidLoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
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
        if (!authProperties.isLoginBruteforceProtection()){
            return false;
        }
        try {
            return attemptsCache.get(key) >= authProperties.getMaxLoginAttempts();
        } catch (ExecutionException e) {
            return false;
        }
    }

    @Autowired
    public void injectAuthProperties(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public void reset() {
        attemptsCache.invalidateAll();
    }
}
