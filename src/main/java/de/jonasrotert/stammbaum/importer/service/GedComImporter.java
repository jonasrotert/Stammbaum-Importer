package de.jonasrotert.stammbaum.importer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.jonasrotert.stammbaum.importer.domain.Person;

@Component
public class GedComImporter {

	private static Logger LOGGER = LoggerFactory.getLogger(GedComImporter.class);

	private void analyzeList(final List<String> lines, final List<Person> persons) {

		for (int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i);
			final String gedcomId = this.getGedComId(line);
			if (gedcomId != null) {
				LOGGER.info("Found header {} in line {}", gedcomId, i);
				int personLineNumber = i;
				final Person person = new Person();
				person.setGedcodmID(gedcomId);
				persons.add(person);
				do {
					personLineNumber++;

					this.extractName(lines.get(personLineNumber), person, personLineNumber);
					this.extractSex(lines.get(personLineNumber), person, personLineNumber);
				} while (personLineNumber + 1 < lines.size() && !lines.get(personLineNumber + 1).startsWith("0"));

			}
		}
	}

	private void extractName(final String line, final Person person, final int personLineNumber) {
		LOGGER.info("Searching for name in line {}", personLineNumber);
		final String regex = "1\\sNAME\\s(?<firstName>[\\w\\d]+)\\s\\/(?<lastName>[\\w\\d]+)\\/";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			person.setFirstName(matcher.group("firstName"));
			person.setLastName(matcher.group("lastName"));
			LOGGER.info("Extracted firstName {} and lastName {} in line {}", person.getFirstName(), person.getLastName(), personLineNumber);
		} else {
			LOGGER.warn("Could not extract name from in line {}", personLineNumber);
		}
	}

	private void extractSex(final String line, final Person person, final int personLineNumber) {
		LOGGER.info("Searching for sex in line {}", personLineNumber);
		final String regex = "1\\sSEX\\s(?<sex>[\\w])";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			person.setSex(matcher.group("sex"));
			LOGGER.info("Extracted sex {} in line {}", person.getSex(), personLineNumber);
		} else {
			LOGGER.warn("Could not extract sex from in line {}", personLineNumber);
		}
	}

	private String getGedComId(final String line) {
		final String regex = "0\\s\\@I([\\w\\d]+)\\@\\sINDI";

		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public void importFile(final String fileName) {
		final Path path = Paths.get(fileName);
		final List<Person> persons = new LinkedList<>();
		try (Stream<String> stream = Files.lines(path)) {
			this.analyzeList(stream.collect(Collectors.toList()), persons);
			LOGGER.info("Found {} person(s)", persons.size());
		} catch (final IOException e) {
			LOGGER.error(e.getStackTrace().toString());
		}
	}
}
