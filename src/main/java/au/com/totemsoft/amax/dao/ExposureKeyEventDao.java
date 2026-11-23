package au.com.totemsoft.amax.dao;

import java.util.List;

import au.com.totemsoft.amax.model.ExposureKeyEvent;

public interface ExposureKeyEventDao extends AbstractDao<ExposureKeyEvent> {

    List<ExposureKeyEvent> findByRuleId(long ruleId);

    List<ExposureKeyEvent> findByWildcard(String name);

}
