package au.com.totemsoft.amax.tool;

import java.nio.CharBuffer;

import com.lmax.disruptor.RingBuffer;

import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.model.translator.SeriesDataEventTranslator;

/**
 * publishing messages is via the Event Publisher/Event Translator portion of the API
 */
public class SeriesDataProducer {

    private final RingBuffer<SeriesDataEvent> ringBuffer;

    private final SeriesDataEventTranslator translator;

    public SeriesDataProducer(
            SeriesDataDisruptor disruptor,
            SeriesDataEventTranslator translator) {
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
