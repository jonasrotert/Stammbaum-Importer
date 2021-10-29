package de.jonasrotert.stammbaum.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

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
	private GedComImporter gedComImporter;

	@Override
	public void run(final String... args) throws Exception {
		for (int i = 0; i < args.length; ++i) {
			if ("-f".equals(args[i]) && i < args.length - 1) {
				final String fileName = args[i + 1];
				LOGGER.info("Given filename: {}", fileName);
				this.gedComImporter.importFile(fileName);
			}
		}
	}
}
