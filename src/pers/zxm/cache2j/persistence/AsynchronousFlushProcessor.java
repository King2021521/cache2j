package pers.zxm.cache2j.persistence;

import pers.zxm.cache2j.core.Cache;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pers.zxm.cache2j.common.Constant.KEY_NAME;
import static pers.zxm.cache2j.common.Constant.OPERATION_KEY;
import static pers.zxm.cache2j.common.Constant.VALUE_NAME;

/**
 * @Author zxm
 * @Description 异步刷盘
 * @Date Create in 上午 9:45 2018/7/26 0026
 */
public class AsynchronousFlushProcessor<K, V> implements FlushDiskProcessor {
    private ConcurrentHashMap storage;

    private MessageQueue messageQueue;
    private Thread workerThread;

    private String flushPath;

    private static final long INTERVAL_MILLS = 60 * 1000;
    private static final int DEFAULT_INIT_CAPACITY = 10000;

    public <K extends Object, V extends Object> AsynchronousFlushProcessor(Cache<K, V> cache) {
        this.messageQueue = cache.getQueue();
        this.storage = new ConcurrentHashMap<>(DEFAULT_INIT_CAPACITY);

        this.flushPath = cache.getCacheBuilder().getPath();

        workerThread = new Thread(this, "AsynchronousFlushProcessor");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                preFlush();
                flush();
                Thread.sleep(INTERVAL_MILLS);
            } catch (InterruptedException e) {

            } catch (IOException e) {

            }
        }
    }

    private void preFlush() {
        while (!messageQueue.isEmpty()) {
            Map element = messageQueue.poll();

            if (element.get(OPERATION_KEY).equals(Operation.INSERT.name())) {
                storage.put(element.get(KEY_NAME), element.get(VALUE_NAME));
            }

            if (element.get(OPERATION_KEY).equals(Operation.REMOVE.name())) {
                storage.remove(element.get(KEY_NAME));
            }
        }
    }

    @Override
    public void flush() throws IOException {
        ByteArrayOutputStream byt = new ByteArrayOutputStream();
        ObjectOutputStream obj = new ObjectOutputStream(byt);

        obj.writeObject(storage);

        byte[] bytes = byt.toByteArray();
        flushFile(flushPath, bytes);

        obj.close();
        byt.close();
    }

    private void flushFile(String path, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);

        fos.write(content);
        fos.close();
    }
}
