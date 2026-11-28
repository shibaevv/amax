package au.com.totemsoft.amax;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.HttpMessageConverter;

import com.lmax.disruptor.EventHandler;

import au.com.totemsoft.amax.model.AbstractEvent;
import lombok.extern.log4j.Log4j2;

/**
 * Consumers read data from the ring buffer.
 */
@Log4j2
public abstract class AbstractProcessor<T> implements EventHandler<T> {

    private static final String MESSAGE = "{}:[Thread #{}]:{}, {} {}";

    private final String name;

    protected HttpMessageConverter<T> converter;

    public void setConverter(HttpMessageConverter<T> converter) {
        this.converter = converter;
    }

    protected AbstractProcessor(String name) {
        this.name = StringUtils.leftPad(name, 25);
    }

    public String getName() {
        return name;
    }

    protected void trace(T event, long sequence, boolean endOfBatch) {
        log.trace(MESSAGE, name, Thread.currentThread().getId(), sequence, endOfBatch ? "endOfBatch ->" : "", event);
    }

    protected void debug(T event, long sequence, boolean endOfBatch) {
        log.debug(MESSAGE, name, Thread.currentThread().getId(), sequence, endOfBatch ? "endOfBatch ->" : "", event);
    }

    protected void debug(AbstractEvent<Long> event, long sequence, boolean endOfBatch) {
        log.debug(MESSAGE, name, Thread.currentThread().getId(), sequence, endOfBatch ? "endOfBatch ->" : "", event);
    }

    protected void info(T event, long sequence, boolean endOfBatch) {
        log.info (MESSAGE, name, Thread.currentThread().getId(), sequence, endOfBatch ? "endOfBatch ->" : "", event);
    }

}
