package au.com.totemsoft.amax.dao;

import java.util.List;

import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.SeriesDataEvent;

public interface SeriesDataEventDao extends AbstractDao<SeriesDataEvent> {

    SeriesDataEvent findByNaturalId(long exposureKeyId, Context context);

    List<SeriesDataEvent> findByRuleId(long ruleId, Context context);

    List<SeriesDataEvent> findByWildcard(String name, Context context);

}
