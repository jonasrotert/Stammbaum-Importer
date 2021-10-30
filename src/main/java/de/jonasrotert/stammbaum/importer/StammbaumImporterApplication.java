package de.jonasrotert.stammbaum.importer;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.util.StringUtils;

import de.jonasrotert.stammbaum.importer.domain.Person;
import de.jonasrotert.stammbaum.importer.service.CSVExporter;
import de.jonasrotert.stammbaum.importer.service.GedComImporter;

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
	private GedComImporter gedComImporter;

	@Override
	public void run(final String... args) throws Exception {
		String fileName = null;
		String outputFilename = null;

		for (int i = 0; i < args.length; ++i) {

			if ("-i".equals(args[i]) && i < args.length - 1) {
				fileName = args[i + 1];
				LOGGER.info("Got filename {}", fileName);
			}
			if ("-o".equals(args[i]) && i < args.length - 1) {
				outputFilename = args[i + 1];
				LOGGER.info("Got outputfilename {}", outputFilename);
			}
		}
		final List<Person> persons = new LinkedList<>();

		if (StringUtils.hasText(fileName)) {
			persons.addAll(this.gedComImporter.importFile(fileName));
		}

		LOGGER.info("Found {} person(s)", persons.size());

		if (StringUtils.hasText(outputFilename)) {
			this.csvExporter.exportToFile(persons, outputFilename);
		}
	}
}
