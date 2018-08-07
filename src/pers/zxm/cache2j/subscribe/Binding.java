package pers.zxm.cache2j.subscribe;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author zxm
 * @Description we define a Binding class to represent the relation of publishers and subscribers.
 * @Date Create in 下午 2:38 2018/8/6 0006
 */
public class Binding<T> {
    private Publisher<T> publisher;
    private final CopyOnWriteArrayList<Subscriber<T>> subscribers;

    public Binding(){
        this.subscribers = new CopyOnWriteArrayList<>();
    }

    public Binding(Publisher<T> publisher){
        this.publisher = publisher;
        this.subscribers = new CopyOnWriteArrayList<>();
    }

    public Binding(Publisher<T> publisher, List<Subscriber<T>> subscribers){
        this.publisher = publisher;
        this.subscribers = (CopyOnWriteArrayList<Subscriber<T>>) subscribers;
    }

    public Binding subcribe(Subscriber<T> subscriber){
        this.subscribers.add(subscriber);
        return this;
    }

    public Binding publish(Publisher<T> publisher){
        this.publisher = publisher;
        return this;
    }

    public Publisher<T> getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher<T> publisher) {
        this.publisher = publisher;
    }

    public CopyOnWriteArrayList<Subscriber<T>> getSubscribers() {
        return subscribers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Binding<?> binding = (Binding<?>) o;
        return Objects.equals(publisher, binding.publisher) &&
                Objects.equals(subscribers, binding.subscribers);
    }

    @Override
    public int hashCode() {

        return Objects.hash(publisher, subscribers);
    }
}
