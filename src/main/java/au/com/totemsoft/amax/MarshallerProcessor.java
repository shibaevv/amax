package au.com.totemsoft.amax;

import org.springframework.stereotype.Component;

@Component
public class MarshallerProcessor<T> extends AbstractProcessor<T> {

    public MarshallerProcessor() {
        super("Marshal message");
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        super.onEvent(event, sequence, endOfBatch);
        // TODO: implement processing logic
        
    }

}
