package pers.zxm.cache2j.core;

/**
 * is a cache auto loading tool when cache is removed
 * @param <K>
 * @param <V>
 * @author zxm
 * @since 2018-01-25
 */
public interface CacheLoader<K,V> {
    V load(K key) throws InterruptedException;
}
