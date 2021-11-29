package com.github.vincemann.springrapid.coredemo.model.abs;

import lombok.*;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
public abstract class Person extends IdentifiableEntityImpl<Long> {
    @Column(name = "first_name")
    @NotBlank
    private String firstName;

    @Unique
    @NotBlank
    @Column(name = "last_name")
    private String lastName;
}
