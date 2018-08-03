package pers.zxm.cache2j.subscribe;

import java.util.List;

/**
 * @Author
 * @Description
 * @Date Create in 上午 10:24 2018/8/3 0003
 */
public interface Channel<T> {
    boolean publish(Message<T> message);

    void subscribe(Consumer consumer);

    void subscribeBatch(List<Consumer> consumers);

    boolean cancelSubcribe(Consumer consumer);
}
