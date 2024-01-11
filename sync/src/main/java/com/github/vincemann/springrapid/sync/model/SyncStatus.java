package com.github.vincemann.springrapid.sync.model;

public enum SyncStatus {

    // added status makes no sense, user gets either update or deleted, and if he did not have entity yet, he needs to fetch as well
    // and can derive the info that is has been added (if even relevant)
//    ADDED('a'),
    REMOVED('r'),
    UPDATED('u');

    private char status;

    SyncStatus(char statusChar){
        this.status = statusChar;
    }

    public static SyncStatus convert(Character character){
        if (character == null) {
            return null;
        }
        for (SyncStatus status : SyncStatus.values()) {
            if (status.status == character) {
                return status;
            }
        }
        throw new IllegalArgumentException("No corresponding SyncStatus for character: " + character);
    }

    public static Character convert(SyncStatus status){
        if (status == null) {
            return null;
        }
        return status.status;
    }
}
