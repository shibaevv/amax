package au.com.totemsoft.amax.rule;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.AbstractProcessor;
import au.com.totemsoft.amax.model.BatchStore;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.LongCharBufferPair;
import au.com.totemsoft.amax.model.RuleEvent;
import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.tool.RuleEventProducer;
import au.com.totemsoft.amax.tool.SeriesDataEventProducer;

@Component
public class ExposureKeyProcessor extends AbstractProcessor<ExposureKeyEvent> {

    private final RuleEventProducer ruleProducer;

    private final SeriesDataEventProducer seriesDataProducer;

    private BatchStore batchStore = new BatchStore();

    public ExposureKeyProcessor(
            @Qualifier("ruleProducer") RuleEventProducer ruleProducer,
            @Qualifier("seriesDataProducer") SeriesDataEventProducer seriesDataProducer) {
        super("Exposure Key Processor");
        this.ruleProducer = ruleProducer;
        this.seriesDataProducer = seriesDataProducer;
    }

    public void onEvent(ExposureKeyEvent event, long sequence, boolean endOfBatch) throws Exception {
        final Context context = event.getContext();
        final Long ruleId = event.getRuleId();
        final Long exposureKeyId = event.getId();
        // constant
        if (exposureKeyId == null) {
            log(event, sequence, endOfBatch);
            //batchStore.put(ruleId, new LongCharBufferPair(exposureKeyId, RuleEvent.toCharBuffer(new RuleEvent(ruleId, event))));
            return;
        }
        //
        // feed event back to rule processor (has to wrap it as RuleEvent)
        if (event.getSeriesData() == null) {
            log(event, sequence, endOfBatch);
            batchStore.put(ruleId, new LongCharBufferPair(exposureKeyId, RuleEvent.toCharBuffer(new RuleEvent(ruleId, event))));
            if (CollectionUtils.isNotEmpty(event.getChildren())) {
                for (ExposureKeyEvent item : event.getChildren()) {
                    batchStore.put(ruleId, new LongCharBufferPair(exposureKeyId, RuleEvent.toCharBuffer(new RuleEvent(ruleId, item))));
                }
            }
            if (endOfBatch) {
                List<CharBuffer> data = new ArrayList<CharBuffer>();
                for (LongCharBufferPair item : batchStore.get(ruleId)) {
                    Long key = item.getKey();
                    // constant
                    if (key == null) {
                        continue;
                    }
                    SeriesDataEvent sd = new SeriesDataEvent(key, context);
                    sd.setRuleId(ruleId);
                    data.add(SeriesDataEvent.toCharBuffer(sd));
                }
                seriesDataProducer.onData(data.toArray(new CharBuffer[0]));
            }
        }
        else {
            // copy seriesData (stored in dependent)
            List<LongCharBufferPair> items = batchStore.get(ruleId);
            for (LongCharBufferPair item : items) {
                RuleEvent r = RuleEvent.fromString(new String(item.getValue().array()));
                ExposureKeyEvent ek = r.getExposureKey();
                if (exposureKeyId.equals(ek.getId())) {
                    SeriesDataEvent sd = event.getSeriesData();
                    if (sd != null && sd.getId() != null) {
                        ek.setSeriesData(sd);
                    }
                    item.setValue(RuleEvent.toCharBuffer(r));
                    //log(ek, sequence, endOfBatch);
                    break;
                }
            }
            if (endOfBatch) {
                items = batchStore.remove(ruleId);
                CharBuffer[] exposureKeys = new CharBuffer[items.size()];
                int i = 0;
                for (LongCharBufferPair item : items) {
                    exposureKeys[i++] = item.getValue();
                }
                ruleProducer.onData(exposureKeys);
            }
        }
    }

}
