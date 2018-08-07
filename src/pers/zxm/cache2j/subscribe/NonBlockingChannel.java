package pers.zxm.cache2j.subscribe;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author
 * @Description
 * @Date Create in 上午 11:27 2018/8/3 0003
 */
public class NonBlockingChannel<T> implements Channel<T> {
    private Binding<T> binding;
    private final CopyOnWriteArrayList<Subscriber<T>> subscribers;
    private final NonBlockingQueue<T> nonBlockingQueue;
    private DaemonWorker<T> worker;

    private NonBlockingChannel(Binding<T> binding) {
        this.subscribers = binding.getSubscribers();
        this.nonBlockingQueue = binding.getPublisher().getNonBlockingQueue();
    }

    public static NonBlockingChannel initChannel(Binding binding) {
        return new NonBlockingChannel(binding);
    }

    public NonBlockingChannel<T> enable() {
        worker = new DaemonWorker<>(subscribers, nonBlockingQueue);
        worker.start();
        return this;
    }

    public Binding<T> getBinding() {
        return binding;
    }

    public void setBinding(Binding<T> binding) {
        this.binding = binding;
    }

    /**
     * 后台任务
     */
    class DaemonWorker<T> implements Runnable {
        private static final int WAITING_MILLS = 10;

        private Thread workerThread;

        private final CopyOnWriteArrayList<Subscriber<T>> subscribers;
        private final NonBlockingQueue<T> nonBlockingQueue;

        public DaemonWorker(CopyOnWriteArrayList<Subscriber<T>> subscribers, NonBlockingQueue<T> nonBlockingQueue) {
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
                    subscribers.stream().forEach(subscriber -> subscriber.consume(message));
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
