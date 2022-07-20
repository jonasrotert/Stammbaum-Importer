package de.jonasrotert.stammbaum.api.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.jonasrotert.stammbaum.api.dto.Graph;
import de.jonasrotert.stammbaum.api.service.GraphService;

@RestController
public class GraphController
{

    private static final Log LOGGER = LogFactory.getLog(GraphController.class);

    @Autowired
    private GraphService     graphService;

    @GetMapping("/load")
    @CrossOrigin
    public Graph loadGraph()
    {
        return this.graphService.loadGraph();
    }

}
