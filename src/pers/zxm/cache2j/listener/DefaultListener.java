package pers.zxm.cache2j.listener;

import pers.zxm.cache2j.Logger;

public class DefaultListener implements CacheListener<String,Object> {
    private static Logger logger = Logger.newInstance(DefaultListener.class);

    @Override
    public void callback(Payload<String, Object> payload) {
        //logger.info(payload.toString());
    }
}
