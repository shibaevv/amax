package au.com.totemsoft.amax.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 
 * TODO: data in this Map to be modified ONLY from RuleProcessor thread.
 * Before rule can be evaluated we need to collect all required dependent items, eg inputs, seriesData, etc
 */
//@Component
public class BatchStore extends Hashtable<Long, List<LongCharBufferPair>> {

    /** serialVersionUID */
    private static final long serialVersionUID = 672720688319502492L;

    public synchronized void put(Long ruleId, LongCharBufferPair value) {
        List<LongCharBufferPair> items = get(ruleId);
        if (items == null) {
            items = new ArrayList<LongCharBufferPair>();
            put(ruleId, items);
        }
        if (items.contains(value)) {
            //items.set(items.indexOf(value), value);
        }
        else {
            items.add(value);
        }
    }

}