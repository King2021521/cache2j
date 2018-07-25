package pers.zxm.cache2j.core;

/**
 * is a cache auto loading tool when cache is removed
 * @param <K>
 * @param <V>
 * @author zxm
 * @since 2018-01-25
 */
public abstract class CacheLoader<K,V> {
    public abstract V load(K key);
}
