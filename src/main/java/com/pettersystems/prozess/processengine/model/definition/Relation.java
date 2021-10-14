package com.pettersystems.prozess.processengine.model.definition;

import lombok.Getter;

import java.time.Duration;
import java.util.List;

@Getter
public class Relation extends IdentifiableDefinitionParticle {

    private Node start;

    private Node end;

    private List<Event> events;

    private Duration timeout;
}
