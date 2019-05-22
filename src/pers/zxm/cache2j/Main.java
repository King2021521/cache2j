package pers.zxm.cache2j;

import pers.zxm.cache2j.core.Cache;
import pers.zxm.cache2j.listener.DefaultListener;
import pers.zxm.cache2j.cleanup.CleanupType;
import pers.zxm.cache2j.core.CacheBuilder;
import pers.zxm.cache2j.subscribe.*;
import pers.zxm.cache2j.support.Logger;

public class Main {
    public static Logger logger = Logger.newInstance(Main.class);

    public static void main(String[] args) throws Exception {
        Cache<String, Object> cache = testCache();

        T1 t1 = new T1(cache,"java");
        T1 t2 = new T1(cache,"java");
        t2.start();
        t1.start();
        Thread.sleep(2*1000);
        System.out.println(cache.get("java"));
        System.out.println(cache.stats());
    }

    public static void testPublishAndSubscribe() throws Exception {
        NonBlockingPublisher<String> publisher = new NonBlockingPublisher<>();
        Binding<String> binding = new Binding<>(publisher).subcribe(new DefaultSubscriber());
        Channel<String> channel = NonBlockingChannel.initChannel(binding)
                .workType(WorkType.BALANCE)
                .enable();

        for (int i = 0; i < 100; i++) {
            Message<String> message = new Message<>();
            message.setTag("test");
            message.setTimestamp(System.currentTimeMillis());
            message.setPayload("test publish message " + i);

            publisher.publish(message);
            Thread.sleep((long) (Math.random() * 1000));
        }
    }

    public static Cache<String, Object> testCache() throws Exception {
        Cache<String, Object> cache = CacheBuilder.newBuilder()
                .listener(new DefaultListener())
                .factor(0.1)
                .interval(1000)
                .ttl(50000)
                .maximum(1000)
                .monitor(CleanupType.LRU)
                .stats()
                //.flushProcessor(ProcessorType.FOS)
                //.path("D:\\cache2j.txt")
                .enableFlushDsk(false)
                .enableBlockingLoad(1000)
                .build(key -> {
                    Thread.sleep(5000);
                    return key+"--"+Thread.currentThread().getName();
                });
        return cache;
    }

    static class T1 extends Thread{
        private Cache<String, Object> cache;
        private Object key;

        T1(Cache<String, Object> cache,Object key){
            this.cache = cache;
            this.key = key;
        }

        @Override
        public void run() {
            System.out.println(cache.get(key));
        }
    }
}
