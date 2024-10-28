package com.africapoa.fn.ds.interfaces;

public interface Producer<T> {
    T produce() throws Exception;
}
