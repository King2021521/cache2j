package pers.zxm.cache2j.persistence;

/**
 * @Author zxm
 * @Description
 * @Date Create in 上午 10:44 2018/7/26 0026
 */
public enum ProcessorType {
    ASYNCHRONOUS(AsynchronousFlushProcessor.class);

    private Class processorType;

    private ProcessorType(Class processorType){
        this.processorType = processorType;
    }

    public Class type(){
        return this.processorType;
    }
}
