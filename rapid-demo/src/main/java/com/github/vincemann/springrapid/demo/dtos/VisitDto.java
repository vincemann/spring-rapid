package com.github.vincemann.springrapid.demo.dtos;

import com.github.vincemann.springrapid.demo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.uniDir.UniDirParentId;
import com.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChild;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class VisitDto extends IdentifiableEntityImpl<Long> implements UniDirChild {

    @Nullable
    @UniDirParentId(Pet.class)
    private Long petId;

    @NotNull
    private LocalDate date;

    @NotBlank
    @Size(max = 255)
    private String description;
}
