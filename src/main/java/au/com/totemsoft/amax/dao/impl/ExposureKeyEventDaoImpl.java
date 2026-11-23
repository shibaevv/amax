package au.com.totemsoft.amax.dao.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.com.totemsoft.amax.dao.ExposureKeyEventDao;
import au.com.totemsoft.amax.dao.cache.NamedCache;
import au.com.totemsoft.amax.dao.cache.NamedCache.ExposureKeyEventCache;
import au.com.totemsoft.amax.model.ExposureKeyEvent;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class ExposureKeyEventDaoImpl extends AbstractDaoImpl<ExposureKeyEvent> implements ExposureKeyEventDao {

    @Cacheable(value = ExposureKeyEventCache.findById, unless = NamedCache.resultIsNull)
    @Override
    public ExposureKeyEvent findById(Long id) {
        return detach(super.findById(id));
    }

    @au.com.totemsoft.amax.dao.cache.Cacheable
    @Cacheable(value = ExposureKeyEventCache.findByRuleId)
    @SuppressWarnings("unchecked")
    @Override
    public List<ExposureKeyEvent> findByRuleId(long ruleId) {
        return getEntityManager()
            .createNamedQuery("exposureKeyEvent.findByRuleId")
            .setParameter("ruleId", ruleId)
            .getResultList();
    }

    @au.com.totemsoft.amax.dao.cache.Cacheable
    @Cacheable(value = ExposureKeyEventCache.findByWildcard)
    @SuppressWarnings("unchecked")
    @Override
    public List<ExposureKeyEvent> findByWildcard(String name) {
        name = updateWildcard(name);
        if (name == null) {
            return Collections.emptyList();
        }
        return getEntityManager()
            .createNamedQuery("exposureKeyEvent.findByWildcard")
            .setParameter("name", name.toLowerCase())
            .getResultList();
    }

}
