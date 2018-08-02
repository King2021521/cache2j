package pers.zxm.cache2j.persistence;

import java.util.Objects;

/**
 * @Author
 * @Description
 * @Date Create in 下午 1:53 2018/8/2 0002
 */
public class Message<K,V> {
    private Operation operation;
    private K key;
    private V value;

    private Message() {
    }

    public Message<K,V> operate(Operation operation){
        this.operation = operation;
        return this;
    }

    public Message<K,V> key(K key){
        this.key = key;
        return this;
    }

    public Message<K,V> value(V value){
        this.value = value;
        return this;
    }

    public static Message newInstance(){
        return new Message();
    }

    public Operation getOperation() {
        return operation;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message<?, ?> message = (Message<?, ?>) o;
        return operation == message.operation &&
                Objects.equals(key, message.key) &&
                Objects.equals(value, message.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(operation, key, value);
    }

    @Override
    public String toString() {
        return "Message{" +
                "operation=" + operation +
                ", key=" + key +
                ", value=" + value +
                '}';
    }
}
