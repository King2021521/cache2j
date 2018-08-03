package pers.zxm.cache2j.common;

import pers.zxm.cache2j.Logger;
import pers.zxm.cache2j.core.Cache;
import pers.zxm.cache2j.core.CacheLoader;
import pers.zxm.cache2j.listener.DefaultListener;
import pers.zxm.cache2j.monitor.MonitorType;
import pers.zxm.cache2j.core.CacheBuilder;
import pers.zxm.cache2j.persistence.ProcessorType;
import pers.zxm.cache2j.subscribe.*;

public class Main {
    public static Logger logger = Logger.newInstance(Main.class);

    public static void main(String[] args) throws Exception {
        testPublishAndSubscribe();
    }

    public static void testPublishAndSubscribe() throws Exception{
        Channel<String> channel = NonBlockingChannel.initChannel()
                .subscribeOne(new DefaultConsumer())
                .enable();

        for (int i = 0; i < 100; i++) {
            Message<String> message = new Message<>();
            message.setTag("test");
            message.setTimestamp(System.currentTimeMillis());
            message.setPayload("test publish message "+ i);
            channel.publish(message);

            Thread.sleep((long)(Math.random()*1000));
        }
    }

    public static void testCache() throws Exception {
        Cache<String, Object> cache = CacheBuilder.newBuilder()
                .listener(new DefaultListener())
                .factor(0.1)
                .interval(1000)
                .ttl(5000)
                .maximum(1000)
                .monitor(MonitorType.LRU)
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
        System.out.println(cache.size());
        logger.info("开始写入缓存");
        for (int i = 0; i < 1010; i++) {
            cache.put("key" + i, "value" + i);
            Thread.sleep(5);
        }
        logger.info("写入缓存完毕");
        Thread.sleep(10 * 1000);

        for (int i = 0; i < 1000; i++) {
            cache.get("key" + i);
        }
        logger.info(cache.stats());

        Thread.sleep(70 * 1000);
    }

}
