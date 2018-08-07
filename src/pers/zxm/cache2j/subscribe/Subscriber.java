package pers.zxm.cache2j.subscribe;

/**
 * @Author
 * @Description
 * @Date Create in 上午 10:43 2018/8/3 0003
 */
public interface Subscriber<T> {
    void consume(Message<T> message);
}
