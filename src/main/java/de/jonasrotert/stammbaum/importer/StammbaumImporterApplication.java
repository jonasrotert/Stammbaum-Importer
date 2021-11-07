package de.jonasrotert.stammbaum.importer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.util.StringUtils;

import de.jonasrotert.stammbaum.importer.domain.Family;
import de.jonasrotert.stammbaum.importer.domain.Person;
import de.jonasrotert.stammbaum.importer.repository.FamilyRepository;
import de.jonasrotert.stammbaum.importer.repository.PersonRepository;
import de.jonasrotert.stammbaum.importer.service.exporter.CSVExporter;
import de.jonasrotert.stammbaum.importer.service.exporter.GraphDatabaseExporter;
import de.jonasrotert.stammbaum.importer.service.importer.gedcom.FamilyImporter;
import de.jonasrotert.stammbaum.importer.service.importer.gedcom.PersonImporter;

@SpringBootApplication
@EnableNeo4jRepositories
public class StammbaumImporterApplication implements CommandLineRunner {

	private static Logger LOGGER = LoggerFactory.getLogger(StammbaumImporterApplication.class);

	public static void main(final String[] args) {
		LOGGER.info("Starting app");
		SpringApplication.run(StammbaumImporterApplication.class, args);
	}

	@Autowired
	private CSVExporter csvExporter;

	@Autowired
	private FamilyImporter familyImporter;

	@Autowired
	private FamilyRepository familyRepository;

	@Autowired
	private PersonImporter gedComImporter;

	@Autowired
	private GraphDatabaseExporter graphDatabaseExporter;

	@Autowired
	private PersonRepository personRepository;

	@Override
	public void run(final String... args) throws Exception {
		String fileName = null;
		String outputFilename = null;
		boolean deletAll = false;

		for (int i = 0; i < args.length; ++i) {
			if ("-dA".equals(args[i])) {
				deletAll = true;
				LOGGER.warn("Parameter deleteAll was chosen!", outputFilename);
			}
			if ("-i".equals(args[i]) && i < args.length - 1) {
				fileName = args[i + 1];
				LOGGER.info("Got filename {}", fileName);
			}
			if ("-o".equals(args[i]) && i < args.length - 1) {
				outputFilename = args[i + 1];
				LOGGER.info("Got outputfilename {}", outputFilename);
			}
		}

		if (deletAll) {
			LOGGER.info("Deleting all persons");
			this.personRepository.deleteAll();
			this.familyRepository.deleteAll();
		}

		final Map<String, Person> persons = new HashMap<>();
		final List<Family> families = new LinkedList<>();

		if (StringUtils.hasText(fileName)) {
			persons.putAll(this.gedComImporter.importFile(fileName));
			families.addAll(this.familyImporter.importFromFile(fileName, persons));
		}

		LOGGER.info("Found {} person(s)", persons.size());
		LOGGER.info("Found {} families", families.size());

		if (StringUtils.hasText(outputFilename)) {
			this.csvExporter.exportToFile(new LinkedList<>(persons.values()), outputFilename);
		}

		this.graphDatabaseExporter.exportPersons(new LinkedList<>(persons.values()));
		this.graphDatabaseExporter.exportFamilies(families);

		LOGGER.info("Done");
	}
}
