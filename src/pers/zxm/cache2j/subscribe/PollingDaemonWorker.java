package pers.zxm.cache2j.subscribe;

/**
 * @Author
 * @Description
 * @Date Create in 下午 3:12 2018/8/7 0007
 */

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 后台任务,每条消息给每个订阅者都投递一遍
 */
public class PollingDaemonWorker<T> implements DaemonWorker {
    private static final int WAITING_MILLS = 10;

    private Thread workerThread;

    private final CopyOnWriteArrayList<Subscriber<T>> subscribers;
    private final NonBlockingQueue<T> nonBlockingQueue;

    public PollingDaemonWorker(CopyOnWriteArrayList<Subscriber<T>> subscribers, NonBlockingQueue<T> nonBlockingQueue) {
        this.subscribers = subscribers;
        this.nonBlockingQueue = nonBlockingQueue;

        workerThread = new Thread(this, "PollingDaemonWorker");
        workerThread.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            if (!nonBlockingQueue.isEmpty()) {
                Message<T> message = nonBlockingQueue.poll();
                subscribers.stream().forEach(subscriber -> subscriber.consume(message));
            } else {
                waiting();
            }
        }
    }

    @Override
    public void waiting() {
        try {
            Thread.sleep(WAITING_MILLS);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void start() {
        this.workerThread.start();
    }
}
