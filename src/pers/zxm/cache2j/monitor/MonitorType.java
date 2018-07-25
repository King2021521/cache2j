package pers.zxm.cache2j.monitor;

public enum MonitorType {
    TTL(TtlMonitor.class),

    CAP(CapMonitor.class);

    private Class type;

    private MonitorType(Class type){
        this.type = type;
    }

    public Class getType(){
        return this.type;
    }
}
