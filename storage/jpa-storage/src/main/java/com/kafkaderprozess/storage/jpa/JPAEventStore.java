package com.kafkaderprozess.storage.jpa;

import com.pettersystems.prozess.processengine.model.instance.EventInstance;
import com.pettersystems.prozess.processengine.runtime.instance.WaitingEvent;
import com.pettersystems.prozess.processengine.runtime.storage.EventStore;
import io.vavr.control.Option;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JPAEventStore implements EventStore {

    @Override
    public Option<WaitingEvent> getWaitingEvent(EventInstance instance) {
        return null;
    }

    @Override
    public void storeWaitingEvents(List<WaitingEvent> events) {

    }

    @Override
    public void storeUnsatisfiedEvent(EventInstance event) {

    }
}
