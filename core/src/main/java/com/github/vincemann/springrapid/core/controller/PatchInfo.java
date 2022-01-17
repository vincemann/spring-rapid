package com.github.vincemann.springrapid.core.controller;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
public class PatchInfo {
    // fields are from entity type not dto type -> fooIds already converted to foos
    Set<String> updatedFields = new HashSet<>();
    Set<String> removeSingleMembersFields = new HashSet<>();

    @Builder
    public PatchInfo(Set<String> updatedFields, Set<String> removeSingleMembersFields) {
        this.updatedFields = updatedFields;
        this.removeSingleMembersFields = removeSingleMembersFields;
    }
}
