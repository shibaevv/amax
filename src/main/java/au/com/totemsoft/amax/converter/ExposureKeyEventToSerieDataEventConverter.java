package au.com.totemsoft.amax.converter;

import org.springframework.core.convert.converter.Converter;

import au.com.totemsoft.amax.model.ExposureKeyEvent;
import au.com.totemsoft.amax.model.SeriesDataEvent;

public class ExposureKeyEventToSerieDataEventConverter implements Converter<ExposureKeyEvent, SeriesDataEvent> {

    @Override
    public SeriesDataEvent convert(ExposureKeyEvent source) {
        return source.getSeriesData();
    }

}
