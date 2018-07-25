package pers.zxm.cache2j;

/**
 * An exception occurred when value is null
 * @author zxm
 * @since 2018-01-26
 */
public class UnCheckNullException extends RuntimeException{
    static final long serialVersionUID = -1242599979055084673L;

    public UnCheckNullException(){
    }

    public UnCheckNullException(String message){
        super(message);
    }

    public UnCheckNullException(String message, Throwable cause){
        super(message,cause);
    }

    public UnCheckNullException(Throwable cause){
        super(cause);
    }
}
