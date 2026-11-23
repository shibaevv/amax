package au.com.totemsoft.amax.model.translator;

import java.nio.CharBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.dao.RuleEventDao;
import au.com.totemsoft.amax.model.RuleEvent;
import au.com.totemsoft.amax.tool.BeanUtils;

/**
 *  Lambda-style API - getting ready for Java 8 Functional Interfaces
 */
@Component("ruleTranslator")
public class RuleEventTranslator extends AbstractEventTranslator<RuleEvent> {

    @Autowired private RuleEventDao ruleEventDao;

    @Override
    public void translateTo(RuleEvent event, long sequence, CharBuffer data) {
        try {
            RuleEvent source = RuleEvent.fromString(new String(data.array()));
            BeanUtils.copyProperties(source, event);
            final Long ruleId = source.getId();
            if (ruleId > 0L) {
                source = ruleEventDao.findById(ruleId);
                BeanUtils.copyProperties(source, event, RuleEvent.IGNORE);
            }
            event.set(sequence);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
