package de.jonasrotert.stammbaum.importer.service.exporter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.jonasrotert.stammbaum.importer.domain.Family;
import de.jonasrotert.stammbaum.importer.domain.Person;
import de.jonasrotert.stammbaum.importer.repository.FamilyRepository;
import de.jonasrotert.stammbaum.importer.repository.PersonRepository;

@Service
public class GraphDatabaseExporter {

	private static Logger LOGGER = LoggerFactory.getLogger(GraphDatabaseExporter.class);

	@Autowired
	private FamilyRepository familyRepository;

	@Autowired
	private PersonRepository personRepository;

	public void exportFamilies(final List<Family> families) {
		LOGGER.info("Exporting all {} families to graph database", families.size());
		families.forEach(f -> {
			// final List<Person> possibleDuplicates = this.familyRepository.findAll

			this.familyRepository.save(f);
		});
	}

	public void exportPersons(final List<Person> persons) {
		LOGGER.info("Exporting all {} persons to graph database", persons.size());
		persons.forEach(p -> {
			final List<Person> possibleDuplicates = this.personRepository.findAllByName(p.getName());

			final boolean containsDuplicate = possibleDuplicates.stream().anyMatch(d -> (p.getBirthday() != null ? p.getBirthday().equals(d.getBirthday()) : true || p.getGedcomID().equals(d.getGedcomID())));

			if (!containsDuplicate) {
				LOGGER.info("No duplicate candidates for {} found", p.getName());
				this.personRepository.save(p);

				LOGGER.info("Saved person {}", p.getName());
			} else {
				LOGGER.info("Person {} is already in database", p.getName());
			}
		});
	}

}
