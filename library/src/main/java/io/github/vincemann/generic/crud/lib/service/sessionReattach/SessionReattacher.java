package io.github.vincemann.generic.crud.lib.service.sessionReattach;

public interface SessionReattacher {

    /**
     * Attaches a single entity to the current Session
     * @param entity
     * @return
     */
    public boolean attachToCurrentSession(Object entity);
}
