package com.pettersystems.prozess.processengine.model.definition;

import lombok.Getter;

import java.util.List;

@Getter
public class Node extends NamedDefinitionParticle {

    private NodeType type;

    private List<Relation> relations;
}
