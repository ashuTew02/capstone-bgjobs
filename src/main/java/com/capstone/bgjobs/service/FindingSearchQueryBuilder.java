package com.capstone.bgjobs.service;

import java.util.List;
import java.util.stream.Collectors;

import com.capstone.bgjobs.model.FindingSeverity;
import com.capstone.bgjobs.model.FindingState;
import com.capstone.bgjobs.model.Tool;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;

public class FindingSearchQueryBuilder {

    private final BoolQuery.Builder builder;

    public FindingSearchQueryBuilder() {
        this.builder = new BoolQuery.Builder();
    }

    /**
     * Filter by a list of toolTypes (OR logic within this list).
     */
    public FindingSearchQueryBuilder withToolTypes(List<Tool> toolTypes) {
        if (toolTypes != null && !toolTypes.isEmpty()) {
            // Convert each enum to a FieldValue
            List<FieldValue> fieldValues = toolTypes.stream()
                .map(Enum::name)
                .map(FieldValue::of)
                .collect(Collectors.toList());
            
            // Use a terms query on the "toolType.keyword" field
            builder.must(m -> m.terms(tq -> tq
                .field("toolType.keyword")
                .terms(TermsQueryField.of(tf -> tf.value(fieldValues)))
            ));
        }
        return this;
    }

    /**
     * Filter by a list of severities.
     */
    public FindingSearchQueryBuilder withSeverities(List<FindingSeverity> severities) {
        if (severities != null && !severities.isEmpty()) {
            List<FieldValue> fieldValues = severities.stream()
                .map(Enum::name)
                .map(FieldValue::of)
                .collect(Collectors.toList());
            
            builder.must(m -> m.terms(tq -> tq
                .field("severity.keyword")
                .terms(TermsQueryField.of(tf -> tf.value(fieldValues)))
            ));
        }
        return this;
    }

    /**
     * Filter by a list of states.
     */
    public FindingSearchQueryBuilder withStates(List<FindingState> states) {
        if (states != null && !states.isEmpty()) {
            List<FieldValue> fieldValues = states.stream()
                .map(Enum::name)
                .map(FieldValue::of)
                .collect(Collectors.toList());
            
            builder.must(m -> m.terms(tq -> tq
                .field("state.keyword")
                .terms(TermsQueryField.of(tf -> tf.value(fieldValues)))
            ));
        }
        return this;
    }

    /**
     * Filter by a single ID (exact match).
     */
    public FindingSearchQueryBuilder withId(String id) {
        if (id != null) {
            builder.must(m -> m.term(t -> t
                .field("id.keyword")
                .value(id)
            ));
        }
        return this;
    }

    /**
     * Build the final BoolQuery.
     */
    public BoolQuery build() {
        return builder.build();
    }
}
