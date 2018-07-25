package pers.zxm.cache2j.core;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheObject<K,V> {
    private K key;

    private V value;

    private long initializationTime;

    private long lastAccessTime;

    private final ReadWriteLock lastAccessTimeLock = new ReentrantReadWriteLock();

    public CacheObject(K key, V value, long initializationTime, long lastAccessTime) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "An cache object cannot be null.");
        }

        this.key = key;
        this.value = value;
        this.initializationTime = initializationTime;
        this.lastAccessTime = lastAccessTime;
    }

    public long getInitializationTime(){
        return initializationTime;
    }

    public long getLastAccessTime() {
        lastAccessTimeLock.readLock().lock();

        try {
            return lastAccessTime;
        } finally {
            lastAccessTimeLock.readLock().unlock();
        }
    }

    public void setLastAccessTime(long lastAccessTime) {
        lastAccessTimeLock.writeLock().lock();

        try {
            this.lastAccessTime = lastAccessTime;
        } finally {
            lastAccessTimeLock.writeLock().unlock();
        }
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
