package pers.zxm.cache2j.persistence;

import pers.zxm.cache2j.core.Cache;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zxm
 * @Description 异步刷盘
 * @Date Create in 上午 9:45 2018/7/26 0026
 */
public class FOSBackupProcessor<K, V> implements FlushDiskProcessor {
    private ConcurrentHashMap storage;

    private MessageQueue messageQueue;
    private Thread workerThread;

    private String flushPath;

    private static final long INTERVAL_MILLS = 60 * 1000;
    private static final int DEFAULT_INIT_CAPACITY = 10000;

    public <K extends Object, V extends Object> FOSBackupProcessor(Cache<K, V> cache) {
        this.messageQueue = cache.getQueue();
        this.storage = new ConcurrentHashMap<>(DEFAULT_INIT_CAPACITY);

        this.flushPath = cache.getCacheBuilder().getPath();

        workerThread = new Thread(this, "FOSBackupProcessor");
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
            OperateMessage<K,V> element = messageQueue.poll();

            if (Operation.INSERT.equals(element.getOperation())) {
                storage.put(element.getKey(), element.getValue());
            }

            if (Operation.REMOVE.equals(element.getOperation())) {
                storage.remove(element.getKey());
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
