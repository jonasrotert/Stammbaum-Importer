package de.jonasrotert.stammbaum.importer.domain;

import java.util.Date;
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
public class Person {

	private Date birthday;

	private String birthplace;

	private String burialPlace;

	private Date buried;

	private String causeOfDeath;

	@Relationship(type = "FATHER")
	private Person father;

	private String firstName;

	private String gedcodmID;

	private String generalNotice;

	private String givenName;

	@Id
	@GeneratedValue
	private Long id;

	private String lastName;

	@Relationship(type = "MARRIAGED_TO")
	private Set<Person> marriagedTo;

	@Relationship(type = "MOTHER")
	private Person mother;

	private String name;

	private String placeOfDeath;

	private String sex;
}
