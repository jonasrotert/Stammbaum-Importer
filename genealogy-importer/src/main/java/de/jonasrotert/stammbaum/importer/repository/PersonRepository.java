package de.jonasrotert.stammbaum.importer.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import de.jonasrotert.stammbaum.importer.domain.Person;

@Repository
@Validated
public interface PersonRepository extends CrudRepository<Person, UUID>
{

}
