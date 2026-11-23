package au.com.totemsoft.amax.tool;

import java.nio.CharBuffer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.RingBuffer;

import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.translator.ExposureKeyEventTranslator;

/**
 * publishing messages is via the Event Publisher/Event Translator portion of the API
 */
@Component("exposureKeyProducer")
public class ExposureKeyEventProducer {

    private final RingBuffer<ExposureKeyEvent> ringBuffer;

    private final ExposureKeyEventTranslator translator;

    public ExposureKeyEventProducer(
            @Qualifier("exposureKeyDisruptor") ExposureKeyDisruptor disruptor,
            @Qualifier("exposureKeyTranslator") ExposureKeyEventTranslator translator) {
        this.ringBuffer = disruptor.getRingBuffer();
        this.translator = translator;
    }

    /** publishing messages is via the Event Publisher/Event Translator */
    public void onData(CharBuffer... values) {
        if (values.length == 0) {
            return; // throw ???
        }
        if (values.length == 1) {
            ringBuffer.publishEvent(translator, values[0]);
        } else {
            ringBuffer.publishEvents(translator, values);
        }
    }

}
