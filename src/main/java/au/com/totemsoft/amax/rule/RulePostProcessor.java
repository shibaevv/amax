package au.com.totemsoft.amax.rule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.AbstractProcessor;
import au.com.totemsoft.amax.model.RuleEvent;
import au.com.totemsoft.amax.tool.RuleEventProducer;
import lombok.extern.log4j.Log4j2;

@Component("outputRuleProcessor")
@Log4j2
public class RulePostProcessor extends AbstractProcessor<RuleEvent> {

    private final RuleEventProducer producer;

    public RulePostProcessor(
            @Qualifier("ruleProducer") RuleEventProducer producer) {
        super("Publisher Processor");
        this.producer = producer;
    }

    public void onEvent(RuleEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("Thread #" + Thread.currentThread().getId() +  " " + getName() + ": #" + sequence);
        //
        // publishing message via the Event Publisher
        producer.onData(RuleEvent.toCharBuffer(event));
    }

}
