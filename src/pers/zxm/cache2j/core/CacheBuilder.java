package pers.zxm.cache2j.core;

import pers.zxm.cache2j.Stats;
import pers.zxm.cache2j.common.Validator;
import pers.zxm.cache2j.listener.CacheListener;
import pers.zxm.cache2j.monitor.MonitorType;
import pers.zxm.cache2j.persistence.ProcessorType;

import java.util.Objects;

/**
 * cache build tool
 *
 * @param <K>
 * @param <V>
 * @author zxm
 */
public final class CacheBuilder<K, V> {
    private CacheListener listener;
    private MonitorType type;
    private Stats stats;

    private ProcessorType processorType = ProcessorType.FOS;
    private Boolean enableFlushDisk = false;
    private String path;

    long ttl = Long.MAX_VALUE;
    long interval = Long.MAX_VALUE;
    int maximum = Integer.MAX_VALUE;
    double factor = 0.2;

    private CacheBuilder() {
    }

    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder();
    }

    public CacheBuilder<K, V> listener(CacheListener listener) {
        this.listener = listener;
        return this;
    }

    public CacheBuilder<K, V> ttl(long ttl) {
        Validator.checkGreaterThanZero(ttl > 0L, "ttl can't less than zero");
        this.ttl = ttl;
        return this;
    }

    public CacheBuilder<K, V> interval(long interval) {
        Validator.checkGreaterThanZero(interval > 0L, "interval can't less than zero");
        this.interval = interval;
        return this;
    }

    public CacheBuilder<K, V> maximum(int maximum) {
        Validator.checkGreaterThanZero(maximum > 0, "maximum can't less than zero");
        this.maximum = maximum;
        return this;
    }

    public CacheBuilder<K, V> factor(double factor) {
        Validator.checkGreaterThanZero((factor > 0.0) && (factor < 1.0), "factor must be between 0 and 1");
        this.factor = factor;
        return this;
    }

    public CacheBuilder<K, V> monitor(MonitorType type) {
        this.type = type;
        return this;
    }

    public CacheBuilder<K, V> stats() {
        this.stats = new Stats();
        return this;
    }

    public CacheBuilder<K, V> flushProcessor(ProcessorType processorType) {
        this.processorType = processorType;
        return this;
    }

    public CacheBuilder<K, V> flushProcessor() {
        this.processorType = ProcessorType.FOS;
        return this;
    }

    public CacheBuilder<K, V> enableFlushDsk(Boolean enable) {
        this.enableFlushDisk = enable;
        return this;
    }

    public CacheBuilder<K, V> path(String path) {
        this.path = path;
        return this;
    }

    public Boolean getEnableFlushDisk(){
        return this.enableFlushDisk;
    }

    public ProcessorType getProcessorType(){
        return this.processorType;
    }

    public String getPath(){
        return this.path;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build(CacheLoader<? super K1, V1> loader) {
        Cache<K1, V1> cache = new Cache(this, loader);
        return cache;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        Cache<K1, V1> cache = new Cache(this, null);
        return cache;
    }

    public CacheListener getListener() {
        return listener;
    }

    public MonitorType getType() {
        return type;
    }

    public Long getTtl() {
        return ttl;
    }

    public Long getInterval() {
        return interval;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public Double getFactor() {
        return factor;
    }

    public Stats getStats() {
        return stats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheBuilder<?, ?> that = (CacheBuilder<?, ?>) o;
        return ttl == that.ttl &&
                interval == that.interval &&
                maximum == that.maximum &&
                Double.compare(that.factor, factor) == 0 &&
                Objects.equals(listener, that.listener) &&
                type == that.type &&
                Objects.equals(stats, that.stats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listener, type, ttl, interval, maximum, factor, stats);
    }
}
