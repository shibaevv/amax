package au.com.totemsoft.amax.rule;

import java.nio.CharBuffer;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.AbstractProcessor;
import au.com.totemsoft.amax.model.BatchStore;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.LongCharBufferPair;
import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.tool.ExposureKeyEventProducer;

@Component
public class SerieDataProcessor extends AbstractProcessor<SeriesDataEvent> {

    private final ExposureKeyEventProducer exposureKeyProducer;

    private BatchStore batchStore = new BatchStore();

    public SerieDataProcessor(
            @Qualifier("exposureKeyProducer") ExposureKeyEventProducer exposureKeyProducer) {
        super("Serie Data Processor");
        this.exposureKeyProducer = exposureKeyProducer;
    }

    @Override
    public void onEvent(SeriesDataEvent event, long sequence, boolean endOfBatch) throws Exception {
        super.onEvent(event, sequence, endOfBatch);
        final Context context = event.getContext();
        final Long ruleId = event.getRuleId();
        final Long exposureKeyId = event.getExposureKeyId();
        // non-empty object
        if (event.getId() != null && event.getId() > 0L) {
            log(event, sequence, endOfBatch);
            //
            // feed event back to exposureKey processor (has to wrap it as ExposureKeyEvent)
            ExposureKeyEvent wrapper = ExposureKeyEvent.builder()
                    .id(exposureKeyId)
                    .ruleId(ruleId)
                    .context(context)
                    .build();
            wrapper.setSeriesData(event);
            batchStore.put(ruleId, new LongCharBufferPair(exposureKeyId, ExposureKeyEvent.toCharBuffer(wrapper)));
        }
        //
        if (endOfBatch) {
            List<LongCharBufferPair> items = batchStore.remove(ruleId);
            if (items != null && !items.isEmpty()) {
                CharBuffer[] data = new CharBuffer[items.size()];
                int i = 0;
                for (LongCharBufferPair item : items) {
                    data[i++] = item.getValue();
                }
                exposureKeyProducer.onData(data);
            }
            else {
                // no data found
                ExposureKeyEvent wrapper = ExposureKeyEvent.builder()
                        .id(exposureKeyId)
                        .ruleId(ruleId)
                        .context(context)
                        .build();
                wrapper.setSeriesData(event);//SeriesDataEvent.NULL);
                CharBuffer data = ExposureKeyEvent.toCharBuffer(wrapper);
                exposureKeyProducer.onData(data);
            }
        }
    }

}
