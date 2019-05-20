package pers.zxm.cache2j;

/**
 * cleanup type unsupported exception
 * @author zxm
 */
public class UnsupportedCleanupTypeException extends RuntimeException {
    static final long serialVersionUID = -1242599979055084673L;

    public UnsupportedCleanupTypeException(){
    }

    public UnsupportedCleanupTypeException(String message){
        super(message);
    }

    public UnsupportedCleanupTypeException(String message, Throwable cause){
        super(message,cause);
    }

    public UnsupportedCleanupTypeException(Throwable cause){
        super(cause);
    }
}
