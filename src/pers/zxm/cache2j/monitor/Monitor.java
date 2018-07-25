package pers.zxm.cache2j.monitor;

/**
 * 缓存淘汰监控器
 */
public interface Monitor {
    void processMonitor();

    void start();

    void stop();

    void startIfStop();

    boolean isRunning();
}
