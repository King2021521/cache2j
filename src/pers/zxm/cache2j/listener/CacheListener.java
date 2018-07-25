package pers.zxm.cache2j.listener;

/**
 * 对象缓存的监听器，当缓存被淘汰时，内部会回调监听器的callback方法，返回缓存对象的相关信息
 * The listener of the object cache, when the cache is eliminated,
 * the callback method of the listener is callback, and the related
 * information of the cache object is returned
 * @param <K>
 * @param <V>
 * @author zxm 1261608273@qq.com
 */
public interface CacheListener<K,V> {
    /**
     * callback method
     * @param payload cache object payload
     */
    void callback(Payload<K,V> payload);
}
