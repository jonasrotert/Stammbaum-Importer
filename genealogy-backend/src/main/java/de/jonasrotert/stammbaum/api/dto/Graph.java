package de.jonasrotert.stammbaum.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Graph
{
    private List<Node> nodes = new ArrayList<>();
    private List<Link> links = new ArrayList<>();
}
