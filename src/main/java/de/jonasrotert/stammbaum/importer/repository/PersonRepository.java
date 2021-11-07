package de.jonasrotert.stammbaum.importer.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import de.jonasrotert.stammbaum.importer.domain.Person;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

	List<Person> findAllByGedcomID(String gedcomId);

	/**
	 * Must define own query here to prevent runtime errors while querying optional
	 * relationships implicitely
	 *
	 * @param name
	 * @return
	 */
	@Query("MATCH (n:Person) WHERE n.name = $name RETURN n")
	List<Person> findAllByName(String name);
}