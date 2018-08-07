package pers.zxm.cache2j.subscribe;

/**
 * @Author
 * @Description
 * @Date Create in 下午 2:13 2018/8/7 0007
 */
public interface DaemonWorker<T> extends Runnable{
    void start();

    void waiting();
}
