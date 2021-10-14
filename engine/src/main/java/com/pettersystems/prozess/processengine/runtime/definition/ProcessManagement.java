package com.pettersystems.prozess.processengine.runtime.definition;

import com.pettersystems.prozess.api.model.Identifier;
import com.pettersystems.prozess.processengine.model.definition.Event;
import com.pettersystems.prozess.processengine.model.definition.Node;
import com.pettersystems.prozess.processengine.model.definition.ProcessDefinition;
import com.pettersystems.prozess.processengine.model.definition.Relation;
import com.pettersystems.prozess.processengine.model.instance.EventInstance;
import com.pettersystems.prozess.processengine.model.instance.ProcessInstance;
import com.pettersystems.prozess.processengine.runtime.EventMapper;
import com.pettersystems.prozess.processengine.runtime.instance.WaitingEvent;
import com.pettersystems.prozess.processengine.runtime.storage.EventStore;
import io.vavr.control.Option;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessManagement {

    private final EventMapper eventMapper;

    private final List<EventStore> eventStores;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Map<Identifier, ProcessDefinition> processDefinitions = new HashMap<>();

    private Map<Identifier, Relation> relationMap = new HashMap<>();

    private Map<Identifier, Event> eventMap = new HashMap<>();

    private Map<Identifier, Node> nodeMap = new HashMap<>();


    public void addProcessDefinition(@NonNull final ProcessDefinition processDefinition,
                                     final boolean informedFromOtherEngine) {
        final Identifier identifier = processDefinition.getIdentifier();
        lock.writeLock().lock();
        try {
            log.info("Updating process engine with process {}", identifier);
            eventMapper.feedEventMapperWithNewEvents(processDefinition);
            processDefinitions.put(identifier, processDefinition);
        } finally {
            lock.writeLock().unlock();
        }
        if(!informedFromOtherEngine) {
            informOtherProcessEngines(identifier, processDefinition);
        }
    }

    private void informOtherProcessEngines(final Identifier identifier,
                                           final ProcessDefinition processDefinition) {
        log.info("Informing other process engines in cluster about update of process definition for {}", identifier);
        // TODO: send new process definition to other process engine instances as well
    }

    public void loadProcessDefinitions() {
        // TODO: load process definitions from process definition storage medium
    }

    public ProcessDefinition getProcessDefinition(@NonNull final Identifier processDefinitionIdentifier) {
        lock.readLock().lock();
        try {
            log.debug("Retrieving process definition with identifier {}", processDefinitionIdentifier);
            return processDefinitions.get(processDefinitionIdentifier);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Relation getRelation(final Identifier identifier) {
        lock.readLock().lock();
        try {
            return relationMap.get(identifier);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Event getEvent(final Identifier identifier) {
        lock.readLock().lock();
        try {
            return eventMap.get(identifier);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Node getNode(final Identifier identifier) {
        lock.readLock().lock();
        try {
            return nodeMap.get(identifier);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<WaitingEvent> getWaitingEvent(final EventInstance instance) {
        return eventStores.stream()
                .map(eventStore -> eventStore.getWaitingEvent(instance))
                .filter(waitingEvent -> waitingEvent.isDefined())
                .map(waitingEvent -> waitingEvent.get())
                .collect(Collectors.toList());
    }
}
