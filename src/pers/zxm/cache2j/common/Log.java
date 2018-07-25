package pers.zxm.cache2j.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author zxm
 * @Description
 * @Date Create in 下午 2:03 2018/7/25 0025
 */
public class Log {
    private static final String FILTER_MARK = "{}";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

    private Class type;

    private Log() {
    }

    private Log(Class type) {
        this.type = type;
    }

    public static Log newInstance(Class type) {
        return new Log(type);
    }

    public void info(String message) {
        print(message, Level.INFO);
    }

    public void info(String message, Object... args) {
        message = argsInjectFilter(message, args);
        print(message, Level.INFO);
    }

    public void error(String message) {
        print(message, Level.ERROR);
    }

    public void error(String message, Object... args) {
        message = argsInjectFilter(message, args);
        print(message, Level.ERROR);
    }

    private String argsInjectFilter(String message, Object[] params) {
        for (Object param : params) {
            String[] arrays = message.split("\\" + FILTER_MARK);
            if (arrays.length != 0) {
                message = arrays[0] + param;
                for (int i = 1; i < arrays.length; i++) {
                    message = message + arrays[i] + FILTER_MARK;
                }
            }
        }
        return message.replace(FILTER_MARK, "");
    }

    private String getDateString() {
        return new SimpleDateFormat(DATE_FORMAT).format(new Date());
    }

    private void print(String message, Level level) {
        System.out.println(getDateString() + " " + "[" + Thread.currentThread().getName() + "]" + " " + level + " " + type.getName() + " - " + message);
    }

    enum Level {
        ERROR,
        INFO;
    }
}
