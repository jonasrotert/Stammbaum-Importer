package de.jonasrotert.stammbaum.importer.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

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

	private Date dayOfDeath;

	@Relationship(type = "STARTED_FAMILY", direction = Direction.OUTGOING)
	private Set<Family> families = new HashSet<>();

	private String firstName;

	private String gedcomID;

	private String generalNotice;

	private String givenName;

	@Id
	@GeneratedValue
	private Long id;

	private String lastName;

	private String name;

	private String occupation;

	private String placeOfDeath;

	private String sex;

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
		this.setName(this.getFirstName() + " " + this.getLastName());
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
		this.setName(this.getFirstName() + " " + this.getLastName());
	}
}
