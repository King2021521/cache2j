package pers.zxm.cache2j.listener;

public class DefaultListener implements CacheListener<String,Object> {

    @Override
    public void callback(Payload<String, Object> payload) {
        System.out.println(payload.toString());
    }
}
