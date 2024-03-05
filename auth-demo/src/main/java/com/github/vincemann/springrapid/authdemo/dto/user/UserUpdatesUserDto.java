package com.github.vincemann.springrapid.authdemo.dto.user;

import com.github.vincemann.springrapid.authdemo.service.ValidUsername;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserUpdatesUserDto {

    @NotNull
    @Positive
    private Long id;

    @NotBlank
    @ValidUsername
    private String name;
}
