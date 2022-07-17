package de.jonasrotert.stammbaum.api.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Node
{
    private String name;
    private UUID   id;
}
