package de.jonasrotert.stammbaum.api.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import de.jonasrotert.stammbaum.api.domain.Family;

@Repository
@Validated
public interface FamilyRepository extends CrudRepository<Family, UUID>
{

}
