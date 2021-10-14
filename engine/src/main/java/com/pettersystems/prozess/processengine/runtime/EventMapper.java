package com.pettersystems.prozess.processengine.runtime;

import com.pettersystems.prozess.processengine.model.definition.Event;
import com.pettersystems.prozess.processengine.model.definition.ProcessDefinition;
import com.pettersystems.prozess.processengine.model.definition.Relation;
import com.pettersystems.prozess.processengine.model.instance.ProcessInstance;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class EventMapper {

    private Map<Event, Set<Tuple2<ProcessDefinition, Relation>>> events = new HashMap<>();

    public void feedEventMapperWithNewEvents(final ProcessDefinition processDefinition) {
        processDefinition.getActivities().stream().forEach(activity ->
            activity.getRelations().stream().forEach(relation ->
                relation.getEvents().forEach(event -> events.computeIfAbsent(event,
                        e -> new HashSet<>()).add(Tuple.of(processDefinition, relation)))));
    }

    public Set<Tuple2<ProcessDefinition, Relation>> getProcessDefinitionsForEvent(final Event event) {
        return Option.of(events.get(event)).getOrElse(new HashSet<>());
    }

    public ProcessInstance identifyProcessInstance() {

    }
}
