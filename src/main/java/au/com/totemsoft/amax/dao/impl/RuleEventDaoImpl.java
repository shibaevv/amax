package au.com.totemsoft.amax.dao.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.com.totemsoft.amax.dao.RuleEventDao;
import au.com.totemsoft.amax.dao.cache.NamedCache;
import au.com.totemsoft.amax.dao.cache.NamedCache.RuleEventCache;
import au.com.totemsoft.amax.model.RuleEvent;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class RuleEventDaoImpl extends AbstractDaoImpl<RuleEvent> implements RuleEventDao {

    //@au.com.totemsoft.amax.dao.cache.Cacheable(value = RuleEventCache.name)
    @Cacheable(value = RuleEventCache.findById, unless = NamedCache.resultIsNull)
    @Override
    public RuleEvent findById(Long id) {
        return detach(super.findById(id));
    }

}
