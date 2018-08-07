package pers.zxm.cache2j.subscribe;

import java.lang.reflect.InvocationTargetException;
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
    private WorkType workType;

    private NonBlockingChannel(Binding<T> binding) {
        this.subscribers = binding.getSubscribers();
        this.nonBlockingQueue = binding.getPublisher().getNonBlockingQueue();
    }

    public static NonBlockingChannel initChannel(Binding binding) {
        return new NonBlockingChannel(binding);
    }

    public NonBlockingChannel<T> workType(WorkType type) {
        this.workType = type;
        return this;
    }

    public NonBlockingChannel<T> enable() {
        worker = this.workType == null ? new PollingDaemonWorker<>(subscribers, nonBlockingQueue) : instance(this.workType.type());
        worker.start();
        return this;
    }

    public Binding<T> getBinding() {
        return binding;
    }

    public void setBinding(Binding<T> binding) {
        this.binding = binding;
    }

    private <R extends DaemonWorker<T>> R instance(Class<R> type) {
        try {
            return type.getConstructor(CopyOnWriteArrayList.class, NonBlockingQueue.class).newInstance(this.subscribers, this.nonBlockingQueue);
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
