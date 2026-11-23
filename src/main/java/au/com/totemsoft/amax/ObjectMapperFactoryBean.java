package au.com.totemsoft.amax;

import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {

    public ObjectMapper getObject() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.disable(MapperFeature.AUTO_DETECT_FIELDS);
        mapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
        mapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable (DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.disable(MapperFeature.AUTO_DETECT_CREATORS);
        mapper.disable(MapperFeature.AUTO_DETECT_FIELDS);
        mapper.disable(MapperFeature.AUTO_DETECT_SETTERS);
        //
        //mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false); // @since 2.2
        //mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd"));
        //mapper.setTimeZone(TimeZone.getTimeZone("GMT")); // -Duser.timezone=GMT
        //mapper.setTimeZone(TimeZone.getTimeZone("Australia/Sydney")); // default
        return mapper;
    }

    public Class<? extends ObjectMapper> getObjectType() {
        return ObjectMapper.class;
    }

    public boolean isSingleton() {
        return false;
    }

}
