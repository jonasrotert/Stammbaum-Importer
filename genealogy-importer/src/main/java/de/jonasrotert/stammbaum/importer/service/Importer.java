package de.jonasrotert.stammbaum.importer.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.IndividualEvent;
import org.gedcom4j.model.PersonalName;
import org.gedcom4j.parser.GedcomParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.jonasrotert.stammbaum.importer.domain.Name;
import de.jonasrotert.stammbaum.importer.domain.Person;
import de.jonasrotert.stammbaum.importer.domain.PersonEvent;
import de.jonasrotert.stammbaum.importer.domain.PersonEventType;
import de.jonasrotert.stammbaum.importer.domain.Sex;
import de.jonasrotert.stammbaum.importer.repository.FamilyRepository;
import de.jonasrotert.stammbaum.importer.repository.PersonRepository;

@Service
public class Importer
{

    private static Logger                 LOGGER     = LoggerFactory.getLogger(Importer.class);

    private static final SimpleDateFormat dateformat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    @Autowired
    private PersonRepository              personRepository;

    @Autowired
    private FamilyRepository              familyRepository;

    public Collection<Person> importPerson(final String fileName)
    {
        final GedcomParser gp = new GedcomParser();
        try
        {
            gp.load(fileName);
        } catch (IOException | GedcomParserException e)
        {
            LOGGER.error("Unable to parse gedcom file", e);
        }

        final var map = gp.getGedcom().getIndividuals().values().stream().map(this::map2Person).collect(Collectors.toMap(Person::getGedcomId, e -> e));
        this.personRepository.saveAll(map.values());

        final var list = gp.getGedcom().getFamilies().values().stream().map(f -> this.mapFamily(f, map)).collect(Collectors.toList());

        this.familyRepository.saveAll(list);
        this.personRepository.saveAll(map.values());

        return map.values();
    }


    private Person map2Person(final Individual individual)
    {
        final var p = new Person();
        p.setGedcomId(individual.getXref());

        if (individual.getSex() != null)
        {
            p.setSex(individual.getSex().getValue().equals("m") ? Sex.MALE : Sex.FEMALE);
        }

        individual.getNames(true).stream().map(this::mapName).filter(Objects::nonNull).forEach(x -> {
            p.getNames().add(x);
            x.setPerson(p);
        });

        individual.getEvents(true).stream().map(this::mapEvent).filter(Objects::nonNull).forEach(x -> {
            p.getEvents().add(x);
            x.setPerson(p);
        });

        return p;
    }


    private PersonEvent mapEvent(final IndividualEvent individualEvent)
    {
        if (individualEvent.getDate() != null)
        {
            try
            {
                return PersonEvent.builder().date(dateformat.parse(individualEvent.getDate().getValue())).eventType(PersonEventType.valueOf(individualEvent.getType().name())).build();
            } catch (final ParseException e)
            {
                LOGGER.error("Unable to parse birthdate: " + individualEvent.getDate().getValue());
                return null;
            }
        }

        return null;
    }


    private de.jonasrotert.stammbaum.importer.domain.Family mapFamily(final Family family, final Map<String, Person> personMap)
    {

        final de.jonasrotert.stammbaum.importer.domain.Family newFamily = de.jonasrotert.stammbaum.importer.domain.Family.builder().gedcomId(family.getXref()).build();

        family.getChildren(true).stream().forEach(c -> {
            final var child = personMap.get(c.getIndividual().getXref());

            if (child != null)
            {
                newFamily.getChildren().add(child);
                child.getFamiliesAsChild().add(newFamily);
            }
        });

        if (family.getWife() != null)
        {
            final var wife = personMap.get(family.getWife().getIndividual().getXref());

            if (wife != null)
            {
                newFamily.getSpouses().add(wife);
                wife.getFamiliesAsSpouse().add(newFamily);
            }
        }

        if (family.getHusband() != null)
        {
            final var husband = personMap.get(family.getHusband().getIndividual().getXref());

            if (husband != null)
            {
                newFamily.getSpouses().add(husband);
                husband.getFamiliesAsSpouse().add(newFamily);
            }
        }
        return newFamily;
    }


    private Name mapName(final PersonalName personalName)
    {

        return Name.builder().firstName(personalName.getGivenName() != null ? personalName.getGivenName().getValue() : null).lastName(personalName.getSurname() != null ? personalName.getSurname().getValue() : null).title(personalName.getSurnamePrefix() != null ? personalName.getSurnamePrefix().getValue() : null).build();
    }

}
