package au.com.totemsoft.amax.tool;

import com.lmax.disruptor.EventFactory;

import au.com.totemsoft.amax.model.ExposureKeyEvent;

public class ExposureKeyDisruptor extends AbstractDisruptor<ExposureKeyEvent> {

    /** In order to allow the Disruptor to preallocate event */
    public static final EventFactory<ExposureKeyEvent> EVENT_FACTORY = ExposureKeyEvent::new;

    public ExposureKeyDisruptor() {
        super(EVENT_FACTORY);
    }

}
