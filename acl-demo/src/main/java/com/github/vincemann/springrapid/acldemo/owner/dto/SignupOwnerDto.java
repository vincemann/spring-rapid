package com.github.vincemann.springrapid.acldemo.owner.dto;

import com.github.vincemann.springrapid.acldemo.AbstractSignupDto;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SignupOwnerDto extends AbstractSignupDto {

    @NotEmpty
    @Size(min = 10, max = 255)
    private String address;

    @NotEmpty
    @Size(min=3,max=255)
    private String city;

    @Nullable
    @Size(min = 10, max = 10)
    private String telephone;

    @Nullable
    private Set<String> hobbies = new HashSet<>();


    @Builder
    public SignupOwnerDto(String contactInformation, String password, String firstName, String lastName, String address, String city, @Nullable String telephone, @Nullable Set<String> hobbies) {
        super(contactInformation, password, firstName, lastName);
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        this.hobbies = hobbies;
    }

    public SignupOwnerDto(Owner owner){
        this(
                owner.getContactInformation(),
                owner.getPassword(),
                owner.getFirstName(),
                owner.getLastName(),
                owner.getAddress(),
                owner.getCity(),
                owner.getTelephone(),
                owner.getHobbies()
        );
    }

    @Override
    public String toString() {
        return "SignupOwnerDto{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                ", hobbies=" + hobbies +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                '}';
    }
}
