package com.pettersystems.prozess.processengine.model.instance;

import com.pettersystems.prozess.api.model.Identifier;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class EventInstance {

    private Identifier processDefinition;

    private OffsetDateTime eventTime;

    private String message;

    private Identifier eventDefinitionIdentifier;
}
