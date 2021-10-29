package de.jonasrotert.stammbaum.importer.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Node
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@Relationship(type = "TEAMMATE")
	public Set<Person> teammates;

	@Override
	public String toString() {

		return this.name + "'s teammates => " + Optional.ofNullable(this.teammates).orElse(Collections.emptySet()).stream().map(Person::getName).collect(Collectors.toList());

	}

	public void worksWith(final Person person) {
		if (this.teammates == null) {
			this.teammates = new HashSet<>();
		}
		this.teammates.add(person);
	}

}
