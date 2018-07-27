package pers.zxm.cache2j.persistence;

import pers.zxm.cache2j.common.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author zxm
 * @Description
 * @Date Create in 上午 9:51 2018/7/27 0027
 */
public class MessageQueue {
    private ConcurrentLinkedQueue<Map> queue;

    public MessageQueue(){
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void add(Map opertion){
        this.queue.add(opertion);
    }

    public void insert(Object key, Object value){
        Map operation = new HashMap();
        operation.put(Constant.OPERATION_KEY, Operation.INSERT.name());
        operation.put(Constant.KEY_NAME, key);
        operation.put(Constant.VALUE_NAME, value);
        add(operation);
    }

    public void remove(Object key){
        Map operation = new HashMap();
        operation.put(Constant.OPERATION_KEY, Operation.REMOVE.name());
        operation.put(Constant.KEY_NAME, key);
        add(operation);
    }

    public Map poll(){
        return this.queue.poll();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
