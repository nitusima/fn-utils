package com.africapoa.fn.ds.interfaces;

public interface Function<T, R> {
    R invoke(T t) throws Exception;
}
