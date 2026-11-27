package au.com.totemsoft.amax.expression;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.IBase;
import au.com.totemsoft.amax.ServiceException;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.ExposureKeyEvent.ExposureKeyEventComparator;
import au.com.totemsoft.amax.model.SeriesDataEvent;

@Component
public class TemplateContext {

    /**
     * registering user defined functions
     */
    public static final Method
        //sum, multiply,
        tmpl,
        bstof, wstof,
        incl, excl,
        itr,
        itrSum, itrMultiply
        ;
    static {
        try {
            //sum         = TemplateContext.class.getDeclaredMethod("sum",         new Class<?>[] {Object[].class});
            //multiply    = TemplateContext.class.getDeclaredMethod("multiply",    new Class<?>[] {Object[].class});
            //
            tmpl        = TemplateContext.class.getDeclaredMethod("tmpl",        new Class<?>[] {Object[].class});
            //
            bstof       = TemplateContext.class.getDeclaredMethod("bstof",       new Class<?>[] {Integer.class, Object[].class});
            wstof       = TemplateContext.class.getDeclaredMethod("wstof",       new Class<?>[] {Integer.class, Object[].class});
            //
            incl        = TemplateContext.class.getDeclaredMethod("incl",        new Class<?>[] {Object[].class});
            excl        = TemplateContext.class.getDeclaredMethod("excl",        new Class<?>[] {Object[].class});
            //
            itr         = TemplateContext.class.getDeclaredMethod("itr",         new Class<?>[] {Object[].class, FunctionArguments.class});
            itrSum      = TemplateContext.class.getDeclaredMethod("itrSum",      new Class<?>[] {Object[].class});
            itrMultiply = TemplateContext.class.getDeclaredMethod("itrMultiply", new Class<?>[] {Object[].class});
        }
        catch (NoSuchMethodException e) {
            throw new SecurityException(e.getMessage(), e);
        }
    }

    /**
     * #MULT( #TMPL( #T1 ), 0.5 )
     * @param arguments - eg {ExposureKeyEvent, ExposureKeyEvent[], ExposureKeyEvent}
     * @return
     */
    public static ExposureKeyEvent[] tmpl(final Object... arguments) {
        ExposureKeyEvent[] result = incl(arguments);
        return result;
    }

    /**
     * #BSTOF( 5, #INCL( #W1, #EXCL( #W2, #W3 ), #W4 ) )
     * @param arguments - eg {ExposureKeyEvent, ExposureKeyEvent[], ExposureKeyEvent}
     * @return
     */
    public static ExposureKeyEvent[] bstof(Integer order, final Object... arguments) {
        ExposureKeyEvent[] result = incl(arguments);
        Arrays.sort(result, ExposureKeyEventComparator.getInstanceReverse());
        return (ExposureKeyEvent[]) ArrayUtils.subarray(result, 0, order);
    }

    /**
     * #WSTOF( 5, #INCL( #W1, #EXCL( #W2, #W3 ), #W4 ) )
     * @param arguments - eg {ExposureKeyEvent, ExposureKeyEvent[], ExposureKeyEvent}
     * @return
     */
    public static ExposureKeyEvent[] wstof(Integer order, final Object... arguments) {
        ExposureKeyEvent[] result = incl(arguments);
        Arrays.sort(result, ExposureKeyEventComparator.getInstance());
        return (ExposureKeyEvent[]) ArrayUtils.subarray(result, 0, order);
    }

    /**
     * #INCL( #W1, #EXCL( #W2, #W3 ), #W4 )
     * @param arguments - eg {ExposureKeyEvent, ExposureKeyEvent[], ExposureKeyEvent}
     * @return
     */
    public static ExposureKeyEvent[] incl(final Object... arguments) {
        List<ExposureKeyEvent> result = new ArrayList<>();
        incl(result, arguments);
        return result.toArray(new ExposureKeyEvent[0]);
    }
    private static void incl(List<ExposureKeyEvent> result, final Object... arguments) {
        for (Object argument : arguments) {
            if (argument == null) {
                continue;
            }
            if (argument instanceof ExposureKeyEvent) {
                inclExposureKeyEvent(result, (ExposureKeyEvent) argument);
            }
            else if (argument instanceof ExposureKeyEvent[])  {
                inclExposureKeyEvent(result, (ExposureKeyEvent[]) argument);
            }
            else {
                // TODO:
            }
        }
    }
    private static void inclExposureKeyEvent(List<ExposureKeyEvent> result, final ExposureKeyEvent... arguments) {
        for (ExposureKeyEvent argument : arguments) {
            if (argument == null) {
                continue;
            }
            if (argument.isWildcard()) {
                List<ExposureKeyEvent> children = argument.getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    for (Iterator<ExposureKeyEvent> iter = children.iterator(); iter.hasNext(); ) {
                        ExposureKeyEvent child = iter.next();
                        if (child != null && !child.isExclude()) {
                            if (!result.contains(child)) {
                                result.add(child);
                                iter.remove();
                            }
                        }
                    }
                }
            }
            if (!argument.isExclude()) {
                if (!result.contains(argument)) {
                    result.add(argument);
                }
            }
        }
    }

    /**
     * #EXCL( #W1, #INCL( #W2, #W3 ), #W4 )
     * @param arguments - eg {ExposureKeyEvent, ExposureKeyEvent[], ExposureKeyEvent}
     * @return
     */
    public static ExposureKeyEvent[] excl(final Object... arguments) {
        List<ExposureKeyEvent> result = new ArrayList<>();
        excl(result, arguments);
        return result.toArray(new ExposureKeyEvent[0]);
    }
    private static void excl(List<ExposureKeyEvent> result, final Object... arguments) {
        for (Object argument : arguments) {
            if (argument == null) {
                continue;
            }
            if (argument instanceof ExposureKeyEvent) {
                exclExposureKeyEvent(result, (ExposureKeyEvent) argument);
            }
            else if (argument instanceof ExposureKeyEvent[])  {
                exclExposureKeyEvent(result, (ExposureKeyEvent[]) argument);
            }
            else {
                // TODO:
            }
        }
    }
    private static void exclExposureKeyEvent(List<ExposureKeyEvent> result, final ExposureKeyEvent... arguments) {
        for (ExposureKeyEvent argument : arguments) {
            if (argument == null) {
                continue;
            }
            if (argument.isWildcard()) {
                List<ExposureKeyEvent> children = argument.getChildren();
                if (CollectionUtils.isNotEmpty(children)) {
                    for (Iterator<ExposureKeyEvent> iter = children.iterator(); iter.hasNext(); ) {
                        ExposureKeyEvent child = iter.next();
                        if (child != null && !result.contains(child)) {
                            child.setExclude(true);
                            result.add(child);
                            iter.remove();
                        }
                    }
                }
            }
            if (!result.contains(argument)) {
                argument.setExclude(true);
                result.add(argument);
            }
        }
    }

    /**
     * #ITR( #TMPL( #T1 ), #SUM( #INCL( #W2 ) ) )
     * @param arguments - eg {ExposureKeyEvent, ExposureKeyEvent[], ExposureKeyEvent}
     * @param functionArguments - eg {Method, ExposureKeyEvent[]}
     * @return
     */
    public static Object[] itr(final Object[] arguments, final FunctionArguments functionArguments) {
        // find tagTypeId to iterate over
        ExposureKeyEvent[] eks = incl(arguments);
        int idx = findWildcardTagIndex(eks);
        List<String> tags = findWildcardTags(eks, idx);
        //
        Method f = functionArguments.getFunction();
        List<ExposureKeyEvent> fArguments = new ArrayList<>();
        incl(fArguments, functionArguments.getArguments());
        //
        List<Object> result = new ArrayList<>();
        for (String tag : tags) {
            Long outputExposureKeyId = 0L;
            List<ExposureKeyEvent> tagEks = new ArrayList<>();
            for (Iterator<ExposureKeyEvent> iter = fArguments.iterator(); iter.hasNext(); ) {
                ExposureKeyEvent ek = iter.next();
                if (ek.containTag(idx, tag)) {
                    outputExposureKeyId = ek.getOutputExposureKeyId();
                    tagEks.add(ek);
                    iter.remove();
                }
            }
            try {
                BigDecimal exposure = (BigDecimal) f.invoke(null, new Object[] {tagEks.toArray()});
                if (outputExposureKeyId != null) {
                    SeriesDataEvent sd = SeriesDataEvent.builder()
                        .exposureKeyId(outputExposureKeyId)
                        .build();
                    sd.setExposure(exposure);
                    result.add(sd);
                }
                else {
                    result.add(exposure);
                }
            } catch (Exception e) {
                throw new ServiceException(e);
            }
        }
        return result.toArray();
    }

    public static Object itrSum(final Object... arguments) {
        return new FunctionArguments(ExpressionUtils.sum, arguments);
    }

    public static Object itrMultiply(final Object... arguments) {
        return new FunctionArguments(ExpressionUtils.multiply, arguments);
    }

    /**
     * @param eks
     * @return tagIndex (0 based) @see TagGroupEnum
     */
    private static int findWildcardTagIndex(ExposureKeyEvent[] eks) {
        for (ExposureKeyEvent ek : eks) {
            if (ek.isWildcard()) {
                int idx = 0;
                for (String tagName : ek.tagNames()) {
                    if (IBase.WILDCARD_TAG.equals(tagName)) {
                        return idx;
                    }
                    idx++;
                }
                throw new ServiceException("No wildcard " + IBase.WILDCARD_TAG + " found in " + ek);
            }
        }
        throw new ServiceException("No wildcard found in " + Arrays.asList(eks));
    }

    private static List<String> findWildcardTags(ExposureKeyEvent[] eks, int idx) {
        List<String> result = new ArrayList<>();
        for (ExposureKeyEvent ek : eks) {
            if (!ek.isWildcard()) {
                result.add(ek.tagNames()[idx]);
            }
        }
        return result;
    }

}
