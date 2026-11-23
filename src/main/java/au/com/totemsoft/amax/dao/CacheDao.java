package au.com.totemsoft.amax.dao;

import java.util.List;

import org.springframework.cache.Cache;

import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.RuleEvent;

public interface CacheDao {

    Cache getCache(String cacheId);

    Cache getRuleStore();
    Cache getRuleExposureKeyStore();

    List<ExposureKeyEvent> findByRule(RuleEvent event, Context context);
    List<ExposureKeyEvent> findByWildcard(ExposureKeyEvent event, Context context);

}
