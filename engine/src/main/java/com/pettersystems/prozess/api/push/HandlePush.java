package com.pettersystems.prozess.api.push;

import com.pettersystems.prozess.api.model.Identifier;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

public interface HandlePush {

    List<Identifier> pushEvent(@NotNull OffsetDateTime eventTime,
                               @NotNull Identifier processDefinition,
                               @NotNull String eventType,
                               List<Identifier> identifiers,
                               @NotNull String message);

}
