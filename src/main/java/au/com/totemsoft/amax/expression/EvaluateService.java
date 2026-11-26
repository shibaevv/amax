package au.com.totemsoft.amax.expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.IBase;
import au.com.totemsoft.amax.ServiceException;
import au.com.totemsoft.amax.model.AbstractEvent;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.RuleEvent;
import au.com.totemsoft.amax.model.RuleInputEvent;
import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.model.TagGroupEnum;
import au.com.totemsoft.amax.tool.BeanUtils;
import lombok.extern.log4j.Log4j2;


@Component
@Log4j2
public class EvaluateService {

    @Autowired private ExpressionUtils expressionUtils;

    /** A type converter can convert values between different types encountered during expression evaluation. */
    @Autowired private TypeConverter typeConverter;

    @SuppressWarnings("rawtypes")
    private void log(AbstractEvent event) {
        log.info(String.format("[Thread #%1$3d] %2$s", Thread.currentThread().getId(), event));
    }

    private EvaluationContext getEvaluationContext(String expression) throws SecurityException {
        StandardEvaluationContext context = expressionUtils.getEvaluationContext();
        //
        context.setTypeConverter(typeConverter);
        //
        expressionUtils.registerFunction(context, TemplateContext.tmpl,  "tmpl");
        expressionUtils.registerFunction(context, TemplateContext.bstof, "bstof");
        expressionUtils.registerFunction(context, TemplateContext.wstof, "wstof");
        expressionUtils.registerFunction(context, TemplateContext.excl,  "excl");
        expressionUtils.registerFunction(context, TemplateContext.incl,  "incl");
        //
        expressionUtils.registerFunction(context, TemplateContext.itr,   "itr");
        // replace function(s) from base context
        boolean itr = StringUtils.startsWithIgnoreCase(expression, "#ITR(");
        expressionUtils.registerFunction(context, itr ? TemplateContext.itrSum : ExpressionUtils.sum, "sum");
        expressionUtils.registerFunction(context, itr ? TemplateContext.itrMultiply : ExpressionUtils.multiply, "multiply", "product", "mult");
        //
        return context;
    }

    @PerformanceAccess
    public void evaluate(RuleEvent event, List<ExposureKeyEvent> eks) throws Exception {
        final long ruleId = event.getId();
        final String expression = event.getExpression();
        final EvaluationContext context = getEvaluationContext(expression);
        log.info("evaluate rule #" + ruleId + " started '" + expression + "' ..\n");
        //
        Collection<RuleInputEvent> inputs = event.inputs(false);
        Collection<RuleInputEvent> outputs = event.outputs(false);
        for (RuleInputEvent output : outputs) {
            final Long outputExposureKeyId = output.getExposureKeyId();
            // TODO: can be null ???
            if (outputExposureKeyId == null) {
                continue;
            }
            ExposureKeyEvent outputEk = null;
            for (Iterator<ExposureKeyEvent> iter = eks.iterator(); iter.hasNext(); ) {
                ExposureKeyEvent ek = iter.next();
                if (outputExposureKeyId.equals(ek.getId())) {
                    iter.remove();
                    outputEk = ek;
                    break;
                }
            }
            if (outputEk == null) {
                //log.error
                throw new ServiceException("FIXME: No output exposure key found for " + output);
                //continue;
            }
            //log(outputEk);
            SeriesDataEvent outputSd = outputEk.getSeriesData();
            if (outputSd == null) {
                log.debug("No data found for " + outputEk + ", creating new one ..");
                outputSd = SeriesDataEvent.builder()
                    .exposureKeyId(outputExposureKeyId)
                    .context(outputEk.getContext())
                    .build();
                outputEk.setSeriesData(outputSd);
            }
            Map<String, Object> parameters = new LinkedHashMap<>();
            // find matching input(s)
            for (Iterator<RuleInputEvent> iter = inputs.iterator(); iter.hasNext(); ) {
                final RuleInputEvent input = iter.next();
                final Long inputExposureKeyId = input.getExposureKeyId();
                // constant
                if (inputExposureKeyId == null) {
                    //add(parameters, input);
                    iter.remove();
                    continue;
                }
                // input has marked with outputExposureKeyId
                if (outputExposureKeyId.equals(input.getOutputExposureKeyId())) {
                    ExposureKeyEvent inputEk = findMatch(eks, input);
                    if (inputEk != null) {
                        add(parameters, inputEk);
                        iter.remove();
                    }
                }
                else {
                    List<ExposureKeyEvent> inputEks = findBestMatch(eks, input, outputEk);
                    if (inputEks != null) {
                        for (ExposureKeyEvent inputEk : inputEks) {
                            add(parameters, inputEk);
                        }
                    }
                }
            }
            //
            Object values = expressionUtils.evaluate(context, expression, parameters, Object.class);
            BigDecimal newExposure = null;
            if (values == null) {
                //throw new ServiceException
                log.warn("Evaluation return: [null]");
            }
            else if (values instanceof BigDecimal) {
                newExposure = (BigDecimal) values;
            }
            else if (values instanceof SeriesDataEvent) {
                SeriesDataEvent sd = (SeriesDataEvent) values;
                if (outputExposureKeyId == sd.getExposureKeyId()) {
                    newExposure = sd.getExposure();
                    break;
                }
            }
            else if (values instanceof ExposureKeyEvent[]) {
                ExposureKeyEvent[] ekValues = (ExposureKeyEvent[]) values;
                for (ExposureKeyEvent ekValue : ekValues) {
                    SeriesDataEvent sd = ekValue.getSeriesData();
                    newExposure = sd.getExposure();
                    break; // TODO: 
                }
            }
            else {
                throw new ServiceException("Unhandled values class: " + Arrays.asList(values));
            }
            //
            log(outputEk);
            BigDecimal oldExposure = outputSd.getExposure();
            if (oldExposure != null && BeanUtils.compare(oldExposure, newExposure) != 0) {
                log.error(outputEk + ": " + oldExposure + " != " + newExposure);
            }
            else {
                outputSd.setExposure(newExposure);
                log(outputSd);
            }
            //
            // TODO: store outputSd
            //
            System.out.println();
        }
        log.info(".. evaluate rule #" + ruleId + " completed.");
    }

    private ExposureKeyEvent findMatch(List<ExposureKeyEvent> eks, RuleInputEvent input) {
        final Long inputExposureKeyId = input.getExposureKeyId();
        for (Iterator<ExposureKeyEvent> iter = eks.iterator(); iter.hasNext(); ) {
            ExposureKeyEvent inputEk = iter.next();
            // TODO: isWildcard, isTemplate ???
            if (!inputEk.isInput()) {
                continue;
            }
            if (inputExposureKeyId != null && inputExposureKeyId.equals(inputEk.getId())) {
                iter.remove();
                return inputEk;
            }
        }
        return null;
    }

    private List<ExposureKeyEvent> findBestMatch(List<ExposureKeyEvent> eks, RuleInputEvent input, ExposureKeyEvent output) {
        final Long inputExposureKeyId = input.getExposureKeyId();
        // find best match input parameter(s)
        int distance = Integer.MAX_VALUE;
        List<ExposureKeyEvent> result = null;
        for (Iterator<ExposureKeyEvent> iter = eks.iterator(); iter.hasNext(); ) {
            ExposureKeyEvent ek = iter.next();
            if (ek.isWildcard()) {
                // recursive call for wildcard ???
            }
            if (ek.isTemplate()) {
                // TODO: ???
                continue;
            }
            if (!ek.isInput()) {
                continue;
            }
            if (inputExposureKeyId != null && inputExposureKeyId.equals(ek.getId())) {
                iter.remove();
                return Arrays.asList(ek);
            }
            //int d = StringUtils.getLevenshteinDistance(output.getName(), ek.getName());
            int d = getDistance(output, ek);
            if (distance < d) {
                continue; // bad match
            }
            if (result == null) {
                result = new ArrayList<>();
            }
            if (distance > d) {
                // new best match
                distance = d;
                if (!result.isEmpty()) {
                    result.clear();
                }
            }
            result.add(ek);
            //
            if (distance == 0) {
                break; // exact match
            }
        }
        if (CollectionUtils.isNotEmpty(result)) {
            if (distance == 0) {
                eks.removeAll(result); // exact match
            }
            return result;
        }
        return null;
    }

    /**
     * <p>Find the distance between two Strings.</p>
     * @param ek1  the first String, must not be null
     * @param ek2  the second String, must not be null
     * @return result distance
     */
    private int getDistance(ExposureKeyEvent ek1, ExposureKeyEvent ek2) {
        String[] ekTags1 = ek1.tagNames();
        String[] ekTags2 = ek2.tagNames();
        TagGroupEnum[] values = TagGroupEnum.values();
        int d = values.length - 1;
        for (TagGroupEnum value : values) {
            int idx = value.getId() - 1; // 1 based
            if (idx < 0) {
                continue;
            }
            String t1 = ekTags1[idx];
            String t2 = ekTags2[idx];
            if (t1.equals(t2) || IBase.WILDCARD_TAG.equals(t1) || IBase.WILDCARD_TAG.equals(t2)) {
                d--;
            }
        }
        return d;
    }

    @SuppressWarnings("unused")
    private void add(Map<String, Object> parameters, RuleInputEvent input) {
        log(input);
        String key = input.getName();
        if (key != null) {
            if (!parameters.containsKey(key)) {
                parameters.put(key, input);
            }
        }
    }

    private void add(Map<String, Object> parameters, ExposureKeyEvent inputEk) {
        log(inputEk);
        String key = inputEk.getName();
        SeriesDataEvent inputSd = inputEk.getSeriesData();
        log(inputSd);
        if (key != null) {
            if (!parameters.containsKey(key)) {
                parameters.put(key, inputEk);
            }
            else if (inputEk.isWildcard()) {
                parameters.put(key, inputEk); // overwrite
            }
        }
    }

}
