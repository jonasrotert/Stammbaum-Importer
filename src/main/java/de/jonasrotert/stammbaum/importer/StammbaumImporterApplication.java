package de.jonasrotert.stammbaum.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class StammbaumImporterApplication {

	private static Logger logger = LoggerFactory.getLogger(StammbaumImporterApplication.class);

	public static void main(final String[] args) {
		logger.info("Hello");
		SpringApplication.run(StammbaumImporterApplication.class, args);
	}

}
