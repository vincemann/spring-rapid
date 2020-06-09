package com.github.vincemann.springrapid.demo.dtos;

import com.github.vincemann.springrapid.demo.model.Specialty;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentDto;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Validated
@ToString(callSuper = true)
public class VetDto extends PersonDto implements UniDirParentDto {


    @Builder
    public VetDto(@NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName, @Nullable @Size(max = 20) Set<Long> specialtyIds) {
        super(firstName, lastName);
        if(specialtyIds==null){
            this.specialtyIds=new HashSet<>();
        }else {
            this.specialtyIds = specialtyIds;
        }
    }


    @Nullable
    @Size(max = 20)
    @UniDirChildIdCollection(Specialty.class)
    private Set<Long> specialtyIds = new HashSet<>();
}
