package au.com.totemsoft.amax.model.translator;

import java.nio.CharBuffer;

import com.lmax.disruptor.EventTranslatorOneArg;

import au.com.totemsoft.amax.dao.impl.AbstractDaoImpl;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import lombok.extern.log4j.Log4j2;

/**
 *  Lambda-style API - getting ready for Java 8 Functional Interfaces
 */
@Log4j2
/*public*/ abstract class AbstractEventTranslator<T> implements EventTranslatorOneArg<T, CharBuffer> {

    protected void log(ExposureKeyEvent event, String msg) {
        log.info(String.format("[Thread #%1$3d] %2$19s %3$s", Thread.currentThread().getId(), msg, event));
    }

}