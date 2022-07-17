package de.jonasrotert.stammbaum.api.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
public class Person
{
    @Id
    private UUID                    id;

    private String                  gedcomId;

    @OneToMany(mappedBy = "person", orphanRemoval = true, cascade = CascadeType.ALL)
    @NotEmpty
    @Valid
    private final List<Name>        names            = new ArrayList<>();

    @OneToMany(mappedBy = "person", orphanRemoval = true, cascade = CascadeType.ALL)
    @Valid
    private final List<PersonEvent> events           = new ArrayList<>();

    @NotNull
    private Sex                     sex;

    @ManyToMany
    @JoinTable(name = "person_family_as_spouse", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "family_id"))
    private final List<Family>      familiesAsSpouse = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "person_family_as_child", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "family_id"))
    private final List<Family>      familiesAsChild  = new ArrayList<>();

    @PrePersist
    public void prePersist()
    {
        if (this.id == null)
        {
            this.id = UUID.randomUUID();
        }
    }

}
