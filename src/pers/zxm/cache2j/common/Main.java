package pers.zxm.cache2j.common;

import pers.zxm.cache2j.Logger;
import pers.zxm.cache2j.core.Cache;
import pers.zxm.cache2j.core.CacheLoader;
import pers.zxm.cache2j.listener.DefaultListener;
import pers.zxm.cache2j.cleanup.CleanupType;
import pers.zxm.cache2j.core.CacheBuilder;
import pers.zxm.cache2j.persistence.ProcessorType;
import pers.zxm.cache2j.subscribe.*;

public class Main {
    public static Logger logger = Logger.newInstance(Main.class);

    public static void main(String[] args) throws Exception {
        //testCache();
        testPublishAndSubscribe();
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

    public static void testCache() throws Exception {
        Cache<String, Object> cache = CacheBuilder.newBuilder()
                .listener(new DefaultListener())
                .factor(0.1)
                .interval(1000)
                .ttl(5000)
                .maximum(1000)
                .monitor(CleanupType.LRU)
                .stats()
                .flushProcessor(ProcessorType.FOS)
                .path("D:\\cache2j.txt")
                .enableFlushDsk(true)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) {
                        return key + "-aaaa";
                    }
                });
    }

}
