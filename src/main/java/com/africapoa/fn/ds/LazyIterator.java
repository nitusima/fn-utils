package com.africapoa.fn.ds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import  com.africapoa.fn.ds.interfaces.Producer;
import  com.africapoa.fn.ds.interfaces.Function;
import  static com.africapoa.fn.utils.Log.log;

public class LazyIterator<T> implements Iterator<T> {
    private final Producer<T> dataProvider;
    private final List<Function<T,T>> operations;
    private T nextItem;
    public LazyIterator(Producer<T> dataProvider,List<Function<T,T>> operations) {
        this.dataProvider = dataProvider;
        this.operations=new ArrayList<>(operations);
    }
    public LazyIterator(Producer<T> dataProvider) {
        this.dataProvider = dataProvider;
        this.operations=new ArrayList<>();
    }

    public LazyIterator<T> copy(){
        return new LazyIterator<>(dataProvider,operations);
    }

    @Override
    public boolean hasNext() {
        if(nextItem!=null){ return true; }
        try {
            T data=dataProvider.produce();
            int maxTries=0;
            data= processItem(data);
            while(maxTries++<10 && data==null){
                data= processItem(dataProvider.produce());
            }
            nextItem=data;
            return nextItem!=null;
        }
        catch (NoSuchElementException e) { return false;}
        catch (Exception e) { log(e); return false;}
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T current= nextItem;
        nextItem=null;
        return current;
    }

    public T processItem(T item) {
        T processed=item;
        for (Function<T, T> operation : operations) {
            T copy=processed;
            if(copy==null)return null;
            processed = ex(() -> operation.invoke(copy));
        }
        return processed;
    }

    private  static <T> T ex(Producer<T> producer){
        try { return producer.produce();}
        catch (Exception e) {log(e);}
        return null;
    }

    public void addOperation(Function<T,T> operation) {
        operations.add(operation) ;
    }
}
