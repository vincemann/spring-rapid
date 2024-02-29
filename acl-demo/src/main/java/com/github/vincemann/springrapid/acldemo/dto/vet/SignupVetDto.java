package com.github.vincemann.springrapid.acldemo.dto.vet;

import com.github.vincemann.springrapid.acldemo.dto.abs.AbstractSignupDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupVetDto extends AbstractSignupDto {

    @Builder
    public SignupVetDto(String contactInformation, String password, String firstName, String lastName) {
        super(contactInformation, password, firstName, lastName);
    }

    @Override
    public String toString() {
        return "SignupVetDto{" +
                "contactInformation='" + getContactInformation() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }
}
