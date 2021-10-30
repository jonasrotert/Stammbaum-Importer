package de.jonasrotert.stammbaum.importer.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.jonasrotert.stammbaum.importer.domain.Person;

@Component
public class CSVExporter {

	private final static String COMMA = ",";

	private static Logger LOGGER = LoggerFactory.getLogger(CSVExporter.class);
	private final static String NL = "\n";

	public void exportToFile(final List<Person> persons, final String outputFilename) {

		try (FileWriter fileWriter = new FileWriter(outputFilename)) {
			LOGGER.info("Writing to {}", outputFilename);

			final StringBuilder sb = new StringBuilder();

			sb.append("#").append(COMMA).append("Vorname").append(COMMA).append("Nachname").append(COMMA).append("Sex").append(COMMA).append("Geboren am").append(COMMA).append("Geburtsort").append(COMMA).append("Gestorben am").append(COMMA).append("Gestorben in").append(COMMA).append("Begraben am").append(COMMA).append("Begräbnisstätte").append(COMMA)
					.append("Beruf").append(NL);

			persons.stream().forEach(person -> this.writeToSB(person, sb));
			fileWriter.write(sb.toString());
			fileWriter.close();
		} catch (final IOException e) {
			LOGGER.error(e.getStackTrace().toString());
		}
	}

	private void writeToSB(final Person person, final StringBuilder sb) {
		sb.append("#").append(COMMA).append(person.getFirstName()).append(COMMA).append(person.getLastName()).append(COMMA).append(person.getSex()).append(COMMA).append(person.getBirthday()).append(COMMA).append(person.getBirthplace()).append(COMMA).append(person.getDayOfDeath()).append(COMMA).append(person.getPlaceOfDeath()).append(COMMA)
				.append(person.getBuried()).append(COMMA).append(person.getBurialPlace()).append(COMMA).append(person.getOccupation()).append(NL);
	}

}
