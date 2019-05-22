package pers.zxm.cache2j.persistence;

import pers.zxm.cache2j.support.Logger;

import java.io.*;
import java.util.Map;

/**
 * @Author zxm
 * @Description Initialize data from a file to memory
 * @Date Create in 下午 1:29 2018/7/31 0031
 */
public class LoadProcessor {
    private static Logger logger = Logger.newInstance(LoadProcessor.class);

    public static Map read(String path) {
        ByteArrayInputStream byteInt = null;
        ObjectInputStream objInt = null;

        try {
            byte[] bytes = getContent(path);
            byteInt = new ByteArrayInputStream(bytes);
            objInt = new ObjectInputStream(byteInt);

            return (Map) objInt.readObject();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        } finally {
            if (objInt != null) {
                try {
                    objInt.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }

            if (byteInt != null) {
                try {
                    byteInt.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            logger.info("file too big");
            return null;
        }

        FileInputStream fi = new FileInputStream(file);

        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;

        while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }

        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        fi.close();
        return buffer;
    }
}
