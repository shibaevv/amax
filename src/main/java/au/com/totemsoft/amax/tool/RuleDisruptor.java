package au.com.totemsoft.amax.tool;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lmax.disruptor.EventFactory;

import au.com.totemsoft.amax.model.RuleEvent;

@Component
//create a brand-new instance every time the component is injected
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RuleDisruptor extends AbstractDisruptor<RuleEvent> {

    /** In order to allow the Disruptor to preallocate event */
    public static final EventFactory<RuleEvent> FACTORY = new EventFactory<RuleEvent>() {
        public RuleEvent newInstance() {
            return new RuleEvent();
        }
    };

    public RuleDisruptor() {
        super(FACTORY);
    }

}
