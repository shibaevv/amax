package au.com.totemsoft.amax.dao;

public interface AbstractDao<T> {

    T findById(Long id);

}
