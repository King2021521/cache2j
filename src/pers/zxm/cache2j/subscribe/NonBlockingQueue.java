package pers.zxm.cache2j.subscribe;

import pers.zxm.cache2j.common.Assert;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author
 * @Description a unbounded ,non-blocking and thread safety queue,
 * as a buffer pool in memory.
 * @Date Create in 下午 1:55 2018/8/3 0003
 */
public class NonBlockingQueue<R> {
    private final ConcurrentLinkedQueue<Message<R>> concurrentLinkedQueue;

    public NonBlockingQueue(){
        this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    }

    public boolean add(Message<R> element){
        if(Assert.notNull(element)){
            return concurrentLinkedQueue.add(element);
        }
        return false;
    }

    public Message<R> poll(){
        return concurrentLinkedQueue.poll();
    }

    public Message<R> peek(){
        return concurrentLinkedQueue.peek();
    }

    public boolean isEmpty(){
        return concurrentLinkedQueue.isEmpty();
    }
}
