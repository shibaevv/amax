package au.com.totemsoft.amax.dao.impl;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import au.com.totemsoft.amax.IBase;
import au.com.totemsoft.amax.dao.AbstractDao;
import lombok.extern.log4j.Log4j2;

/**
 * Can be called from thread, so @Transactional annotation required as no service layer build here.
 */
//@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
@Log4j2
public abstract class AbstractDaoImpl<T> implements AbstractDao<T> {

    private final Class<T> persistentClass;

    @PersistenceContext
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    @SuppressWarnings("unchecked")
    protected AbstractDaoImpl() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public T findById(Long id) {
        log.debug(String.format("[Thread #%1$3d] id=%2$10d", Thread.currentThread().getId(), id));
        return em.find(persistentClass, id);
    }

    protected T detach(T entity) {
        if (entity != null) {
            getEntityManager().detach(entity); // JPA 2
        }
        return entity;
    }

    /**
     * @return null if not wildcard
     */
    protected String updateWildcard(String name) {
        // just to be safe
        if (!StringUtils.contains(name, IBase.WILDCARD_TAG)) {
            return null;
        }
        // encode '%' character() for sql like criteria
        if (StringUtils.contains(name, IBase.WILDCARD_SQL)) {
            name = name.replace(IBase.WILDCARD_SQL, IBase.WILDCARD_SQL_PRESERVE);
        }
        name = name.replace(IBase.WILDCARD_TAG, IBase.WILDCARD_SQL);
        return name;
    }

}
