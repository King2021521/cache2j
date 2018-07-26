package pers.zxm.cache2j.common;

import pers.zxm.cache2j.Log2j;
import pers.zxm.cache2j.core.Cache;
import pers.zxm.cache2j.core.CacheLoader;
import pers.zxm.cache2j.listener.DefaultListener;
import pers.zxm.cache2j.monitor.MonitorType;
import pers.zxm.cache2j.core.CacheBuilder;

public class Main {
    public static Log2j log2j = Log2j.newInstance(Main.class);

    public static void main(String[] args) throws Exception{
        Cache<String,Object> cache = CacheBuilder.newBuilder()
                .listener(new DefaultListener())
                .factor(0.1)
                .interval(1000)
                .ttl(5000)
                .maximum(1000)
                .monitor(MonitorType.CAP)
                .stats()
                .build(new CacheLoader<String,Object>(){
                    @Override
                    public Object load(String key) {
                        return key+"-aaaa";
                    }
                });
        log2j.info("开始写入缓存");
        for(int i=0;i<1010;i++){
            cache.put("key"+i,"value"+i);
            Thread.sleep(5);
        }
        log2j.info("写入缓存完毕");
        Thread.sleep(10*1000);

        for (int i=0;i<1000;i++){
            cache.get("key"+i);
        }
        log2j.info(cache.stats());
    }
}
