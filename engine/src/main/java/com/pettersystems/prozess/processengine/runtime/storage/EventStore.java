package com.pettersystems.prozess.processengine.runtime.storage;

import com.pettersystems.prozess.processengine.model.instance.EventInstance;
import com.pettersystems.prozess.processengine.runtime.instance.WaitingEvent;
import io.vavr.control.Option;

import java.util.List;

public interface EventStore {

    Option<WaitingEvent> getWaitingEvent(final EventInstance instance);

    void storeWaitingEvents(final List<WaitingEvent> events);

    void storeUnsatisfiedEvent(EventInstance event);
}
