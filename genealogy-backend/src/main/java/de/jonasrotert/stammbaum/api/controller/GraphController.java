package de.jonasrotert.stammbaum.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.jonasrotert.stammbaum.api.dto.Graph;
import de.jonasrotert.stammbaum.api.service.GraphService;

@RestController
public class GraphController
{

    @Autowired
    private GraphService graphService;

    @GetMapping("/load")
    @CrossOrigin
    public Graph loadGraph()
    {
        return this.graphService.loadGraph();
    }

}
