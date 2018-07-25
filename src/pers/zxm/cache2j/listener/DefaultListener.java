package pers.zxm.cache2j.listener;

import pers.zxm.cache2j.common.Log;

public class DefaultListener implements CacheListener<String,Object> {
    private static Log logger = Log.newInstance(DefaultListener.class);

    @Override
    public void callback(Payload<String, Object> payload) {
        logger.info(payload.toString());
    }
}
