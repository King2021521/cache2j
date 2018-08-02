package pers.zxm.cache2j.persistence;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author zxm
 * @Description
 * @Date Create in 上午 9:51 2018/7/27 0027
 */
public class MessageQueue<K,V> {
    private ConcurrentLinkedQueue<Message<K,V>> queue;

    public MessageQueue(){
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void add(Message message){
        this.queue.add(message);
    }

    public void add(K key, V value){
        Message<K,V> message = Message.newInstance()
        .operate(Operation.INSERT)
        .key(key)
        .value(value);

        this.add(message);
    }

    public void remove(Object key){
        Message<K,V> message = Message.newInstance()
        .operate(Operation.REMOVE)
        .key(key);

        this.add(message);
    }

    public Message<K, V> poll(){
        return this.queue.poll();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
