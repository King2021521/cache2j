package pers.zxm.cache2j.listener;

import pers.zxm.cache2j.Log2j;

public class DefaultListener implements CacheListener<String,Object> {
    private static Log2j logger = Log2j.newInstance(DefaultListener.class);

    @Override
    public void callback(Payload<String, Object> payload) {
        //logger.info(payload.toString());
    }
}
