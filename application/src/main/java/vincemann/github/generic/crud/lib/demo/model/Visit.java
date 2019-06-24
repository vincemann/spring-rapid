package vincemann.github.generic.crud.lib.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import vincemann.github.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Visit extends IdentifiableEntityImpl<Long> {

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "description")
    private String description;
}
