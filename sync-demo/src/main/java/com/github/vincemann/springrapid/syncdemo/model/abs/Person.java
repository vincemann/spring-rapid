package com.github.vincemann.springrapid.syncdemo.model.abs;

import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
public abstract class Person extends AuditingEntity<Long> {
    @Column(name = "first_name")
    @NotBlank
    private String firstName;

    @Unique
    @NotBlank
    @Column(name = "last_name")
    private String lastName;
}
