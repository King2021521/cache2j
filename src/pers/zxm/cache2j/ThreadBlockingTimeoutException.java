package pers.zxm.cache2j;

/**
 * @Author
 * @Description 线程阻塞超时异常
 * @Date Create in 下午 4:22 2019/5/22 0022
 */
public class ThreadBlockingTimeoutException extends LoadingFailException {
    public ThreadBlockingTimeoutException(String message){
        super(message);
    }
}
