package pers.zxm.cache2j.persistence;

import java.io.IOException;

/**
 * @Author
 * @Description
 * @Date Create in 上午 10:58 2018/7/26 0026
 */
public interface FlushDiskProcessor extends Runnable{
    void flush() throws IOException;
}
