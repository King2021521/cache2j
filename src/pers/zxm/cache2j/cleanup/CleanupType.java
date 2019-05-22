package pers.zxm.cache2j.cleanup;

public enum CleanupType {
    TTL(TTLCleanup.class),

    LRU(LRUCleanup.class);

    private Class type;

    private CleanupType(Class type){
        this.type = type;
    }

    public Class getType(){
        return this.type;
    }
}
