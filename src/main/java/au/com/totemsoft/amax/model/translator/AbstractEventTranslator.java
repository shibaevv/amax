package au.com.totemsoft.amax.model.translator;

import java.nio.CharBuffer;

import com.lmax.disruptor.EventTranslatorOneArg;

import lombok.extern.log4j.Log4j2;

/**
 *  Lambda-style API - getting ready for Java 8 Functional Interfaces
 */
@Log4j2
/*public*/ abstract class AbstractEventTranslator<T> implements EventTranslatorOneArg<T, CharBuffer> {

    protected void log(T event, long sequence) {
        log.info("[Thread #{}]:{} {}", Thread.currentThread().getId(), sequence, event);
    }

}
