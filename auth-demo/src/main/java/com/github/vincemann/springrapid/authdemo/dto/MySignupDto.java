package com.github.vincemann.springrapid.authdemo.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.dto.SignupDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class MySignupDto extends SignupDto {

    @NotBlank(message = "{blank.name}")
    private String name;

    public MySignupDto(String contactInformation, String password, @NotBlank(message = "{blank.name}") String name, Set<String> roles) {
        super(contactInformation, password,roles);
        this.name = name;
    }
}
