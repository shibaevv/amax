package au.com.totemsoft.amax.rule;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.AbstractProcessor;
import au.com.totemsoft.amax.dao.CacheDao;
import au.com.totemsoft.amax.expression.EvaluateService;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.RuleEvent;
import au.com.totemsoft.amax.model.RuleInputEvent;
import au.com.totemsoft.amax.tool.ExposureKeyEventProducer;

@Component
public class RuleProcessor extends AbstractProcessor<RuleEvent> /*implements LifecycleAware*/ {

    private final ExposureKeyEventProducer exposureKeyProducer;

    private final EvaluateService evaluateService;

    private final CacheDao cacheDao;

    public RuleProcessor(
            @Qualifier("exposureKeyProducer") ExposureKeyEventProducer exposureKeyProducer,
            EvaluateService evaluateService,
            CacheDao cacheDao) {
        super("Rule Processor");
        this.exposureKeyProducer = exposureKeyProducer;
        this.evaluateService = evaluateService;
        this.cacheDao = cacheDao;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(RuleEvent event, long sequence, boolean endOfBatch) throws Exception {
        super.onEvent(event, sequence, endOfBatch);
        final long ruleId = event.getId();
        // check if rule dependencies are complete
        ExposureKeyEvent ekEvent = event.getExposureKey();
        if (ekEvent == null) {
            store(event, sequence, endOfBatch);
            return;
        }
        // store event/dependent
        store(ekEvent, sequence, endOfBatch);
        // now we can proceed with evaluation
        if (endOfBatch) {
            ValueWrapper value = cacheDao.getRuleExposureKeyStore().get(ruleId);
            List<ExposureKeyEvent> eks = (List<ExposureKeyEvent>) value.get();
            evaluateService.evaluate(event, eks);
        }
    }

    private void store(RuleEvent event, long sequence, boolean endOfBatch) throws Exception {
        log(event, sequence, endOfBatch);
        //
        final long ruleId = event.getId();
        cacheDao.getRuleStore().put(ruleId, event);
        // request for dependent data
        List<RuleInputEvent> inputs = event.getInputs();
        if (inputs.isEmpty()) {
            return; // throw ???
        }
        final Context context = event.getContext();
        // pre-load caches
        /*List<ExposureKeyEvent> exposureKeys = */cacheDao.findByRule(event, context);
        //
        CharBuffer[] exposureKeys = new CharBuffer[inputs.size()];
        int i = 0;
        for (RuleInputEvent item : inputs) {
            //log(item, i, false);
            ExposureKeyEvent ek = ExposureKeyEvent.builder()
                .id(item.getExposureKeyId())
                .ruleId(ruleId)
                .context(context)
                .build();
            ek.setInputType(item.getOutputType());
            exposureKeys[i++] = ExposureKeyEvent.toCharBuffer(ek);
        }
        exposureKeyProducer.onData(exposureKeys);
    }

    @SuppressWarnings("unchecked")
    private void store(ExposureKeyEvent event, long sequence, boolean endOfBatch) throws Exception {
        log(event, sequence, endOfBatch);
        if (event.getSeriesData() != null) log(event.getSeriesData(), sequence, endOfBatch);
        final long ruleId = event.getRuleId();
        final ValueWrapper value = cacheDao.getRuleExposureKeyStore().get(ruleId);
        final List<ExposureKeyEvent> exposureKeys;
        if (value == null) {
            exposureKeys = new ArrayList<>();
            cacheDao.getRuleExposureKeyStore().put(ruleId, exposureKeys);
        }
        else {
            exposureKeys = (List<ExposureKeyEvent>) value.get();
        }
        if (!exposureKeys.contains(event)) {
            exposureKeys.add(event);
        }
        else {
            int idx = exposureKeys.indexOf(event);
            //ExposureKeyEvent eventCached = exposureKeys.get(idx);
            exposureKeys.set(idx, event);
        }
    }

}
