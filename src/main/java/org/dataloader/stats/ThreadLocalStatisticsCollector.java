package org.dataloader.stats;

import org.dataloader.stats.context.IncrementBatchLoadCountByStatisticsContext;
import org.dataloader.stats.context.IncrementBatchLoadExceptionCountStatisticsContext;
import org.dataloader.stats.context.IncrementCacheHitCountStatisticsContext;
import org.dataloader.stats.context.IncrementLoadCountStatisticsContext;
import org.dataloader.stats.context.IncrementLoadErrorCountStatisticsContext;

/**
 * This can collect statistics per thread as well as in an overall sense.  This allows you to snapshot stats for a web request say
 * as well as all requests.
 * <p>
 * You will want to call {@link #resetThread()} to clean up the thread local aspects of this object per request thread.
 * <p>
 * ThreadLocals have their place in the Java world but be careful on how you use them.  If you don't clean them up on "request boundaries"
 * then you WILL have misleading statistics.
 *
 * @see org.dataloader.stats.StatisticsCollector
 */
public class ThreadLocalStatisticsCollector implements StatisticsCollector {

    private static final ThreadLocal<SimpleStatisticsCollector> collector = ThreadLocal.withInitial(SimpleStatisticsCollector::new);

    private final SimpleStatisticsCollector overallCollector = new SimpleStatisticsCollector();

    /**
     * Removes the underlying thread local value for this current thread.  This is a way to reset the thread local
     * values for the current thread and start afresh
     *
     * @return this collector for fluent coding
     */
    public ThreadLocalStatisticsCollector resetThread() {
        collector.remove();
        return this;
    }

    @Override
    public <K> long incrementLoadCount(IncrementLoadCountStatisticsContext<K> context) {
        overallCollector.incrementLoadCount(context);
        return collector.get().incrementLoadCount(context);
    }

    @Deprecated
    @Override
    public long incrementLoadCount() {
        return incrementLoadCount(null);
    }

    @Override
    public <K> long incrementLoadErrorCount(IncrementLoadErrorCountStatisticsContext<K> context) {
        overallCollector.incrementLoadErrorCount(context);
        return collector.get().incrementLoadErrorCount(context);
    }

    @Deprecated
    @Override
    public long incrementLoadErrorCount() {
        return incrementLoadErrorCount(null);
    }

    @Override
    public <K> long incrementBatchLoadCountBy(long delta, IncrementBatchLoadCountByStatisticsContext<K> context) {
        overallCollector.incrementBatchLoadCountBy(delta, context);
        return collector.get().incrementBatchLoadCountBy(delta, context);
    }

    @Deprecated
    @Override
    public long incrementBatchLoadCountBy(long delta) {
        return incrementBatchLoadCountBy(delta, null);
    }

    @Override
    public <K> long incrementBatchLoadExceptionCount(IncrementBatchLoadExceptionCountStatisticsContext<K> context) {
        overallCollector.incrementBatchLoadExceptionCount(context);
        return collector.get().incrementBatchLoadExceptionCount(context);
    }

    @Deprecated
    @Override
    public long incrementBatchLoadExceptionCount() {
        return incrementBatchLoadExceptionCount(null);
    }

    @Override
    public <K> long incrementCacheHitCount(IncrementCacheHitCountStatisticsContext<K> context) {
        overallCollector.incrementCacheHitCount(context);
        return collector.get().incrementCacheHitCount(context);
    }

    @Deprecated
    @Override
    public long incrementCacheHitCount() {
        return incrementCacheHitCount(null);
    }

    /**
     * This returns the statistics for this thread.
     *
     * @return this thread's statistics
     */
    @Override
    public Statistics getStatistics() {
        return collector.get().getStatistics();
    }

    /**
     * This returns the overall statistics, that is not per thread but for the life of this object
     *
     * @return overall statistics
     */
    public Statistics getOverallStatistics() {
        return overallCollector.getStatistics();
    }

    @Override
    public String toString() {
        return "ThreadLocalStatisticsCollector{" +
                "thread=" + getStatistics().toString() +
                "overallCollector=" + overallCollector.getStatistics().toString() +
                '}';
    }
}
