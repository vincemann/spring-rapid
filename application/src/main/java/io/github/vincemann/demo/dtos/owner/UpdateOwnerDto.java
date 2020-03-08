package io.github.vincemann.demo.dtos.owner;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@ToString(callSuper = true)
public class UpdateOwnerDto extends BaseOwnerDto{

    @NotNull
    @Override
    public Long getId() {
        return super.getId();
    }

    @Builder
    public UpdateOwnerDto(@Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone) {
        super(address, city, telephone);
    }
}
