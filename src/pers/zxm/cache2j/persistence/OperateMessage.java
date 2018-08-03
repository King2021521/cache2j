package pers.zxm.cache2j.persistence;

import java.util.Objects;

/**
 * @Author
 * @Description
 * @Date Create in 下午 1:53 2018/8/2 0002
 */
public class OperateMessage<K,V> {
    private Operation operation;
    private K key;
    private V value;

    private OperateMessage() {
    }

    public OperateMessage<K,V> operate(Operation operation){
        this.operation = operation;
        return this;
    }

    public OperateMessage<K,V> key(K key){
        this.key = key;
        return this;
    }

    public OperateMessage<K,V> value(V value){
        this.value = value;
        return this;
    }

    public static OperateMessage newInstance(){
        return new OperateMessage();
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
        OperateMessage<?, ?> operateMessage = (OperateMessage<?, ?>) o;
        return operation == operateMessage.operation &&
                Objects.equals(key, operateMessage.key) &&
                Objects.equals(value, operateMessage.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(operation, key, value);
    }

    @Override
    public String toString() {
        return "OperateMessage{" +
                "operation=" + operation +
                ", key=" + key +
                ", value=" + value +
                '}';
    }
}
