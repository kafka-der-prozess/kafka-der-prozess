package com.pettersystems.prozess.processengine.runtime.storage;

import com.pettersystems.prozess.api.model.Identifier;
import com.pettersystems.prozess.processengine.model.instance.ProcessInstance;
import io.vavr.control.Option;

public interface ProcessStore {

    void save(final ProcessInstance processInstance);

    Option<ProcessInstance> getProcessInstance(Identifier identifier);
}
