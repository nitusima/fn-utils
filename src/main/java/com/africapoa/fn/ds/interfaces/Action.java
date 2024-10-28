package com.africapoa.fn.ds.interfaces;


public interface Action<T> {
    void apply(T t) throws Exception;
}

