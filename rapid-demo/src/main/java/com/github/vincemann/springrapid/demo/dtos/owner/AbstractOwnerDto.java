package com.github.vincemann.springrapid.demo.dtos.owner;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class AbstractOwnerDto extends IdentifiableEntityImpl<Long> {

    public AbstractOwnerDto(@Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Nullable @Size(min = 10, max = 10) String telephone) {
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    @Size(min=10,max=255)
    private String address;

    @Size(min=3,max=255)
    private String city;

    @Size(min=10,max=10)
    private String telephone;
}
