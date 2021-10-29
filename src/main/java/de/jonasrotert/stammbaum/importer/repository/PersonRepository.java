package de.jonasrotert.stammbaum.importer.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import de.jonasrotert.stammbaum.importer.domain.Person;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

	Person findByName(String name);

	// List<Person> findByTeammatesName(String name);
}