package pers.zxm.cache2j.persistence;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author zxm
 * @Description
 * @Date Create in 上午 9:51 2018/7/27 0027
 */
public class MessageQueue<K,V> {
    private ConcurrentLinkedQueue<OperateMessage<K,V>> queue;

    public MessageQueue(){
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void add(OperateMessage operateMessage){
        this.queue.add(operateMessage);
    }

    public void add(K key, V value){
        OperateMessage<K,V> operateMessage = OperateMessage.newInstance()
        .operate(Operation.INSERT)
        .key(key)
        .value(value);

        this.add(operateMessage);
    }

    public void remove(Object key){
        OperateMessage<K,V> operateMessage = OperateMessage.newInstance()
        .operate(Operation.REMOVE)
        .key(key);

        this.add(operateMessage);
    }

    public OperateMessage<K, V> poll(){
        return this.queue.poll();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
