package com.github.vincemann.springrapid.sync.model;

public enum SyncStatus {

    ADDED('a'),
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
