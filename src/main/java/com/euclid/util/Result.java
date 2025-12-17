package com.euclid.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A type-safe result type that represents either success or failure.
 * This eliminates null checks and makes error handling explicit.
 *
 * @param <T> The type of the success value
 * @param <E> The type of the error value
 */
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

    /**
     * Creates a successful result.
     *
     * @param value The success value
     * @param <T> The type of the success value
     * @param <E> The type of the error value
     * @return A Success result
     */
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a failed result.
     *
     * @param error The error value
     * @param <T> The type of the success value
     * @param <E> The type of the error value
     * @return A Failure result
     */
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }

    /**
     * Checks if this result is a success.
     *
     * @return true if success, false otherwise
     */
    boolean isSuccess();

    /**
     * Checks if this result is a failure.
     *
     * @return true if failure, false otherwise
     */
    boolean isFailure();

    /**
     * Gets the value if successful.
     *
     * @return Optional containing the value if success, empty otherwise
     */
    Optional<T> getValue();

    /**
     * Gets the error if failed.
     *
     * @return Optional containing the error if failure, empty otherwise
     */
    Optional<E> getError();

    /**
     * Maps the success value to a new type.
     *
     * @param mapper The mapping function
     * @param <U> The new success type
     * @return A new Result with the mapped value
     */
    <U> Result<U, E> map(Function<? super T, ? extends U> mapper);

    /**
     * Represents a successful result.
     */
    record Success<T, E>(T value) implements Result<T, E> {
        public Success {
            Objects.requireNonNull(value, "Success value cannot be null");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public Optional<T> getValue() {
            return Optional.of(value);
        }

        @Override
        public Optional<E> getError() {
            return Optional.empty();
        }

        @Override
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return new Success<>(mapper.apply(value));
        }
    }

    /**
     * Represents a failed result.
     */
    record Failure<T, E>(E error) implements Result<T, E> {
        public Failure {
            Objects.requireNonNull(error, "Error value cannot be null");
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public Optional<T> getValue() {
            return Optional.empty();
        }

        @Override
        public Optional<E> getError() {
            return Optional.of(error);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
            return (Result<U, E>) this;
        }
    }
}
