package de.jonasrotert.stammbaum.importer.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import de.jonasrotert.stammbaum.importer.domain.Family;

public interface FamilyRepository extends Neo4jRepository<Family, Long> {
	List<Family> findAllByGedcomID(String gedcomId);

	@Query("MATCH (f:Family) WHERE (f)-->(:Person {name: '$husbandName'}) AND (f)-->(:Person {name: '$wifeName'}) RETURN f")
	List<Family> findAllByHusbandAndWife(String husbandName, String wifeName);
}