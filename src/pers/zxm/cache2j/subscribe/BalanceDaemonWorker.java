package pers.zxm.cache2j.subscribe;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author
 * @Description
 * @Date Create in 下午 3:12 2018/8/7 0007
 */
public class BalanceDaemonWorker<T> implements DaemonWorker {

    private static final int WAITING_MILLS = 10;
    private static final int HEAD_POSITION = 0;

    private Thread workerThread;

    private final CopyOnWriteArrayList<Subscriber<T>> subscribers;
    private final NonBlockingQueue<T> nonBlockingQueue;

    public BalanceDaemonWorker(CopyOnWriteArrayList<Subscriber<T>> subscribers, NonBlockingQueue<T> nonBlockingQueue) {
        this.subscribers = subscribers;
        this.nonBlockingQueue = nonBlockingQueue;

        workerThread = new Thread(this, "BalanceDaemonWorker");
        workerThread.setDaemon(true);
    }

    @Override
    public void run() {
        int index = HEAD_POSITION;

        while (true) {
            if (!nonBlockingQueue.isEmpty()) {
                if (index <= subscribers.size() - 1) {

                    Message<T> message = nonBlockingQueue.poll();
                    Subscriber<T> subscriber = subscribers.get(index);
                    if (subscriber instanceof Subscriber) {
                        subscriber.consume(message);
                    }

                    index++;
                } else {
                    index = HEAD_POSITION;
                }
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
