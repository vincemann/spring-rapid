package com.github.vincemann.springrapid.sync.dto;

import lombok.*;

/**
 * Sync status response is Set of objects of this class. i.E.: [42a,3u,10u,4r]
 * id concatenated with status char a=added, u=updated, r=removed
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EntitySyncStatus {

    public static enum Status{
        ADDED,
        REMOVED,
        UPDATED
    }

    private String id;
    private Status status;


    @Builder
    public EntitySyncStatus(String id, Status status) {
        this.id = id;
        this.status = status;
    }

    public EntitySyncStatus(String id, Character status) {
        this.id = id;
        this.status = convert(status);
    }




    public static Status convert(Character character){
        switch (character){
            case 'u':
                return Status.UPDATED;
            case 'a':
                return Status.ADDED;
            case 'r':
                return Status.REMOVED;
        }
        throw new IllegalArgumentException("invalid status char: " + character);
    }

    public static Character convert(Status status){
        switch (status){
            case UPDATED:
                return 'u';
            case ADDED:
                return 'a';
            case REMOVED:
                return 'r';
        }
        throw new IllegalArgumentException("unknown status " + status);
    }
}
