package au.com.totemsoft.amax;

import org.springframework.stereotype.Component;

/**
 * The unmarshaler turns the event data from the wire into a java object
 */
@Component
public class UnmarshallerProcessor<T> extends AbstractProcessor<T> {

    public UnmarshallerProcessor() {
        super("Unmarshal message");
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        super.onEvent(event, sequence, endOfBatch);
        // TODO: implement processing logic
        
    }

}
