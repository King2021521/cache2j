package pers.zxm.cache2j.core;

import pers.zxm.cache2j.common.Log;
import pers.zxm.cache2j.listener.CacheListener;
import pers.zxm.cache2j.listener.Payload;
import pers.zxm.cache2j.monitor.Monitor;
import pers.zxm.cache2j.LoadingFailException;
import pers.zxm.cache2j.Stats;
import pers.zxm.cache2j.UnCheckNullException;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Cache<K, V> extends AbstractCache<K, V> {
    private static Log logger = Log.newInstance(Cache.class);

    private final ReentrantLock lock = new ReentrantLock();
    private final CopyOnWriteArrayList<CacheListener<K, V>> listeners;
    private Monitor monitor;
    private CacheLoader<? super K, V> loader;
    private Stats stats;

    private Long ttl;
    private Long interval;
    private Integer maximum;
    private Double factor;
    private CacheBuilder<? super K, ? super V> cacheBuilder;

    public Cache(CacheBuilder<? super K, ? super V> builder, CacheLoader<? super K, V> loader) {
        this(new ConcurrentHashMap<>(), new CopyOnWriteArrayList<>(), builder, loader);
    }

    private Cache(ConcurrentHashMap<K, CacheObject<K, V>> delegate,
                  CopyOnWriteArrayList<CacheListener<K, V>> cacheListeners,
                  CacheBuilder<? super K, ? super V> builder,
                  CacheLoader<? super K, V> loader) {
        this.delegate = delegate;
        this.listeners = cacheListeners;
        this.cacheBuilder = builder;

        this.stats = builder.getStats();
        this.loader = loader;
        this.ttl = builder.getTtl();
        this.interval = builder.getInterval();
        this.maximum = builder.getMaximum();
        this.factor = builder.getFactor();

        if (builder.getListener() != null) {
            listeners.add(builder.getListener());
        }

        this.monitor = builder.getType() == null ? null : newInstance(builder.getType().getType());
    }

    /**
     * throw exception when value is null
     *
     * @param key
     * @return V
     * @throws UnCheckNullException
     * @throws LoadingFailException
     */
    public V get(Object key) throws LoadingFailException {
        CacheObject<K, V> object = delegate.get(key);

        if (object != null) {
            if (stats != null) {
                stats.hit();
            }

            object.setLastAccessTime(System.currentTimeMillis());
            return object.getValue();
        } else if (loader != null) {
            V v;
            try {
                v = loader.load((K) key);
            } catch (Exception e) {
                throw new LoadingFailException("unable to load cache object");
            }

            if (stats != null) {
                if (v == null) {
                    stats.miss();
                } else {
                    stats.reload();
                }
            }

            doPut((K) key, v);

            object = delegate.get(key);
            object.setLastAccessTime(System.currentTimeMillis());
            return object.getValue();
        }

        throw new UnCheckNullException("cache object value can not is null");
    }

    /**
     * put操作，当未指定Monitor时，默认执行清理，适用于读多写少的场景
     *
     * @param key
     * @param value
     * @return
     */
    public V put(K key, V value) {
        V v;
        try {
            v = doPut(key, value);
        } catch (IllegalArgumentException e) {
            throw e;
        } finally {
            if (this.monitor == null) {
                this.cleanup();
            }
        }
        return v;
    }

    private V doPut(K key, V value) {
        CacheObject<K, V> answer = delegate.put(key, new CacheObject<>(key, value, System.currentTimeMillis(), System.currentTimeMillis()));
        if (answer == null) {
            return null;
        }

        return answer.getValue();
    }

    protected Cache addListener(CacheListener<K, V> listener) {
        listeners.add(listener);
        return this;
    }

    public CopyOnWriteArrayList<CacheListener<K, V>> getListeners() {
        return this.listeners;
    }

    public void removeListener(CacheListener<K, V> listener) {
        listeners.remove(listener);
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public CacheBuilder<? super K, ? super V> getCacheBuilder() {
        return this.cacheBuilder;
    }

    public CacheLoader<? super K, V> getLoader() {
        return loader;
    }

    public String stats() {
        return stats.toString();
    }

    public ConcurrentHashMap<K, CacheObject<K, V>> getDelegate() {
        return this.delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    private <T extends Monitor> T newInstance(Class<T> type) {
        try {
            return type.getConstructor(Cache.class).newInstance(this);
        } catch (InstantiationException e) {
            logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage());
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 每次随机清理一个缓存
     */
    private void cleanup() {
        lock.lock();
        try {
            int size = delegate.size();

            if (this.maximum < Integer.MAX_VALUE) {
                if (size >= this.maximum) {
                    Object[] ks = delegate.keySet().toArray();
                    Object key = ks[(int) (Math.random() * ks.length)];
                    for (CacheListener<K, V> listener : listeners) {
                        listener.callback(new Payload<>((K) key, delegate.get(key).getValue(), delegate.get(key).getInitializationTime()));
                    }

                    delegate.remove(key);
                }
            }
        } finally {
            lock.unlock();
        }

    }
}
