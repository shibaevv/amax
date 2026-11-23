package au.com.totemsoft.amax.converter;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;

import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.SeriesDataEvent;

public class ExposureKeyEventToBigDecimalConverter implements Converter<ExposureKeyEvent, BigDecimal> {

    @Override
    public BigDecimal convert(ExposureKeyEvent source) {
        SeriesDataEvent dependent = source.getSeriesData();
        return dependent == null ? null : dependent.getExposure();
    }

}
