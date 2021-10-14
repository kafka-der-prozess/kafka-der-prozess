package com.pettersystems.prozess.processengine.runtime.instance;

import com.pettersystems.prozess.api.model.Identifier;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class WaitingEvent {

    private Identifier processInstance;

    private OffsetDateTime timeout;

    private Identifier eventDefinition;
}
