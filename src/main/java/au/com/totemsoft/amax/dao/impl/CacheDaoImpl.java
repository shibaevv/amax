package au.com.totemsoft.amax.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.com.totemsoft.amax.dao.CacheDao;
import au.com.totemsoft.amax.dao.ExposureKeyEventDao;
import au.com.totemsoft.amax.dao.SeriesDataEventDao;
import au.com.totemsoft.amax.dao.cache.NamedCache.ExposureKeyEventCache;
import au.com.totemsoft.amax.dao.cache.NamedCache.RuleEventCache;
import au.com.totemsoft.amax.expression.PerformanceAccess;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.RuleEvent;

@Repository
//@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class CacheDaoImpl implements CacheDao {

    private Cache ruleStore; // <Long::ruleId, RuleEvent>
    private Cache ruleExposureKeyStore; // <Long::ruleId, List<ExposureKeyEvent>>

    @Autowired private ExposureKeyEventDao exposureKeyEventDao;
    @Autowired private SeriesDataEventDao seriesDataEventDao;

    @Override
    public Cache getCache(String cacheId) {
        // TODO: Auto-generated method stub
        return null;
    }

    @Override
    public Cache getRuleStore() {
        if (ruleStore == null) {
            this.ruleStore = getCache(RuleEventCache.findById);
        }
        return ruleStore;
    }

    @Override
    public Cache getRuleExposureKeyStore() {
        if (ruleExposureKeyStore == null) {
            this.ruleExposureKeyStore = getCache(ExposureKeyEventCache.findByRuleId);
        }
        return ruleExposureKeyStore;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @PerformanceAccess(message = "pre-load caches: ")
    @Override
    public List<ExposureKeyEvent> findByRule(RuleEvent event, Context context) {
        final long ruleId = event.getId();
        List<ExposureKeyEvent> exposureKeys = exposureKeyEventDao.findByRuleId(ruleId);
        if (!exposureKeys.isEmpty()) {
            /*List<SeriesDataEvent> data = */seriesDataEventDao.findByRuleId(ruleId, context);
        }
        return exposureKeys;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @PerformanceAccess(message = "pre-load caches: ")
    @Override
    public List<ExposureKeyEvent> findByWildcard(ExposureKeyEvent event, Context context) {
        final String wildcard = event.getName();
        List<ExposureKeyEvent> result = exposureKeyEventDao.findByWildcard(wildcard);
        if (!result.isEmpty()) {
            /*List<SeriesDataEvent> data = */seriesDataEventDao.findByWildcard(wildcard, context);
        }
        return result;
    }

}
