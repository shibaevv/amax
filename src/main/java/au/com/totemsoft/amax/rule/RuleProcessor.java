package au.com.totemsoft.amax.rule;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.annotation.Scope;
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
//create a brand-new instance every time the component is injected
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RuleProcessor extends AbstractProcessor<RuleEvent> /*implements LifecycleAware*/ {

    private final ExposureKeyEventProducer exposureKeyProducer;

    @Autowired private CacheDao cacheDao;

    @Autowired private EvaluateService evaluateService;

    public RuleProcessor(
            @Qualifier("exposureKeyProducer") ExposureKeyEventProducer exposureKeyProducer) {
        super("Rule Processor");
        this.exposureKeyProducer = exposureKeyProducer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(RuleEvent event, long sequence, boolean endOfBatch) throws Exception {
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
            ExposureKeyEvent ek = new ExposureKeyEvent(item.getExposureKeyId(), ruleId, context);
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
