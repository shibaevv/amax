package au.com.totemsoft.amax;

import org.springframework.http.converter.HttpMessageConverter;

import com.lmax.disruptor.EventHandler;

import au.com.totemsoft.amax.dao.impl.AbstractDaoImpl;
import au.com.totemsoft.amax.model.AbstractEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractProcessor<T> implements EventHandler<T> {

    private final String name;

    protected HttpMessageConverter<T> converter;

    public void setConverter(HttpMessageConverter<T> converter) {
        this.converter = converter;
    }

    public AbstractProcessor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

//    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
//        debug(event, sequence, endOfBatch);
//    }

    protected void debug(T event, long sequence, boolean endOfBatch) {
        log.debug("Thread #" + Thread.currentThread().getId() +  " " + name + ": #" + sequence + ", endOfBatch=" + endOfBatch + ", " + event);
    }

    protected void info(T event, long sequence, boolean endOfBatch) {
        log.info("Thread #" + Thread.currentThread().getId() +  " " + name + ": #" + sequence + ", endOfBatch=" + endOfBatch + ", " + event);
    }

    protected void log(AbstractEvent event, String msg) {
        log.info(String.format("[Thread #%1$3d] %2$19s %3$s", Thread.currentThread().getId(), msg, event));
    }

    protected void log(AbstractEvent event, long sequence, boolean endOfBatch) {
        log.info(String.format("[Thread #%1$3d] %2$10s %3$5d %4$s", Thread.currentThread().getId(), endOfBatch ? "endOfBatch ->" : "             ", sequence, event));
    }

}