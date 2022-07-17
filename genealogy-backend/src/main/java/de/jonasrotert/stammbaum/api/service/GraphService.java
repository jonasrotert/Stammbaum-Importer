package de.jonasrotert.stammbaum.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.jonasrotert.stammbaum.api.domain.Family;
import de.jonasrotert.stammbaum.api.domain.Person;
import de.jonasrotert.stammbaum.api.dto.Graph;
import de.jonasrotert.stammbaum.api.dto.Link;
import de.jonasrotert.stammbaum.api.dto.Node;
import de.jonasrotert.stammbaum.api.repository.FamilyRepository;
import de.jonasrotert.stammbaum.api.repository.PersonRepository;

@Service
public class GraphService
{
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FamilyRepository familyRepository;

    private String getName(final Person person)
    {
        final StringBuilder sb = new StringBuilder();
        person.getNames().stream().forEach(n -> sb.append(n.getFirstName()).append(" ").append(n.getLastName()));
        return sb.toString();
    }


    public Graph loadGraph()
    {
        final var graph = new Graph();

        final var persons = this.personRepository.findAll();

        for (final Person person : persons)
        {
            graph.getNodes().add(Node.builder().id(person.getId()).name(this.getName(person)).build());
        }

        final var families = this.familyRepository.findAll();

        for (final Family family : families)
        {

            graph.getNodes().add(Node.builder().id(family.getId()).build());

            family.getChildren().forEach(child -> {
                graph.getLinks().add(Link.builder().source(family.getId()).target(child.getId()).build());
            });

            family.getSpouses().forEach(spouse -> {
                graph.getLinks().add(Link.builder().target(family.getId()).source(spouse.getId()).build());
            });
        }

        return graph;
    }
}
