package au.com.totemsoft.amax;

import org.springframework.stereotype.Component;

@Component
public class MarshallerProcessor<T> extends AbstractProcessor<T> {

    public MarshallerProcessor() {
        super("Unmarshal message");
    }

    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        debug(event, sequence, endOfBatch);
        // TODO: implement processing logic
        
    }

}