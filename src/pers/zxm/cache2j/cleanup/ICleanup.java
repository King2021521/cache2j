package pers.zxm.cache2j.cleanup;

/**
 * 缓存淘汰监控器
 */
public interface ICleanup {
    void processMonitor();

    void start();

    void stop();

    void startIfStop();

    boolean isRunning();
}
