package com.github.vincemann.springrapid.auth.dto.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//can only see id
public class FindForeignUserDto {
    private String id;

    @Override
    public String toString() {
        return "FindForeignUserDto{" +
                "id='" + id + '\'' +
                '}';
    }
}
