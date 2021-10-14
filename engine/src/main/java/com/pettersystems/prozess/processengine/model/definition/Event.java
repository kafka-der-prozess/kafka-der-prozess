package com.pettersystems.prozess.processengine.model.definition;

import com.pettersystems.prozess.api.model.Identifier;
import lombok.Data;

import java.util.List;

@Data
public abstract class Event extends NamedDefinitionParticle {

    private Identifier relationDefinition;
}
