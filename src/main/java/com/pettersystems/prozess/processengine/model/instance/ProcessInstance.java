package com.pettersystems.prozess.processengine.model.instance;

import com.pettersystems.prozess.api.model.Identifier;
import com.pettersystems.prozess.processengine.model.definition.ProcessDefinition;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProcessInstance {

    private ProcessDefinition definition;

    private Identifier identifier;

    private List<NodeInstance> reachedStates;

    private Map<String, InstanceValue> variables;
}
