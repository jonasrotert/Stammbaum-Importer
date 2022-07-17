package de.jonasrotert.stammbaum.api.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
public class Name
{

    @Id
    private UUID   id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String title;

    @ManyToOne
    @Valid
    @NotNull
    private Person person;

    @PrePersist
    public void prePersist()
    {
        if (this.id == null)
        {
            this.id = UUID.randomUUID();
        }
    }
}
