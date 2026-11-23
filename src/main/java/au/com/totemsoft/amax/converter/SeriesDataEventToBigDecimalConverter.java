package au.com.totemsoft.amax.converter;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;

import au.com.totemsoft.amax.model.SeriesDataEvent;

public class SeriesDataEventToBigDecimalConverter implements Converter<SeriesDataEvent, BigDecimal> {

    @Override
    public BigDecimal convert(SeriesDataEvent source) {
        return source.getExposure();
    }

}
