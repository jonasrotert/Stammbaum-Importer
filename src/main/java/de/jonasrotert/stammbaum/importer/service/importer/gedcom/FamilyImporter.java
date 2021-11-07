package de.jonasrotert.stammbaum.importer.service.importer.gedcom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.jonasrotert.stammbaum.importer.domain.Family;
import de.jonasrotert.stammbaum.importer.domain.Person;

@Component
public class FamilyImporter {

	private static Logger LOGGER = LoggerFactory.getLogger(FamilyImporter.class);

	private void analyzeList(final List<String> lines, final List<Family> families, final Map<String, Person> persons) {

		for (int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i);
			final String gedcomId = this.getGedComId(line);
			if (gedcomId != null) {
				LOGGER.debug("Found family header {} in line {}", gedcomId, i);
				int familyLineNumber = i;
				final Family family = new Family();
				family.setGedcomID(gedcomId);
				families.add(family);
				final Set<Person> chilren = new HashSet<>();
				do {
					familyLineNumber++;

					final Person husband = persons.get(this.extractGedComIdOfHusband(lines.get(familyLineNumber)));
					if (husband != null) {
						husband.getFamilies().add(family);
						// family.getStarters().add(husband);
					}

					final Person wife = persons.get(this.extractGedComIdOfWife(lines.get(familyLineNumber)));
					if (wife != null) {
						wife.getFamilies().add(family);
						// family.getStarters().add(wife);
					}

					final Person child = persons.get(this.extractGedComIdOfChild(lines.get(familyLineNumber)));
					if (child != null) {
						chilren.add(child);
					}
				} while (familyLineNumber + 1 < lines.size() && !lines.get(familyLineNumber + 1).startsWith("0"));

				family.getChildren().addAll(chilren);
			}
		}
	}

	private String extractGedComIdOfChild(final String line) {
		return this.extractReferenceFromLine(1, "CHIL", line);
	}

	private String extractGedComIdOfHusband(final String line) {
		return this.extractReferenceFromLine(1, "HUSB", line);
	}

	private String extractGedComIdOfWife(final String line) {
		return this.extractReferenceFromLine(1, "WIFE", line);
	}

	private String extractReferenceFromLine(final int level, final String key, final String line) {
		return this.getFirstMatch(level + "\\s" + key + "\\s\\@I(.+)\\@", line);
	}

	private String getFirstMatch(final String regex, final String line) {
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(line);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private String getGedComId(final String line) {
		return this.getFirstMatch("0\\s\\@F([\\w\\d]+)\\@\\sFAM", line);
	}

	public List<Family> importFromFile(final String fileName, final Map<String, Person> persons) {
		final Path path = Paths.get(fileName);
		final List<Family> families = new LinkedList<>();
		try (Stream<String> stream = Files.lines(path)) {
			this.analyzeList(stream.collect(Collectors.toList()), families, persons);
		} catch (final IOException e) {
			LOGGER.error(e.getStackTrace().toString());
		}

		return families;
	}
}
