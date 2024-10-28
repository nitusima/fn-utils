package com.africapoa.fn.ds.interfaces;

public interface Accumulator<A, T> {
    A combine(A value1, T value2) throws Exception;
}
