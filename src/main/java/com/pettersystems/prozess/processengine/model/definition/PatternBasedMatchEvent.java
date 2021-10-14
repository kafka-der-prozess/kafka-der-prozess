package com.pettersystems.prozess.processengine.model.definition;

import java.util.List;

public class PatternBasedMatchEvent extends Event {

    private String patternProcessIdentifierExtractor;

    private String patternEventCondition;

    private List<String> variableExtraction;
}
