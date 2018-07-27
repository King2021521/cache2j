package pers.zxm.cache2j.monitor;

import pers.zxm.cache2j.common.Constant;
import pers.zxm.cache2j.core.Cache;
import pers.zxm.cache2j.core.CacheObject;
import pers.zxm.cache2j.listener.CacheListener;
import pers.zxm.cache2j.listener.Payload;
import pers.zxm.cache2j.persistence.MessageQueue;
import pers.zxm.cache2j.persistence.Operation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A cache monitor base on maximum capacity{@link Cache}
 * 当缓存中对象的数量大于用户设置的某个阈值（maximum）时，内部执行淘汰缓存的线程开始执行淘汰数据，具体的策略是:
 * 根据缓存对象的LastAccessTime做排序，淘汰最近使用频率最低的 maximum*factor+totalSize-maximum个对象。
 * @param <K>
 * @param <V>
 * @author zxm
 * @since 2018-01-26
 */
public class CapMonitor<K, V> implements Monitor, Runnable {
    private final ReadWriteLock stateLock = new ReentrantReadWriteLock();

    /**
     * the max capacity
     */
    private int maximum;

    private double clearFactor;
    /**
     * Interval times Milliseconds
     */
    private long expirationIntervalMillis;

    private boolean running = false;

    private Thread workerThread;

    private ConcurrentHashMap<K, CacheObject<K, V>> delegate;

    private CopyOnWriteArrayList<CacheListener<K, V>> listeners;

    private MessageQueue messageQueue;

    public CapMonitor(Cache<K, V> cache) {
        this.delegate = cache.getDelegate();
        this.listeners = cache.getListeners();

        this.maximum = cache.getCacheBuilder().getMaximum();
        this.clearFactor = cache.getCacheBuilder().getFactor();
        this.expirationIntervalMillis = cache.getCacheBuilder().getInterval();

        this.messageQueue = cache.getQueue();

        workerThread = new Thread(this, "CacheCapMonitor");
        workerThread.setDaemon(true);

        this.start();
    }

    @Override
    public void run() {
        while (running) {
            processMonitor();

            try {
                Thread.sleep(expirationIntervalMillis);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void processMonitor() {
        int size = delegate.size();

        if (size >= maximum) {
            Map<Long, Object> sortMap = new TreeMap<>();
            for (CacheObject<K, V> object : delegate.values()) {
                sortMap.put(object.getLastAccessTime(), object.getKey());
            }

            Set<Long> keySet = sortMap.keySet();
            Iterator<Long> it = keySet.iterator();
            int count = 0;
            int clearSize = (int) (maximum * clearFactor) + size - maximum;
            while ((count < clearSize) && it.hasNext()) {
                Object v = sortMap.get(it.next());

                for (CacheListener<K, V> listener : listeners) {
                    listener.callback(new Payload<>((K) v,delegate.get(v).getValue(),delegate.get(v).getInitializationTime()));
                }

                delegate.remove(v);
                if(this.messageQueue != null){
                    this.messageQueue.remove(v);
                }
                count++;
            }
        }
    }

    @Override
    public void start() {
        stateLock.writeLock().lock();
        doWriteLock();
    }

    @Override
    public void stop() {
        stateLock.writeLock().lock();

        try {
            if (running) {
                running = false;
                workerThread.interrupt();
            }
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    @Override
    public void startIfStop() {
        stateLock.readLock().lock();
        try {
            if (running) {
                return;
            }
        } finally {
            stateLock.readLock().unlock();
        }

        stateLock.writeLock().lock();
        doWriteLock();
    }

    private void doWriteLock() {
        try {
            if (!running) {
                running = true;
                workerThread.start();
            }
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isRunning() {
        stateLock.readLock().lock();

        try {
            return running;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    public double getClearFactor() {
        stateLock.readLock().lock();

        try {
            return clearFactor;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    public void setClearFactor(double clearFactor) {
        stateLock.writeLock().lock();

        try {
            this.clearFactor = clearFactor;
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    public int getMaximum() {
        stateLock.readLock().lock();

        try {
            return maximum;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    public void setMaximum(int maximum) {
        stateLock.writeLock().lock();

        try {
            this.maximum = maximum;
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    /**
     * Get the interval in which an object will live in the map before
     * it is removed.
     *
     * @return The time in Millisecond.
     */
    public long getExpirationInterval() {
        stateLock.readLock().lock();

        try {
            return expirationIntervalMillis;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    /**
     * Set the interval in which an object will live in the map before
     * it is removed.
     *
     * @param expirationInterval The time in Millisecond
     */
    public void setExpirationInterval(long expirationInterval) {
        stateLock.writeLock().lock();

        try {
            this.expirationIntervalMillis = expirationInterval;
        } finally {
            stateLock.writeLock().unlock();
        }
    }

}
