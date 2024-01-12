package com.github.vincemann.springrapid.core.event;

public enum UpdateCollectionType {
    ADD,
    REMOVE;

    // modify child or parent is not supported
    // just make new request for that
    // if entity is replaced, 2 events (remove, add) are called
}
