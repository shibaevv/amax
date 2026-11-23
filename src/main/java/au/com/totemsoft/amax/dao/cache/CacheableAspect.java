package au.com.totemsoft.amax.dao.cache;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.ApplicationContextException;

import au.com.totemsoft.amax.dao.CacheDao;
import au.com.totemsoft.amax.dao.cache.NamedCache.ExposureKeyEventCache;
import au.com.totemsoft.amax.dao.cache.NamedCache.SeriesDataEventCache;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.SeriesDataEvent;

/**
 * Cache all current entities (@see org.springframework.cache.annotation.Cacheable)
 */
@Aspect
public class CacheableAspect {

    /** spring cacheManager */
    @Autowired private CacheDao cacheDao;

    /**
     * Helper method
     * @param access
     * @return
     */
    private Cache getCache(String cacheName) {
        final Cache cache = cacheDao.getCache(cacheName);
        if (cache != null) {
            return cache;
        }
        throw new ApplicationContextException("No cache configured: " + cacheName);
    }

    @Pointcut("execution(public * au.com.totemsoft.amax.dao.impl.ExposureKeyEventDaoImpl.findByRuleId(..))")
    private void exposureKeyEventDao_findByRuleId() {}
    @SuppressWarnings("unchecked")
    @Around("@annotation(access) && exposureKeyEventDao_findByRuleId() && args(ruleId)")
    public Object exposureKeyEventDao_findByRuleId(ProceedingJoinPoint pjp, Cacheable access,
        long ruleId) throws Throwable {
        final Cache cache_findById = getCache(ExposureKeyEventCache.findById);
        //final Cache cache_findByRuleId = getCache(ExposureKeyEventCache.findByRuleId);
        // reload cache
        List<ExposureKeyEvent> result = (List<ExposureKeyEvent>) pjp.proceed();
        for (ExposureKeyEvent ek : result) {
            cache_findById.put(ek.getId(), ek);
        }
        return result;
    }

    @Pointcut("execution(public * au.com.totemsoft.amax.dao.impl.ExposureKeyEventDaoImpl.findByWildcard(..))")
    private void exposureKeyEventDao_findByWildcard() {}
    @SuppressWarnings("unchecked")
    @Around("@annotation(access) && exposureKeyEventDao_findByWildcard() && args(name)")
    public Object exposureKeyEventDao_findByWildcard(ProceedingJoinPoint pjp, Cacheable access,
        String name) throws Throwable {
        final Cache cache_findById = getCache(ExposureKeyEventCache.findById);
        //final Cache cache_findByWildcard = getCache(ExposureKeyEventCache.findByWildcard);
        // reload cache
        List<ExposureKeyEvent> result = (List<ExposureKeyEvent>) pjp.proceed();
        for (ExposureKeyEvent ek : result) {
            cache_findById.put(ek.getId(), ek);
        }
        return result;
    }

    @Pointcut("execution(public * au.com.totemsoft.amax.dao.impl.SeriesDataEventDaoImpl.findByNaturalId(..))")
    private void seriesDataEventDao_findByNaturalId() {}
    @Around("@annotation(access) && seriesDataEventDao_findByNaturalId() && args(exposureKeyId, context)")
    public Object seriesDataEventDao_findByNaturalId(ProceedingJoinPoint pjp, Cacheable access,
        Long exposureKeyId, Context context) throws Throwable {
        final Cache cache_findById = getCache(SeriesDataEventCache.findById);
        final Cache cache_findByNaturalId = getCache(SeriesDataEventCache.findByNaturalId);
        //
        final String key = SeriesDataEventGenerator.generate(exposureKeyId, context.getDate());
        ValueWrapper value = cache_findByNaturalId.get(key);
        if (value != null) {
            Long seriesDataEventId = (Long) value.get();
            value = cache_findById.get(seriesDataEventId);
            return value.get();
        }
        // data should be pre-loaded, @see RuleProcessor.store()
        if (access.preLoaded()) {
            return null;
        }
        SeriesDataEvent result = (SeriesDataEvent) pjp.proceed();
        if (result == null) {
            return null;
        }
        Long seriesDataEventId = result.getId();
        cache_findById.put(seriesDataEventId, result);
        cache_findByNaturalId.put(key, seriesDataEventId);
        return result;
    }

    @Pointcut("execution(public * au.com.totemsoft.amax.dao.impl.SeriesDataEventDaoImpl.findByRuleId(..))")
    private void seriesDataEventDao_findByRuleId() {}
    @SuppressWarnings("unchecked")
    @Around("@annotation(access) && seriesDataEventDao_findByRuleId() && args(ruleId, context)")
    public Object seriesDataEventDao_findByRuleId(ProceedingJoinPoint pjp, Cacheable access,
        long ruleId, Context context) throws Throwable {
        List<SeriesDataEvent> result = (List<SeriesDataEvent>) pjp.proceed();
        cache(result, context);
        return result;
    }

    @Pointcut("execution(public * au.com.totemsoft.amax.dao.impl.SeriesDataEventDaoImpl.findByWildcard(..))")
    private void seriesDataEventDao_findByWildcard() {}
    @SuppressWarnings("unchecked")
    @Around("@annotation(access) && seriesDataEventDao_findByWildcard() && args(name, context)")
    public Object seriesDataEventDao_findByWildcard(ProceedingJoinPoint pjp, Cacheable access,
        String name, Context context) throws Throwable {
        List<SeriesDataEvent> result = (List<SeriesDataEvent>) pjp.proceed();
        cache(result, context);
        return result;
    }

    private void cache(List<SeriesDataEvent> result, Context context) throws Throwable {
        final Cache cache_findById = getCache(SeriesDataEventCache.findById);
        final Cache cache_findByNaturalId = getCache(SeriesDataEventCache.findByNaturalId);
        for (SeriesDataEvent sd : result) {
            Long seriesDataEventId = sd.getId();
            String key = SeriesDataEventGenerator.generate(sd.getExposureKeyId(), sd.getDate() != null ? sd.getDate() : context.getDate());
            cache_findById.put(seriesDataEventId, sd);
            cache_findByNaturalId.put(key, seriesDataEventId);
        }
    }

}