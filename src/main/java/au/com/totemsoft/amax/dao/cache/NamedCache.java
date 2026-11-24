package au.com.totemsoft.amax.dao.cache;

/**
 * Cache key/name used
 */
public interface NamedCache {

    String resultIsNull        = "#result == null";

    public interface ExposureKeyEventCache {
        // cache name(s)
        String findById        = "ExposureKeyEvent";
        String findByRuleId    = findById + ".findByRuleId";
        String findByWildcard  = findById + ".findByWildcard";
    }

    public interface RuleEventCache {
        // cache name(s)
        String findById        = "RuleEvent";
    }

    public interface SeriesDataEventCache {
        // cache key(s)
        String naturalIdGenerator   = "T(au.com.totemsoft.amax.dao.cache.SeriesDataEventGenerator).generate(#exposureKeyId, #date)";
        // cache name(s)
        String findById        = "SeriesDataEvent";
        String findByNaturalId = findById + ".findByNaturalId";
        String findByRuleId    = findById + ".findByRuleId";
    }

}
