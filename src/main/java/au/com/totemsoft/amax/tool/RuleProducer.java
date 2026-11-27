package au.com.totemsoft.amax.tool;

import java.nio.CharBuffer;

import com.lmax.disruptor.RingBuffer;

import au.com.totemsoft.amax.model.RuleEvent;
import au.com.totemsoft.amax.model.translator.RuleEventTranslator;

/**
 * publishing messages is via the Event Publisher/Event Translator portion of the API
 */
public class RuleProducer {

    private final RingBuffer<RuleEvent> ringBuffer;

    private final RuleEventTranslator translator;

    public RuleProducer(
            RuleDisruptor disruptor,
            RuleEventTranslator translator) {
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
