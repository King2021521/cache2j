package pers.zxm.cache2j.subscribe;

/**
 * @Author
 * @Description
 * @Date Create in 下午 3:37 2018/8/3 0003
 */
public class DefaultSubscriber<T> implements Subscriber<T> {
    @Override
    public void consume(Message<T> message) {
        System.out.println(message.getPayload());
    }
}
