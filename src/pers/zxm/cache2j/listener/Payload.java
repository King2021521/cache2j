package pers.zxm.cache2j.listener;

import java.util.Objects;

/**
 *
 * The parameters of the listener's callback method
 * @param <K>
 * @param <V>
 * @author zxm
 * @since 2018.01.30
 */
public class Payload<K,V> {
    private K key;

    private V value;

    private long initAccessTime;

    public Payload(K key, V value, long initAccessTime){
        this.key = key;
        this.value = value;
        this.initAccessTime = initAccessTime;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public long getInitAccessTime() {
        return initAccessTime;
    }

    public void setInitAccessTime(long initAccessTime) {
        this.initAccessTime = initAccessTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload<?, ?> payload = (Payload<?, ?>) o;
        return initAccessTime == payload.initAccessTime &&
                Objects.equals(key, payload.key) &&
                Objects.equals(value, payload.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(key, value, initAccessTime);
    }

    @Override
    public String toString() {
        return "Payload{" +
                "key=" + key +
                ", value=" + value +
                ", initAccessTime=" + initAccessTime +
                '}';
    }
}
