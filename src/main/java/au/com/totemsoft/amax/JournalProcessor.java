package au.com.totemsoft.amax;

import org.springframework.stereotype.Component;

@Component
public class JournalProcessor<T> extends AbstractProcessor<T> {

    public JournalProcessor() {
        super("Journal message");
    }

    public void onEvent(T event, long sequence, boolean endOfBatch) throws Exception {
        debug(event, sequence, endOfBatch);
        // TODO: implement processing logic
        
    }

}