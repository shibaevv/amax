package au.com.totemsoft.amax.tool;

import java.nio.CharBuffer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.RingBuffer;

import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.model.translator.SeriesDataEventTranslator;

/**
 * publishing messages is via the Event Publisher/Event Translator portion of the API
 */
@Component("seriesDataProducer")
public class SeriesDataEventProducer {

    private final RingBuffer<SeriesDataEvent> ringBuffer;

    private final SeriesDataEventTranslator translator;

    public SeriesDataEventProducer(
            @Qualifier("seriesDataDisruptor") SeriesDataDisruptor disruptor,
            @Qualifier("seriesDataTranslator") SeriesDataEventTranslator translator) {
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
