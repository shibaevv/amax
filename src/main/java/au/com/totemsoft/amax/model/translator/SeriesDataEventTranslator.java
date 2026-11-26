package au.com.totemsoft.amax.model.translator;

import java.nio.CharBuffer;

import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.dao.SeriesDataEventDao;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.tool.BeanUtils;
import lombok.RequiredArgsConstructor;

/**
 *  Lambda-style API - getting ready for Java 8 Functional Interfaces
 */
@Component("seriesDataTranslator")
@RequiredArgsConstructor
public class SeriesDataEventTranslator extends AbstractEventTranslator<SeriesDataEvent> {

    private final SeriesDataEventDao seriesDataEventDao;

    @Override
    public void translateTo(SeriesDataEvent event, long sequence, CharBuffer data) {
        try {
            SeriesDataEvent source = SeriesDataEvent.fromString(new String(data.array()));
            BeanUtils.copyProperties(source, event);
            long exposureKeyId = source.getExposureKeyId();
            if (exposureKeyId > 0L) {
                Context context = source.getContext();
                source = seriesDataEventDao.findByNaturalId(exposureKeyId, context);
                BeanUtils.copyProperties(source, event, SeriesDataEvent.IGNORE);
            }
            event.setSequence(sequence);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
