package de.jonasrotert.stammbaum.importer.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.Valid;

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
public class Family
{
    @Id
    private UUID                    id;

    private String                  gedcomId;

    @Transient
    private final List<FamilyEvent> events   = new ArrayList<>();

    @ManyToMany(mappedBy = "familiesAsSpouse")
    @Valid
    private final List<Person>      spouses  = new ArrayList<>();

    @ManyToMany(mappedBy = "familiesAsChild")
    @Valid
    private final List<Person>      children = new ArrayList<>();

    @PrePersist
    public void prePersist()
    {
        if (this.id == null)
        {
            this.id = UUID.randomUUID();
        }
    }
}
