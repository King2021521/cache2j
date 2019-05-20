package pers.zxm.cache2j.subscribe;

import java.util.UUID;

/**
 * @Author
 * @Description
 * @Date Create in 上午 10:05 2018/8/7 0007
 */
public class NonBlockingPublisher<T> extends Publisher<T> {

    public NonBlockingPublisher() {
        super.nonBlockingQueue = new NonBlockingQueue<>();
    }

    @Override
    public boolean publish(Message<T> message) {
        if (message != null) {
            if (null == message.getMessageId()) {
                message.setMessageId(createMessageId());
            }

            return nonBlockingQueue.add(message);
        }
        return false;
    }

    private String createMessageId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
