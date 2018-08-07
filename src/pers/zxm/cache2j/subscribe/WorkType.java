package pers.zxm.cache2j.subscribe;

/**
 * @Author
 * @Description
 * @Date Create in 下午 2:58 2018/8/7 0007
 */
public enum WorkType{
    POLLING(PollingDaemonWorker.class),

    BALANCE(BalanceDaemonWorker.class);

    private Class type;

    private WorkType(Class type){
        this.type = type;
    }

    public Class type(){
        return this.type;
    }
}
