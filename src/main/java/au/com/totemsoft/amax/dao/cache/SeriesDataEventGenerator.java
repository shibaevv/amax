package au.com.totemsoft.amax.dao.cache;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Cache key generator.
 * Used for creating a key based on the given parameters.
 */
@Component
public class SeriesDataEventGenerator {

    public static String generate(Long exposureKeyId, Date date) {
        final String format = "%1$d_%2$tY%2$tm%2$td";
        return String.format(format, exposureKeyId, date);
    }

}
