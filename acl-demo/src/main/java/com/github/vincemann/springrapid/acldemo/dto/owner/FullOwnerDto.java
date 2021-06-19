package com.github.vincemann.springrapid.acldemo.dto.owner;

import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildId;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class FullOwnerDto extends AbstractOwnerDto {
    public static final String DIRTY_SECRET = "can you see this?";


    private String dirtySecret = DIRTY_SECRET;


    @Builder
    public FullOwnerDto(@Size(min = 2, max = 20) String firstName, @Size(min = 2, max = 20) String lastName, @Size(min = 10, max = 255) String address, @Size(min = 3, max = 255) String city, @Size(min = 10, max = 10) String telephone, Set<String> hobbies, Set<Long> petIds, String dirtySecret) {
        super(firstName, lastName, address, city, telephone, hobbies, petIds);
        this.dirtySecret = dirtySecret;
    }
}
