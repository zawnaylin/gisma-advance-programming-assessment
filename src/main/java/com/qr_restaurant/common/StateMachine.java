package com.qr_restaurant.common;

import java.util.*;

/**
 * The transitions an entity's status may take, kept in one place so the rules are stated
 * once and checked the same way everywhere.
 *
 * @param <S> the status enum
 */
public final class StateMachine<S extends Enum<S>> {

    private final Map<S, Set<S>> allowedTransitions;

    private StateMachine(Map<S, Set<S>> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    /**
     * @return whether the status may move from {@code from} to {@code to}
     */
    public boolean canTransition(S from, S to) {
        Set<S> allowed = allowedTransitions.get(from);
        return allowed != null && allowed.contains(to);
    }

    /**
     * Checks a transition before it is applied.
     *
     * @throws IllegalStateException if the status may not move from {@code from} to {@code to}
     */
    public void validate(S from, S to) {
        if (!canTransition(from, to)) {
            throw new IllegalStateException("Cannot transition from " + from + " to " + to);
        }
    }

    /**
     * @param type the status enum to describe
     * @return a builder to declare the allowed transitions with
     */
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
        /**
         * Allows moving from one status to any of the given ones.
         *
         * @return this builder
         */
        public final Builder<S> allow(S from, S... to) {
            allowedTransitions
                    .computeIfAbsent(from, key -> EnumSet.noneOf(type))
                    .addAll(Arrays.asList(to));
            return this;
        }

        /**
         * @return a state machine with the transitions declared so far
         */
        public StateMachine<S> build() {
            return new StateMachine<>(allowedTransitions);
        }
    }
}