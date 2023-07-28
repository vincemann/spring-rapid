package com.github.vincemann.springrapid.authdemo.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.dto.SignupDto;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MySignupDto extends SignupDto {

    @JsonView(UserVerifyUtils.SignupInput.class)
    @NotBlank(message = "{blank.name}"/*, groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.UpdateValidation.class}*/)
    private String name;

    private List<String> roles = new ArrayList<>();
    public MySignupDto(String contactInformation, String password, @NotBlank(message = "{blank.name}", groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.UpdateValidation.class}) String name, List<String> roles) {
        super(contactInformation, password);
        this.name = name;
        if (roles != null)
            this.roles = roles;
    }
}
