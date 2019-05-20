package pers.zxm.cache2j.support;

public class Validator {
    public static void checkGreaterThanZero(boolean expression, String message){
        if(!expression){
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkIsNotNull(boolean expression, String message){
        if(!expression){
            throw new IllegalArgumentException(message);
        }
    }
}
