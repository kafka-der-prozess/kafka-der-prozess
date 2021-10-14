package com.pettersystems.prozess.processengine.model.definition;

import com.pettersystems.prozess.api.model.Identifier;
import lombok.Getter;

@Getter
public abstract class IdentifiableDefinitionParticle {

    private Identifier identifier;
}
