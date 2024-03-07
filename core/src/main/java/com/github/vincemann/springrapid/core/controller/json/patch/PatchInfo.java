package com.github.vincemann.springrapid.core.controller.json.patch;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;


public class PatchInfo {
    // fields are from entity type not dto type -> fooIds already converted to foos
    Set<String> updatedFields = new HashSet<>();
    Set<String> removeSingleMembersFields = new HashSet<>();

    public PatchInfo(Set<String> updatedFields, Set<String> removeSingleMembersFields) {
        this.updatedFields = updatedFields;
        this.removeSingleMembersFields = removeSingleMembersFields;
    }

    public PatchInfo() {
    }

    public Set<String> getAllUpdatedFields() {
        Set<String> allUpdatedFields = Sets.newHashSet(updatedFields);
        allUpdatedFields.addAll(removeSingleMembersFields);
        return allUpdatedFields;
    }

    public Set<String> getUpdatedFields() {
        return updatedFields;
    }

    public Set<String> getRemoveSingleMembersFields() {
        return removeSingleMembersFields;
    }
}
