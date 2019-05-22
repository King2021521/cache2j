package pers.zxm.cache2j.support;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存统计信息，含命中次数，未命中次数及命中率。
 * @author zxm
 * @since 2018.02.01
 */
public final class Stats {
    private AtomicLong hitCount = new AtomicLong();
    private AtomicLong missCount = new AtomicLong();
    private AtomicLong reloadCount = new AtomicLong();

    public long getHitCount() {
        return hitCount.longValue();
    }

    public long getMissCount() {
        return missCount.longValue();
    }

    public long getReloadCount(){
        return reloadCount.longValue();
    }

    public float getHitRate() {
        return hitCount.floatValue() / (hitCount.floatValue() + missCount.floatValue() + reloadCount.floatValue());
    }

    public void hit(){
        this.hitCount.incrementAndGet();
    }

    public void miss(){
        this.missCount.incrementAndGet();
    }

    public void reload(){
        this.reloadCount.incrementAndGet();
    }

    @Override
    public String toString() {
        return "Stats{" +
                "hitCount=" + hitCount +
                ", missCount=" + missCount +
                ", reloadCount=" + reloadCount +
                ", hitRate=" + hitCount.floatValue() / (hitCount.floatValue() + missCount.floatValue() + reloadCount.floatValue()) +
                '}';
    }
}
