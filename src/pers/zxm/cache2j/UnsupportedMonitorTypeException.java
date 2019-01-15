package pers.zxm.cache2j;

/**
 * cleanup type unsupported exception
 * @author zxm
 */
public class UnsupportedMonitorTypeException extends RuntimeException {
    static final long serialVersionUID = -1242599979055084673L;

    public UnsupportedMonitorTypeException(){
    }

    public UnsupportedMonitorTypeException(String message){
        super(message);
    }

    public UnsupportedMonitorTypeException(String message, Throwable cause){
        super(message,cause);
    }

    public UnsupportedMonitorTypeException(Throwable cause){
        super(cause);
    }
}
