package com.qr_restaurant.common;

import java.util.*;

public final class StateMachine<S extends Enum<S>> {

    private final Map<S, Set<S>> allowedTransitions;

    private StateMachine(Map<S, Set<S>> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public void validate(S from, S to) {
        Set<S> allowed = allowedTransitions.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new IllegalStateException("Cannot transition from " + from + " to " + to);
        }
    }

    public static <S extends Enum<S>> Builder<S> builder(Class<S> type) {
        return new Builder<>(type);
    }

    public static final class Builder<S extends Enum<S>> {
        private final Class<S> type;
        private final Map<S, Set<S>> allowedTransitions;

        private Builder(Class<S> type) {
            this.type = type;
            this.allowedTransitions = new EnumMap<>(type);
        }

        @SafeVarargs
        public final Builder<S> allow(S from, S... to) {
            allowedTransitions
                    .computeIfAbsent(from, key -> EnumSet.noneOf(type))
                    .addAll(Arrays.asList(to));
            return this;
        }

        public StateMachine<S> build() {
            return new StateMachine<>(allowedTransitions);
        }
    }
}