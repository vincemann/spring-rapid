package com.github.vincemann.springrapid.auth.dto.user;


public class ReadForeignUserDto {
    private String id;

    public ReadForeignUserDto(String id) {
        this.id = id;
    }

    public ReadForeignUserDto() {
    }

    @Override
    public String toString() {
        return "ReadForeignUserDto{" +
                "id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
