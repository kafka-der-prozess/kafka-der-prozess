package com.pettersystems.prozess.error;

import com.pettersystems.prozess.api.model.Identifier;

public class ProcessInstanceNotFoundException extends RuntimeException {

    public ProcessInstanceNotFoundException(final Identifier identifier) {
        super("Could not find process instance with id " + identifier.getIdentifier());
    }
}
