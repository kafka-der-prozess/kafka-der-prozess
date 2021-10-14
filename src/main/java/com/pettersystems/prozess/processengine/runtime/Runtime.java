package com.pettersystems.prozess.processengine.runtime;

import com.pettersystems.prozess.processengine.model.definition.Event;
import com.pettersystems.prozess.processengine.model.definition.NodeType;
import com.pettersystems.prozess.processengine.model.definition.ProcessDefinition;
import com.pettersystems.prozess.processengine.model.definition.Relation;
import com.pettersystems.prozess.processengine.model.instance.EventInstance;
import com.pettersystems.prozess.processengine.model.instance.NodeInstance;
import com.pettersystems.prozess.processengine.model.instance.ProcessInstance;
import com.pettersystems.prozess.processengine.runtime.definition.ProcessManagement;
import com.pettersystems.prozess.processengine.runtime.instance.ProcessInstanceManagement;
import com.pettersystems.prozess.processengine.runtime.instance.WaitingEvent;
import com.pettersystems.prozess.processengine.runtime.storage.EventStore;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Runtime {

    private final ProcessManagement processManagement;
    private final ProcessInstanceManagement processInstanceManagement;
    private final List<EventStore> eventStores;

    public void handleEvent(@NonNull final EventInstance event) {
        log.debug("Runtime handling event: {}", event);
        final ProcessDefinition processDefinition = processManagement.getProcessDefinition(event.getProcessDefinition());
        final Event eventDefinition = processManagement.getEvent(event.getEventDefinitionIdentifier());
        final Relation relationDefinition = processManagement.getRelation(eventDefinition.getRelationDefinition());

        final List<WaitingEvent> waitingEvents = processManagement.getWaitingEvent(event);

        // find waiting processes
        waitingEvents.forEach(waitingEvent -> {
            final Tuple2<NodeInstance, ProcessInstance> newState =
                    processInstanceManagement.increaseProcessCounter(relationDefinition, waitingEvent, event);

            inferAndStoreFutureWaitingEvents(newState._1, newState._2);
        });

        // spawn new processes
        if(relationDefinition.getStart().getType() == NodeType.START) {
            // this spawns a new process instance as it is probably the first event in a series

            final Tuple2<NodeInstance, ProcessInstance> newProcessInstance = processInstanceManagement.generateProcessInstance(
                    event, processDefinition, relationDefinition);

            inferAndStoreFutureWaitingEvents(newProcessInstance._1, newProcessInstance._2);
            // however... maybe there are other correlated events waiting which must be joined so we
            // we need to check that (example might be account creation process in dalek, where 4 different
            // events can be the start of the process)
        } else if(waitingEvents.isEmpty()) {
            // don't have the waiting event, yet... need to store in unsatisfied event queue
            storeEventInUnsatisfiedQueue(event);
        }
    }


    private void inferAndStoreFutureWaitingEvents(final NodeInstance newState, final ProcessInstance processInstance) {
        List<WaitingEvent> waitingEvents = newState.getDefinition().getRelations().stream()
                .filter(relation -> newState.getDefinition().equals(relation.getStart()))
                .flatMap(relation -> {
                    OffsetDateTime timeout = newState.getReached().plus(relation.getTimeout());
                    List<Event> possibleEvents = relation.getEvents();
                    return possibleEvents.stream().map(event -> WaitingEvent.builder()
                            .processInstance(processInstance.getIdentifier())
                            .timeout(timeout)
                            .eventDefinition(event.getIdentifier())
                            .build());
                })
                .collect(Collectors.toList());

        waitingEvents.stream().forEach(waitingEvent ->
                log.debug("Created new waiting event for process-instance {}", processInstance.getIdentifier()));

        eventStores.stream().forEach(eventStore -> eventStore.storeWaitingEvents(waitingEvents));
    }

    private void storeEventInUnsatisfiedQueue(final EventInstance event) {
        log.info("Got event {} which we don't know how to handle. Storing in unsatisfied event queue.", event);
        eventStores.stream().forEach(eventStore -> eventStore.storeUnsatisfiedEvent(event));
    }
}
