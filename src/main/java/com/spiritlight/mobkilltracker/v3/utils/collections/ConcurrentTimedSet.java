package com.spiritlight.mobkilltracker.v3.utils.collections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ConcurrentTimedSet<E> extends AbstractSet<E> {

    private final long delay;
    private final TimeUnit unit;

    private final Map<E, ObjectDescriptor<E>> map = new ConcurrentHashMap<>();

    private final ObjectDescriptor<E> EMPTY = new ObjectDescriptor<E>(null, 0) {
        @Override
        public boolean expired() {
            return true;
        }
    };

    public ConcurrentTimedSet(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = unit;
    }

    @Override
    public boolean add(E e) {
        ObjectDescriptor<E> element = new ObjectDescriptor<>(e, unit.toMillis(delay));
        return this.isEmpty(map.put(e, element));
    }

    @Override
    public boolean remove(Object o) {
        return this.isEmpty(map.remove(o) == null);
    }

    private boolean isEmpty(Object o) {
        if(o instanceof ObjectDescriptor) {
            return ((ObjectDescriptor<?>) o).expired();
        }
        return o == null;
    }

    @Override
    public boolean contains(Object o) {
        ObjectDescriptor<E> od = map.getOrDefault(o, EMPTY);
        if(od.expired()) {
            map.remove(o);
            return false;
        } else return true;
    }

    private boolean expired(E element) {
        return map.getOrDefault(element, EMPTY).expired();
    }

    @Override
    public Iterator<E> iterator() {
        map.entrySet().removeIf(entry -> entry == null || entry.getValue().expired());
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return (int) map.keySet().stream().filter(e -> !expired(e)).count();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    private static class ObjectDescriptor<E> {
        private final long age;
        private E element;

        private ObjectDescriptor(E element, long age) {
            this.age = System.currentTimeMillis() + age;
            this.element = element;
        }

        public E getElement() {
            if(this.expired()) expiry();
            return element;
        }

        private void expiry() {
            this.element = null;
        }

        public boolean expired() {
            return System.currentTimeMillis() > age;
        }

        @Override
        public String toString() {
            return "ObjectDescriptor{element=" + element.toString() + ",time=" + age + ",expired=" + this.expired() + "}";
        }
    }
}
