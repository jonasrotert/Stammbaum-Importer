package de.jonasrotert.stammbaum.importer.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Node
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Family {

	@Relationship(type = "CHILDREN")
	private Set<Person> children = new HashSet<>();

	private String gedcomID;

	@Id
	@GeneratedValue
	private Long id;

	// @Relationship(type = "STARTED_FAMILY", direction = Direction.INCOMING)
	// private final Set<Person> starters = new HashSet<>();
}
