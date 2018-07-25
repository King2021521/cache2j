package pers.zxm.cache2j.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCache<K,V> implements Map<K,V> {
    protected ConcurrentHashMap<K, CacheObject<K,V>> delegate;

    public int size(){
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    public abstract V get(Object key);

    public abstract V put(K key, V value);

    public V remove(Object key) {
        CacheObject<K,V> answer = delegate.remove(key);
        if (answer == null) {
            return null;
        }

        return answer.getValue();
    }

    public void putAll(Map<? extends K, ? extends V> inMap) {
        for (Entry<? extends K, ? extends V> e : inMap.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Deprecated
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public Set<Entry<K, V>> entrySet(){
        throw new UnsupportedOperationException();
    }
}
