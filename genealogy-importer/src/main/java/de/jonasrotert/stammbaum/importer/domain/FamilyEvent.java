package de.jonasrotert.stammbaum.importer.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class FamilyEvent
{
    private Date            date;
    private FamilyEventType eventType;
}
