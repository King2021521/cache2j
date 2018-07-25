package pers.zxm.cache2j.common;

public class Validator {
    public static void checkGreaterThanZero(boolean expression, String message){
        if(!expression){
            throw new IllegalArgumentException(message);
        }
    }
}
