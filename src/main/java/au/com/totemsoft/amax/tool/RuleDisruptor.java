package au.com.totemsoft.amax.tool;

import com.lmax.disruptor.EventFactory;

import au.com.totemsoft.amax.model.RuleEvent;

public class RuleDisruptor extends AbstractDisruptor<RuleEvent> {

    /** In order to allow the Disruptor to preallocate event */
    public static final EventFactory<RuleEvent> EVENT_FACTORY = RuleEvent::new;

    public RuleDisruptor() {
        super(EVENT_FACTORY);
    }

}
