package com.africapoa.fn.ds;

import com.africapoa.fn.ds.interfaces.Accumulator;
import com.africapoa.fn.ds.interfaces.Action;
import com.africapoa.fn.ds.interfaces.Function;
import com.africapoa.fn.ds.interfaces.Producer;
import com.africapoa.fn.ds.interfaces.Predicate;
import com.africapoa.fn.ds.interfaces.Runnable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import static com.africapoa.fn.utils.Log.log;

/**
 * Class FnList provides functional operations on a list of items.
 * <p>
 * WARNING: This class is designed to swallow both NULLS and Exceptions occurring during its operations.
 * Any exceptions thrown during operations such as map, filter, etc. will be caught
 * and will not propagate to the calling code. Instead, all errors are logged
 * <p>
 * This behavior ensures smooth operation of the pipeline but can potentially hide issues
 * or unexpected behaviors. When debugging or troubleshooting, be sure to check the logs
 * for any relevant error messages from this class.
 * <p>
 * It's important to be aware of this when using this class, especially if
 * operations have side effects or if there's a need to know about any failures.
 */
@SuppressWarnings({"unchecked","unused"})
public class FnList<T> implements Iterable<T>{

    private final LazyIterator<T> lazy;

    private FnList(LazyIterator<T> lazyIterator){
        lazy = lazyIterator.copy();
    }

    public static <T> FnList<T> from(Producer<T> producer){
       return new FnList<>(new LazyIterator<>(producer));
    }
    public static <T> FnList<T> from(T[] data) {
        return new FnList<>(new LazyIterator<>(Arrays.asList(data).iterator()::next));
    }

    public static <T> FnList<T> from(Collection<T> collection) {
        return new FnList<>(new LazyIterator<>(collection.iterator()::next));
    }

    public static <T> FnList<T> generate(Function<Integer,T>generator){
        Mutable<Integer> mutable=new Mutable<>(0);
        return FnList.from( ()->{
            try {
                T t = generator.invoke(mutable.value++);
                if (t == null) { throw new NoSuchElementException();}
                return t;
            }catch (IndexOutOfBoundsException e){
                throw new NoSuchElementException();
            }
        });
    }
    public static FnList<Integer> range(int lower, int upper){
        return generate(x->lower+x<upper?lower+x:null);
    }

    public static FnList<Integer> range(int upper){
        return FnList.range(0,upper);
    }

    public FnList<T> filter(Predicate<T> predicate) {
        lazy.addOperation(input -> predicate.test(input) ? input : null);
        return new FnList<>(lazy);
    }

    public <S> FnList<S> map(Function<T, S> function) {
        lazy.addOperation(input -> (T) function.invoke(input));
        // Cast is safe here because we're just transforming data.
        return (FnList<S>) new FnList<>(lazy);
    }

    public <S> FnList<S> flatMap(Function<T, Collection<S>> function) {
        lazy.addOperation(input -> (T) function.invoke(input));
        return (FnList<S>)flat();
    }

    public <S> FnList<S> flatMapArrays(Function<T, S[]> function) {
        lazy.addOperation(input -> (T)Arrays.asList(function.invoke(input)));
        return (FnList<S>) flat();
    }

    public FnList<Object> flat() {
        LazyIterator<T> lazyCopy=lazy.copy();
        Mutable<Iterator<?>> mutable=new Mutable<>(null);
        return FnList.generate(i->{
            while(mutable.value == null || !mutable.value.hasNext()){
                T nextItem=lazyCopy.next();
                if(nextItem instanceof Collection){
                    mutable.value=((Collection<?>)nextItem).iterator();
                }
                else if (nextItem instanceof Object[]){
                    mutable.value= (Arrays.asList((T[])nextItem)).iterator();
                }
                else{ return nextItem;}
            }
            return mutable.value.next();
        });
    }
    public <A> A reduce(A identity,Accumulator<A, T> accumulator) {
        A result = identity;
        for (T item : this) {
            try{ result = accumulator.combine(result, item);}
            catch (Exception e){log(e);}
        }
        return result;
    }

    public FnList<T> unique(){ return unique(x->x); }

    public <S> FnList<T> unique(Function<T, S> giveId){
        Set<S> ids = new LinkedHashSet<>();ids.add(null);
        return this.filter(x->ids.add(ex(()->giveId.invoke(x))));
    }

    public <S> Map<S, List<T>> group(Function<T, S> giveId){
        Map<S,List<T>> map = new HashMap<>();
        return this.reduce(map,(m,t)->{
            S id=giveId.invoke(t);
            List<T> list =m.get(id);
            if(list==null) {
                list=new ArrayList<>();
                m.put(id,list);
            }
            list.add(t);
            return m;
        });
    }

    public List<T> list(){
        return reduce(new ArrayList<>(),(a,b)->{a.add(b);return a;});
    }

    public Set<T> toSet(){
        return new LinkedHashSet<>(unique().list());
    }

    public String toString(){
        return list().toString();
    }

    public String toJson(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        return gson.toJson(list());
    }

    public void forEachItem(Action<T> action) {
        for(T t:this){ ex(()->action.apply(t)); }
    }

    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }
    private  static <T> T ex(Producer<T> producer){
        try { return producer.produce();}
        catch (Exception e) {log(e);}
        return null;
    }
    private  static void ex(Runnable runnable){
        ex(()->{runnable.run(); return null;});
    }

    public FnList<T> prune() {
        return filter(it -> it != null
                && (!(it instanceof String) || !((String) it).trim().isEmpty())
                && (!(it instanceof Collection) || !((Collection<?>) it).isEmpty())
                && (!(it instanceof Map) || !((Map<?, ?>) it).isEmpty())
                && (!(it instanceof CharSequence) || ((CharSequence) it).length()!=0)
                && ( !it.getClass().isArray() || java.lang.reflect.Array.getLength(it) > 0 )
        );
    }

    public @NotNull Iterator<T> iterator() { return lazy;}

    public T first() {
        while(lazy.hasNext()){
            T val=lazy.next();
            if(val!=null) return val;
        }
        return null;
    }
}
