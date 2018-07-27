package pers.zxm.cache2j.common;

import pers.zxm.cache2j.Log2j;
import pers.zxm.cache2j.core.Cache;
import pers.zxm.cache2j.core.CacheLoader;
import pers.zxm.cache2j.listener.DefaultListener;
import pers.zxm.cache2j.monitor.MonitorType;
import pers.zxm.cache2j.core.CacheBuilder;
import pers.zxm.cache2j.persistence.AsynchronousFlushProcessor;
import pers.zxm.cache2j.persistence.ProcessorType;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static Log2j log2j = Log2j.newInstance(Main.class);

    public static void main(String[] args) throws Exception {
        Cache<String,Object> cache = CacheBuilder.newBuilder()
                .listener(new DefaultListener())
                .factor(0.1)
                .interval(1000)
                .ttl(5000)
                .maximum(1000)
                .monitor(MonitorType.CAP)
                .stats()
                .flushProcessor(ProcessorType.ASYNCHRONOUS)
                .path("D:\\cache2j.txt")
                .enableFlushDsk(true)
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
        //log2j.info(cache.stats());

        Thread.sleep(70*1000);
        /*write();
        read();*/
    }

    public static void write() throws IOException {
        Map map = new HashMap<>();
        map.put("d", 11);
        map.put("b",2);
        map.put("java",111);
        ByteArrayOutputStream byt = new ByteArrayOutputStream();
        ObjectOutputStream obj = new ObjectOutputStream(byt);

        obj.writeObject(map);

        byte[] bytes = byt.toByteArray();

        FileOutputStream fos = new FileOutputStream("D:\\cache2j.txt");

        fos.write(bytes);
        fos.close();
        obj.close();
        byt.close();
    }

    public static void read() throws IOException,ClassNotFoundException{
        byte[] bytes = getContent("D:\\cache2j.txt");
        ByteArrayInputStream byteInt=new ByteArrayInputStream(bytes);
        ObjectInputStream objInt=new ObjectInputStream(byteInt);
        Map map;
        map = (Map) objInt.readObject();
        System.out.println(map);
    }

    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }

        FileInputStream fi = new FileInputStream(file);

        byte[] buffer = new byte[(int) fileSize];

        int offset = 0;

        int numRead = 0;

        while (offset < buffer.length

                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {

            offset += numRead;

        }

        // 确保所有数据均被读取

        if (offset != buffer.length) {

            throw new IOException("Could not completely read file "
                    + file.getName());

        }

        fi.close();

        return buffer;
    }

}
