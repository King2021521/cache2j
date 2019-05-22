package pers.zxm.cache2j.core;

import pers.zxm.cache2j.*;
import pers.zxm.cache2j.listener.CacheListener;
import pers.zxm.cache2j.listener.Payload;
import pers.zxm.cache2j.cleanup.ICleanup;
import pers.zxm.cache2j.persistence.FlushDiskProcessor;
import pers.zxm.cache2j.persistence.LoadProcessor;
import pers.zxm.cache2j.persistence.MessageQueue;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Cache<K, V> extends AbstractCache<K, V> {
    private static Logger logger = Logger.newInstance(Cache.class);

    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final CopyOnWriteArrayList<CacheListener<K, V>> listeners;
    private ICleanup ICleanup;
    private CacheLoader<? super K, V> loader;
    private Stats stats;

    private MessageQueue queue;
    private FlushDiskProcessor flushDiskProcessor;

    private Long ttl;
    private Long interval;
    private Integer maximum;
    private Double factor;
    private boolean enableBlockingLoad;
    private Long blockTimeout;
    private CacheBuilder<? super K, ? super V> cacheBuilder;

    public Cache(CacheBuilder<? super K, ? super V> builder, CacheLoader<? super K, V> loader) {
        this(new ConcurrentHashMap<>(), new CopyOnWriteArrayList<>(), builder, loader);
    }

    public Cache(CacheBuilder<? super K, ? super V> builder) {
        this(builder, null);
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

        this.enableBlockingLoad = builder.getEnableBlockingLoad();
        this.blockTimeout = builder.getBlockTimeout();

        initCache(builder);
    }

    private void initCache(CacheBuilder<? super K, ? super V> builder) {
        if (builder.getListener() != null) {
            listeners.add(builder.getListener());
        }

        if (builder.getEnableFlushDisk()) {
            if (null == builder.getPath()) {
                throw new UnCheckNullException("dump path may be not null");
            }

            this.queue = new MessageQueue();
            this.flushDiskProcessor = newProcessor(builder.getProcessorType().type());

            Map bak = LoadProcessor.read(builder.getPath());
            if (null != bak && !bak.isEmpty()) {
                recovery(bak);
            }
        }

        this.ICleanup = builder.getType() == null ? null : newInstance(builder.getType().getType());
    }

    private void recovery(Map bak) {
        Set<K> keys = bak.keySet();
        for (K key : keys) {
            doPut(key, (V) bak.get(key));
        }
    }

    /**
     * uncheck null when value is null
     *
     * @param key
     * @return V
     * @throws UnCheckNullException
     * @throws LoadingFailException
     */
    public V get(Object key) throws LoadingFailException {
        return doGet(key);
    }

    /**
     * throw exception when value is null
     *
     * @param key
     * @return
     */
    public V getWithCheck(Object key) {
        V value = doGet(key);
        if (null == value) {
            throw new UnCheckNullException("cache object value can not is null");
        }
        return value;
    }

    private V doGet(Object key) {
        CacheObject<K, V> object = delegate.get(key);
        V value = beforeLoading(object);
        if (value != null) {
            return value;
        }

        if (loader != null) {
            return enableBlockingLoad == true ? blockingLoad(key) : load(key);
        }
        return null;
    }

    /**
     * 阻塞式加载缓存，当缓存过期瞬间，大量线程并发去从db加载数据，为了防止穿透，此处加载时使用阻塞机制，仅允许一个线程穿透到db，其余线程保持阻塞，
     * 当阻塞时间超过blockTimeout时，线程会抛出请求超时的异常。
     *
     * @param key
     * @exception LoadingFailException
     * @return
     */
    private V blockingLoad(Object key) {
        lockPolling();
        reentrantLock.lock();

        try {
            //双重检查
            CacheObject<K, V> object = delegate.get(key);
            V value = beforeLoading(object);
            if (value != null) {
                return value;
            }
            return load(key);
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 轮询锁
     */
    private void lockPolling(){
        if (reentrantLock.isLocked()) {
            long start = System.currentTimeMillis();
            while (reentrantLock.isLocked()) {
                if (!reentrantLock.isLocked()) {
                    break;
                }
                checkTimeout(start);
            }
        }
    }

    /**
     * 判断是否超时
     * @param start
     */
    private void checkTimeout(long start){
        if (System.currentTimeMillis() - start > blockTimeout) {
            throw new ThreadBlockingTimeoutException("current thread request timeout");
        }
    }

    /**
     * load value from data source,no blocking!
     *
     * @param key
     * @return
     */
    private V load(Object key) {
        V v;
        try {
            v = loader.load((K) key);
        } catch (Exception e) {
            throw new LoadingFailException("unable to load cache object");
        }

        resolveStats(v);
        if (v != null) {
            afterLoading(key, v);
        }

        return v;
    }

    private V beforeLoading(CacheObject<K, V> object) {
        if (object != null) {
            if (stats != null) {
                stats.hit();
            }

            object.setLastAccessTime(System.currentTimeMillis());
            return object.getValue();
        }
        return null;
    }

    private V afterLoading(Object key, V v) {
        doPut((K) key, v);
        CacheObject<K, V> object = delegate.get(key);
        object.setLastAccessTime(System.currentTimeMillis());
        return object.getValue();
    }

    private void resolveStats(V v) {
        if (stats != null) {
            if (v == null) {
                stats.miss();
            } else {
                stats.reload();
            }
        }
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
            if (this.ICleanup == null) {
                this.cleanup();
            }
        }
        return v;
    }

    private V doPut(K key, V value) {
        CacheObject<K, V> answer = delegate.put(key, new CacheObject<>(key, value, System.currentTimeMillis(), System.currentTimeMillis()));

        if (this.queue != null) {
            this.queue.add(key, value);
        }

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

    public ICleanup getICleanup() {
        return ICleanup;
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

    public MessageQueue getQueue() {
        return this.queue;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    private <T extends ICleanup> T newInstance(Class<T> type) {
        return reflect(type);
    }

    private <T extends FlushDiskProcessor> T newProcessor(Class<T> type) {
        return reflect(type);
    }

    private <T> T reflect(Class<T> clazz) {
        try {
            return clazz.getConstructor(Cache.class).newInstance(this);
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
                    if (this.queue != null) {
                        this.queue.remove(key);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
