package au.com.totemsoft.amax;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Only unchecked exceptions cause rollbacks in transactions (so lets set this as unchecked).
 * Or we can do this: @Transactional(rollbackFor = ServiceException.class)
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 8670068154122180940L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(ExceptionUtils.getRootCauseMessage(cause), cause);
    }

    public ServiceException(String message, Throwable cause) {
        super((message == null ? "" : message + ": ") + ExceptionUtils.getRootCauseMessage(cause), cause);
    }

}
