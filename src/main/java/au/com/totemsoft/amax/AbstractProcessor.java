package au.com.totemsoft.amax;

import org.springframework.http.converter.HttpMessageConverter;

import com.lmax.disruptor.EventHandler;

import au.com.totemsoft.amax.model.AbstractEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractProcessor<T> implements EventHandler<T> {

    private final String name;

    protected HttpMessageConverter<T> converter;

    public void setConverter(HttpMessageConverter<T> converter) {
        this.converter = converter;
    }

    protected AbstractProcessor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        debug(event, sequence, endOfBatch);
    }

    protected void debug(T event, long sequence, boolean endOfBatch) {
        log.debug("[Thread #{}]:{}, {}, endOfBatch:{}, {}", Thread.currentThread().getId(), sequence, name, endOfBatch ? "endOfBatch ->" : "", event);
    }

    protected void info(T event, long sequence, boolean endOfBatch) {
        log.info("[Thread #{}]:{}, {}, endOfBatch:{}, {}", Thread.currentThread().getId(), sequence, name, endOfBatch ? "endOfBatch ->" : "", event);
    }

    protected void log(AbstractEvent<Long> event, String msg) {
        log.info("[Thread #{}] {}, {}", Thread.currentThread().getId(), msg, event);
    }

    protected void log(AbstractEvent<Long> event, long sequence, boolean endOfBatch) {
        log.info("[Thread #{}]:{} {}, {}", Thread.currentThread().getId(), sequence, endOfBatch ? "endOfBatch ->" : "", event);
    }

}
