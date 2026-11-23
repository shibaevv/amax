package au.com.totemsoft.amax.dao.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.com.totemsoft.amax.dao.SeriesDataEventDao;
import au.com.totemsoft.amax.dao.cache.NamedCache;
import au.com.totemsoft.amax.dao.cache.NamedCache.SeriesDataEventCache;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.SeriesDataEvent;
import lombok.extern.log4j.Log4j2;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
@Log4j2
public class SeriesDataEventDaoImpl extends AbstractDaoImpl<SeriesDataEvent> implements SeriesDataEventDao {

    @Cacheable(value = SeriesDataEventCache.findById, unless = NamedCache.resultIsNull)
    @Override
    public SeriesDataEvent findById(Long id) {
        return detach(super.findById(id));
    }

    @au.com.totemsoft.amax.dao.cache.Cacheable//(preLoaded = false)
    //@Cacheable(value = SeriesDataEventCache.findByNaturalId)
    @Override
    public SeriesDataEvent findByNaturalId(long exposureKeyId, Context context) {
        log.debug(String.format("[Thread #%1$3d] findByNaturalId exposureKeyId=%2$d", Thread.currentThread().getId(), exposureKeyId));
        try {
            return (SeriesDataEvent) getEntityManager()
                .createNamedQuery("seriesDataEvent.findByNaturalId")
                .setParameter("exposureKeyId", exposureKeyId)
                .setParameter("date", context.getDate())
                .getSingleResult();
        } catch (NoResultException ignore) {
            return null;
        }
    }

    @au.com.totemsoft.amax.dao.cache.Cacheable
    //@Cacheable(value = SeriesDataEventCache.findByRuleId)
    @SuppressWarnings("unchecked")
    @Override
    public List<SeriesDataEvent> findByRuleId(long ruleId, Context context) {
        log.debug(String.format("[Thread #%1$3d] findByRuleId %2$d", Thread.currentThread().getId(), ruleId));
        return getEntityManager()
            .createNamedQuery("seriesDataEvent.findByRuleId")
            .setParameter("ruleId", ruleId)
            .setParameter("date", context.getDate())
            .getResultList();
    }

    @au.com.totemsoft.amax.dao.cache.Cacheable
    //@Cacheable(value = SeriesDataEventCache.findByWildcard)
    @SuppressWarnings("unchecked")
    @Override
    public List<SeriesDataEvent> findByWildcard(String name, Context context) {
        log.debug(String.format("[Thread #%1$3d] findByWildcard '%2$s'", Thread.currentThread().getId(), name));
        name = updateWildcard(name);
        if (name == null) {
            return Collections.emptyList();
        }
        return getEntityManager()
            .createNamedQuery("seriesDataEvent.findByWildcard")
            .setParameter("name", name.toLowerCase())
            .setParameter("date", context.getDate())
            .getResultList();
    }

}
