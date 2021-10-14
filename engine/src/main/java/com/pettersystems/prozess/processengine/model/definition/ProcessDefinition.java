package com.pettersystems.prozess.processengine.model.definition;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class ProcessDefinition extends NamedDefinitionParticle {

    private List<Node> activities;


}
