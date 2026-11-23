package au.com.totemsoft.amax.tool;

import org.springframework.stereotype.Component;

import com.lmax.disruptor.EventFactory;

import au.com.totemsoft.amax.model.SeriesDataEvent;

@Component
public class SeriesDataDisruptor extends AbstractDisruptor<SeriesDataEvent> {

    /** In order to allow the Disruptor to preallocate event */
    public static final EventFactory<SeriesDataEvent> FACTORY = new EventFactory<SeriesDataEvent>() {
        public SeriesDataEvent newInstance() {
            return new SeriesDataEvent();
        }
    };

    public SeriesDataDisruptor() {
        super(FACTORY);
    }

}