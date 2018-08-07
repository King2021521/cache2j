package pers.zxm.cache2j.subscribe;

/**
 * @Author
 * @Description
 * @Date Create in 下午 2:46 2018/8/6 0006
 */
public abstract class Publisher<T> {
    protected NonBlockingQueue<T> nonBlockingQueue;

    abstract boolean publish(Message<T> message);

    public NonBlockingQueue<T> getNonBlockingQueue() {
        return nonBlockingQueue;
    }

    public void setNonBlockingQueue(NonBlockingQueue<T> nonBlockingQueue) {
        this.nonBlockingQueue = nonBlockingQueue;
    }
}
