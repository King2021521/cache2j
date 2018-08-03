package pers.zxm.cache2j.subscribe;

import pers.zxm.cache2j.common.Assert;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author
 * @Description
 * @Date Create in 上午 11:27 2018/8/3 0003
 */
public class NonBlockingChannel<T> implements Channel<T> {
    private final CopyOnWriteArrayList<Consumer<T>> subscribers;
    private final NonBlockingQueue<T> nonBlockingQueue;
    private DaemonWorker<T> worker;

    private NonBlockingChannel() {
        this.subscribers = new CopyOnWriteArrayList<>();
        this.nonBlockingQueue = new NonBlockingQueue<>();
    }

    public static NonBlockingChannel initChannel() {
        return new NonBlockingChannel();
    }

    public NonBlockingChannel<T> subscribeOne(Consumer consumer) {
        this.subscribe(consumer);
        return this;
    }

    public NonBlockingChannel<T> subscribeMany(List<Consumer> consumers) {
        this.subscribeBatch(consumers);
        return this;
    }

    public NonBlockingChannel<T> enable() {
        worker = new DaemonWorker<>(subscribers, nonBlockingQueue);
        worker.start();
        return this;
    }

    @Override
    public boolean publish(Message<T> message) {
        if (Assert.notNull(message)) {
            if (null == message.getMessageId()) {
                message.setMessageId(createMessageId());
            }

            return nonBlockingQueue.add(message);
        }
        return false;
    }

    @Override
    public void subscribe(Consumer consumer) {
        if (null != consumer) {
            this.subscribers.add(consumer);
        }
    }

    @Override
    public void subscribeBatch(List<Consumer> consumers) {
        consumers.stream().filter(Assert::notNull).forEach(consumer -> subscribers.add(consumer));
    }

    @Override
    public boolean cancelSubcribe(Consumer consumer) {
        return subscribers.remove(consumer);
    }

    private String createMessageId(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 后台任务
     */
    class DaemonWorker<T> implements Runnable {
        private static final int WAITING_MILLS = 10;

        private Thread workerThread;

        private final CopyOnWriteArrayList<Consumer<T>> subscribers;
        private final NonBlockingQueue<T> nonBlockingQueue;

        public DaemonWorker(CopyOnWriteArrayList<Consumer<T>> subscribers, NonBlockingQueue<T> nonBlockingQueue) {
            this.subscribers = subscribers;
            this.nonBlockingQueue = nonBlockingQueue;

            workerThread = new Thread(this, "DaemonWorker");
            workerThread.setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                if (!nonBlockingQueue.isEmpty()) {
                    Message<T> message = nonBlockingQueue.poll();
                    subscribers.stream().forEach(consumer -> consumer.consume(message));
                } else {
                    waiting();
                }
            }
        }

        private void waiting() {
            try {
                Thread.sleep(WAITING_MILLS);
            } catch (InterruptedException e) {
            }
        }

        public void start() {
            this.workerThread.start();
        }
    }

}
