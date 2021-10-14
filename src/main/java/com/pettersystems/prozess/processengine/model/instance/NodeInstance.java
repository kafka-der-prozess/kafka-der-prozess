package com.pettersystems.prozess.processengine.model.instance;

import com.pettersystems.prozess.api.model.Identifier;
import com.pettersystems.prozess.processengine.model.definition.Node;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class NodeInstance {

    private Node definition;

    private Identifier throughRelationWithId;

    private OffsetDateTime reached;

    private Boolean isLate;

    private Map<String, InstanceValue> variables;
}
