package com.pettersystems.prozess.processengine.runtime.instance;

import com.pettersystems.prozess.api.model.Identifier;
import com.pettersystems.prozess.error.ProcessInstanceNotFoundException;
import com.pettersystems.prozess.processengine.model.definition.Node;
import com.pettersystems.prozess.processengine.model.definition.ProcessDefinition;
import com.pettersystems.prozess.processengine.model.definition.Relation;
import com.pettersystems.prozess.processengine.model.instance.EventInstance;
import com.pettersystems.prozess.processengine.model.instance.NodeInstance;
import com.pettersystems.prozess.processengine.model.instance.ProcessInstance;
import com.pettersystems.prozess.processengine.runtime.definition.ProcessManagement;
import com.pettersystems.prozess.processengine.runtime.storage.ProcessStore;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessInstanceManagement {

    private final ProcessStore processStore;

    private ReverseArrivalComparator reverseArrivalComparator = new ReverseArrivalComparator();

    private static class ReverseArrivalComparator implements Comparator<NodeInstance> {

        @Override
        public int compare(NodeInstance o1, NodeInstance o2) {
            if(o1.getReached() != null && o2.getReached() != null) {
                if(o1.getReached().isAfter(o2.getReached())) {
                    return -1;
                } else if(o1.equals(o2)) {
                    return 0;
                } else {
                    return 1;
                }
            }
            return 0;
        }
    }

    public Tuple2<NodeInstance, ProcessInstance> generateProcessInstance(final EventInstance eventInstance,
                                                                         final ProcessDefinition processDefinition,
                                                                         final Relation relationDefinition) {

        NodeInstance nodeInstance = NodeInstance.builder()
                .definition(relationDefinition.getStart())
                .throughRelationWithId(relationDefinition.getIdentifier())
                .reached(eventInstance.getEventTime())
                // TODO: extract variables
                .variables(Collections.emptyMap())
                .isLate(false)
                .build();

        List<NodeInstance> reachedNodes = new ArrayList<>();
        reachedNodes.add(nodeInstance);

        ProcessInstance processInstance = ProcessInstance.builder()
                .identifier(Identifier.builder().identifier(UUID.randomUUID().toString()).build())
                .definition(processDefinition)
                .reachedStates(reachedNodes)
                // TODO: extract variables
                .build();

        log.debug("Started new process with id {} with definition {} on node with id {}",
                processInstance.getIdentifier(), processInstance.getDefinition().getIdentifier(),
                nodeInstance.getDefinition().getIdentifier());

        storeProcessInstance(processInstance);

        return Tuple.of(nodeInstance, processInstance);
    }

    public Tuple2<NodeInstance, ProcessInstance> increaseProcessCounter(final Relation relationDefinition,
                                                                        final WaitingEvent waitingEvent,
                                                                        final EventInstance eventInstance) {
        final ProcessInstance processInstance = getInstanceFromEvent(waitingEvent);

        NodeInstance nodeInstance = NodeInstance.builder()
                .definition(relationDefinition.getEnd())
                .throughRelationWithId(relationDefinition.getIdentifier())
                .reached(eventInstance.getEventTime())
                // TODO: extract variables
                .variables(Collections.emptyMap())
                .isLate(computeIsLate(relationDefinition, processInstance, eventInstance))
                .build();

        processInstance.getReachedStates().add(nodeInstance);

        log.debug("Process with id {} with definition {} reached state on node with id {}",
                processInstance.getIdentifier(), processInstance.getDefinition().getIdentifier(),
                nodeInstance.getDefinition().getIdentifier());

        storeProcessInstance(processInstance);

        return Tuple.of(nodeInstance, processInstance);
    }

    private Optional<NodeInstance> getLastPreviousState(final ProcessInstance processInstance, final Relation relationDefinition) {
        final Node start = relationDefinition.getStart();
        return processInstance.getReachedStates().stream()
                .filter(nodeInstance -> nodeInstance.getDefinition().equals(start))
                .sorted(reverseArrivalComparator)
                .findFirst();
    }

    private ProcessInstance getInstanceFromEvent(final WaitingEvent waitingEvent) {
        return processStore.getProcessInstance(waitingEvent.getProcessInstance())
                .getOrElse(() -> {
                    log.warn("Process-instance with id {} should exist, but could not be found!");
                    throw new ProcessInstanceNotFoundException(waitingEvent.getProcessInstance());
                });
    }

    private boolean computeIsLate(final Relation relationDefinition,
                                  final ProcessInstance processInstance,
                                  final EventInstance eventInstance) {
        if(relationDefinition.getTimeout() == null) return false;
        return getLastPreviousState(processInstance, relationDefinition)
                .map(latestPreviousState -> relationDefinition.getTimeout().compareTo(
                    Duration.between(latestPreviousState.getReached(), eventInstance.getEventTime())) < 0)
                .orElseGet(() -> false);
    }

    public void storeProcessInstance(final ProcessInstance processInstance) {
        processStore.save(processInstance);
    }
}
