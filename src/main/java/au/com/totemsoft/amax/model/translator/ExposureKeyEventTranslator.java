package au.com.totemsoft.amax.model.translator;

import java.nio.CharBuffer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.totemsoft.amax.dao.CacheDao;
import au.com.totemsoft.amax.dao.ExposureKeyEventDao;
import au.com.totemsoft.amax.dao.SeriesDataEventDao;
import au.com.totemsoft.amax.model.Context;
import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.SeriesDataEvent;
import au.com.totemsoft.amax.tool.BeanUtils;

/**
 *  Lambda-style API - getting ready for Java 8 Functional Interfaces
 */
@Component("exposureKeyTranslator")
public class ExposureKeyEventTranslator extends AbstractEventTranslator<ExposureKeyEvent> {

    @Autowired private CacheDao cacheDao;

    @Autowired private ExposureKeyEventDao exposureKeyEventDao;
    @Autowired private SeriesDataEventDao seriesDataEventDao;

    @Override
    public void translateTo(ExposureKeyEvent event, long sequence, CharBuffer data) {
        if (data == null) {
            return;
        }
        try {
            ExposureKeyEvent source = ExposureKeyEvent.fromString(new String(data.array()));
            BeanUtils.copyProperties(source, event);
            Long exposureKeyId = source.getId();
            // ExposureKeyEvent not already loaded
            boolean load = source.getSeriesData() == null;
            if (load && exposureKeyId != null && exposureKeyId > 0L) {
                source = exposureKeyEventDao.findById(exposureKeyId);
                BeanUtils.copyProperties(source, event, ExposureKeyEvent.IGNORE); // ignore @Transient
                // check for wildcard
                // TODO: improve ExposureKeyEvent.children ???
                if (event.isWildcard()) {
                    final Context context = event.getContext();
                    final Long ruleId = event.getRuleId();
                    final Integer inputType = event.getInputType();
                    // pre-load caches
                    // TODO: RefData [] and [?], eg W2=BANK.MSG.DERIVATIVES.[MSG_MKT_REGION__DIVISION__DRVS].EQU.MARKET.WEIGHTED_MARKET.P/V.[?]
                    List<ExposureKeyEvent> eks = cacheDao.findByWildcard(event, context);
                    for (ExposureKeyEvent ek : eks) {
                        //ek.setContext(context);
                        //ek.setRuleId(ruleId);
                        if (ek.getInputType() == null) {
                            ek.setInputType(inputType);
                        }
                        SeriesDataEvent sd = seriesDataEventDao.findByNaturalId(ek.getId(), context);
                        ek.setSeriesData(sd);
                        ek.setSequence(sequence);
                    }
                    event.setChildren(eks);
                }
            }
            event.setSequence(sequence);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
