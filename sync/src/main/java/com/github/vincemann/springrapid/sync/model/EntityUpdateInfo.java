package com.github.vincemann.springrapid.sync.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import lombok.*;

import java.util.Date;


@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class EntityUpdateInfo {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date lastUpdate;

    // constructor for jpa
    public EntityUpdateInfo(Long id, Date lastUpdate) {
        this.id = String.valueOf(id);
        this.lastUpdate = lastUpdate;
    }
}
