package au.com.totemsoft.amax;

import org.springframework.stereotype.Component;

@Component
public class ReplicatorProcessor<T> extends AbstractProcessor<T> {

    public ReplicatorProcessor() {
        super("Replicate message");
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        trace(event, sequence, endOfBatch);
        // TODO: implement processing logic
        
    }

}
