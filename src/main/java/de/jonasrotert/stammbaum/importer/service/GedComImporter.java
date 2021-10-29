package de.jonasrotert.stammbaum.importer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
				LOGGER.debug("Found header {} in line {}", gedcomId, i);
				int personLineNumber = i;
				final Person person = new Person();
				person.setGedcodmID(gedcomId);
				persons.add(person);
				do {
					personLineNumber++;

					this.extractName(lines.get(personLineNumber), person, personLineNumber);
					this.extractOccupation(lines.get(personLineNumber), person, personLineNumber);
					this.extractSex(lines.get(personLineNumber), person, personLineNumber);
					this.extractBirthInformation(lines, lines.get(personLineNumber), person, personLineNumber);
					this.extractDeathInformation(lines, lines.get(personLineNumber), person, personLineNumber);
					this.extractBurialInformation(lines, lines.get(personLineNumber), person, personLineNumber);
				} while (personLineNumber + 1 < lines.size() && !lines.get(personLineNumber + 1).startsWith("0"));

			}
		}
	}

	private void extractBirthday(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for birthday in line {}", personLineNumber);
		final Date birthday = this.extractDate(line);

		if (birthday != null) {
			person.setBirthday(birthday);
			LOGGER.info("Extracted birthday {} in line {}", person.getBirthday().toString(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract birthday in line {}", personLineNumber);
		}
	}

	private void extractBirthInformation(final List<String> lines, final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for birth information in line {}", personLineNumber);
		final String regex = "1\\sBIRT";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			int birthinformationLine = personLineNumber;

			do {
				birthinformationLine++;
				this.extractBirthday(lines.get(birthinformationLine), person, birthinformationLine);
				this.extractBirthplace(lines.get(birthinformationLine), person, birthinformationLine);
			} while (birthinformationLine + 1 < lines.size() && lines.get(birthinformationLine + 1).startsWith("2"));

		} else {
			LOGGER.debug("Could not extract birth information in line {}", personLineNumber);
		}
	}

	private void extractBirthplace(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for birthplace in line {}", personLineNumber);
		final String birthplace = this.extractPlace(line);

		if (birthplace != null) {
			person.setBirthplace(birthplace);
			LOGGER.info("Extracted birthplace {} in line {}", person.getBirthplace(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract birthplace in line {}", personLineNumber);
		}
	}

	private void extractBurialInformation(final List<String> lines, final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for burial information in line {}", personLineNumber);
		final String regex = "1\\sBURI";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			int burialInformationLine = personLineNumber;

			do {
				burialInformationLine++;
				this.extractDayOfBurial(lines.get(burialInformationLine), person, burialInformationLine);
				this.extractBurialPlace(lines.get(burialInformationLine), person, burialInformationLine);
			} while (burialInformationLine + 1 < lines.size() && lines.get(burialInformationLine + 1).startsWith("2"));

		} else {
			LOGGER.debug("Could not extract burial information in line {}", personLineNumber);
		}
	}

	private void extractBurialPlace(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for burial place in line {}", personLineNumber);
		final String burialplace = this.extractPlace(line);

		if (burialplace != null) {
			person.setBurialPlace(burialplace);
			LOGGER.info("Extracted burial place {} in line {}", person.getBurialPlace(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract burial place in line {}", personLineNumber);
		}
	}

	private Date extractDate(final String line) {
		final String regex = "2\\sDATE\\s(?<date>\\d+\\s\\w+\\s\\d+)";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {

			final String extractedDate = matcher.group("date");
			final SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
			try {
				return formatter.parse(extractedDate);

			} catch (final ParseException e) {
				LOGGER.error("Unabled to parse date {}!", extractedDate);
			}
		}

		return null;
	}

	private void extractDayOfBurial(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for day of burial in line {}", personLineNumber);
		final Date dayOfBurial = this.extractDate(line);

		if (dayOfBurial != null) {
			person.setBuried(dayOfBurial);
			LOGGER.info("Extracted day of burial {} in line {}", person.getBuried().toString(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract day of burial in line {}", personLineNumber);
		}
	}

	private void extractDayOfDeath(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for day of death in line {}", personLineNumber);
		final Date dayOfDeath = this.extractDate(line);

		if (dayOfDeath != null) {
			person.setDayOfDeath(dayOfDeath);
			LOGGER.info("Extracted day of death {} in line {}", person.getDayOfDeath().toString(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract day of death in line {}", personLineNumber);
		}
	}

	private void extractDeathInformation(final List<String> lines, final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for death information in line {}", personLineNumber);
		final String regex = "1\\sDEAT";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			int birthinformationLine = personLineNumber;

			do {
				birthinformationLine++;
				this.extractDayOfDeath(lines.get(birthinformationLine), person, birthinformationLine);
				this.extractPlaceOfDeath(lines.get(birthinformationLine), person, birthinformationLine);
			} while (birthinformationLine + 1 < lines.size() && lines.get(birthinformationLine + 1).startsWith("2"));

		} else {
			LOGGER.debug("Could not extract death information in line {}", personLineNumber);
		}
	}

	private void extractName(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for name in line {}", personLineNumber);
		final String regex = "1\\sNAME\\s(?<firstName>.+)\\s\\/(?<lastName>.+)\\/";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			person.setFirstName(matcher.group("firstName"));
			person.setLastName(matcher.group("lastName"));
			LOGGER.info("Extracted firstName {} and lastName {} in line {}", person.getFirstName(), person.getLastName(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract name in line {}", personLineNumber);
		}
	}

	private void extractOccupation(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for occupation in line {}", personLineNumber);
		final String regex = "1\\sOCCU\\s(?<occupation>.+)\\s\\/(?<lastName>.+)\\/";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			person.setOccupation(matcher.group("occupation"));
			LOGGER.info("Extracted occupation {} in line {}", person.getOccupation(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract occupation in line {}", personLineNumber);
		}
	}

	private String extractPlace(final String line) {
		final String regex = "2\\sPLAC\\s(?<place>.+)";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			return matcher.group("place");
		} else {
			return null;
		}
	}

	private void extractPlaceOfDeath(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for place of death in line {}", personLineNumber);
		final String placeOfDeath = this.extractPlace(line);

		if (placeOfDeath != null) {
			person.setPlaceOfDeath(placeOfDeath);
			LOGGER.info("Extracted place of Death {} in line {}", person.getPlaceOfDeath(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract place of death in line {}", personLineNumber);
		}
	}

	private void extractSex(final String line, final Person person, final int personLineNumber) {
		LOGGER.debug("Searching for sex in line {}", personLineNumber);
		final String regex = "1\\sSEX\\s(?<sex>[\\w])";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			person.setSex(matcher.group("sex"));
			LOGGER.info("Extracted sex {} in line {}", person.getSex(), personLineNumber);
		} else {
			LOGGER.debug("Could not extract sex in line {}", personLineNumber);
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
