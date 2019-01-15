package pers.zxm.cache2j.cleanup;

import pers.zxm.cache2j.core.CacheObject;
import pers.zxm.cache2j.core.Cache;
import pers.zxm.cache2j.listener.CacheListener;
import pers.zxm.cache2j.listener.Payload;
import pers.zxm.cache2j.persistence.MessageQueue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A Thread that monitors an {@link Cache} and will remove
 * elements that have passed the threshold.
 */
public class TtlCleanup<K, V> implements ICleanup, Runnable {
    private final ReadWriteLock stateLock = new ReentrantReadWriteLock();

    /**
     * time to live time Milliseconds
     */
    private long ttl;

    /**
     * Interval times Milliseconds
     */
    private long expirationIntervalMillis;

    private boolean running = false;

    private Thread workerThread;

    private ConcurrentHashMap<K, CacheObject<K, V>> delegate;

    private CopyOnWriteArrayList<CacheListener<K, V>> listeners;

    private MessageQueue messageQueue;

    /**
     * Creates a new instance of cleanup.
     */
    public TtlCleanup(Cache<K, V> cache) {
        this.delegate = cache.getDelegate();
        this.listeners = cache.getListeners();

        this.ttl = cache.getCacheBuilder().getTtl();
        this.expirationIntervalMillis = cache.getCacheBuilder().getInterval();

        this.messageQueue = cache.getQueue();

        workerThread = new Thread(this, "CacheTtlMonitor");
        workerThread.setDaemon(true);

        this.start();
    }

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
        long timeNow = System.currentTimeMillis();

        for (CacheObject<K, V> o : delegate.values()) {

            if (ttl <= 0) {
                continue;
            }

            long timeIdle = timeNow - o.getInitializationTime();

            if (timeIdle >= ttl) {
                delegate.remove(o.getKey());

                if(this.messageQueue != null){
                    messageQueue.remove(o.getKey());
                }

                for (CacheListener<K, V> listener : listeners) {
                    listener.callback(new Payload<>(o.getKey(), o.getValue(), o.getInitializationTime()));
                }
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

    /**
     * Returns the Time-to-live value.
     *
     * @return The time-to-live (Milliseconds)
     */
    public long getTimeToLive() {
        stateLock.readLock().lock();

        try {
            return ttl;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    /**
     * Update the value for the time-to-live
     *
     * @param ttl The time-to-live (Milliseconds)
     */
    public void setTimeToLive(long ttl) {
        stateLock.writeLock().lock();

        try {
            this.ttl = ttl;
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
