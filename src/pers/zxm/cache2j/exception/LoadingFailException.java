package pers.zxm.cache2j.exception;

/**
 * An exception occurred when cacheLoader working
 * @author zxm
 * @since 2018-01-26
 */
public class LoadingFailException extends RuntimeException{
    static final long serialVersionUID = -1242599979055084673L;

    public LoadingFailException(){
    }

    public LoadingFailException(String message){
        super(message);
    }

    public LoadingFailException(String message, Throwable cause){
        super(message,cause);
    }

    public LoadingFailException(Throwable cause){
        super(cause);
    }
}
