
package com.africapoa.fn.ds.interfaces;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 * This is a functional interface whose functional method is {@link #test(Object)}.
 *
 * @param <T> the type of the input to the predicate
 */
@FunctionalInterface
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     * @throws Exception if unable to evaluate the predicate
     */
    boolean test(T t) throws Exception;
}
