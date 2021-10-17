package com.kafkaderprozess.storage.jpa;

import com.pettersystems.prozess.api.model.Identifier;
import com.pettersystems.prozess.processengine.model.instance.ProcessInstance;
import com.pettersystems.prozess.processengine.runtime.storage.ProcessStore;
import io.vavr.control.Option;
import org.springframework.stereotype.Component;

@Component
public class JPAProcessStore implements ProcessStore {
    @Override
    public void save(ProcessInstance processInstance) {

    }

    @Override
    public Option<ProcessInstance> getProcessInstance(Identifier identifier) {
        return null;
    }
}
