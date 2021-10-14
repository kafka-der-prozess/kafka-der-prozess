package com.pettersystems.prozess.processengine.model.definition;

import lombok.Getter;

@Getter
public abstract class NamedDefinitionParticle extends IdentifiableDefinitionParticle {

    private String name;
}
