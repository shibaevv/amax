package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.totemsoft.amax.tool.TextHttpInputMessage;
import au.com.totemsoft.amax.tool.TextHttpOutputMessage;

@JsonInclude(Include.NON_NULL)
public class Context {

    private static HttpMessageConverter<Context> converter;
    public void setConverter(HttpMessageConverter<Context> converter) {
        Context.converter = converter;
    }

    @JsonProperty
    private Date date;

    Context() {
        super();
    }
    public Context(LocalDate date) {
        this.date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%1$s: date=%2$tY-%2$tm-%2$td", getClass().getSimpleName(), date);
    }

    /**
     * Helper method
     * @param value
     * @return
     */
    public static Context fromString(String value, HttpMessageConverter<Context> converter) throws Exception {
        return converter.read(Context.class, new TextHttpInputMessage(value.getBytes()));
    }

    public static String toString(Context value) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        converter.write(value, MediaType.APPLICATION_JSON, new TextHttpOutputMessage(baos));
        return new String(baos.toByteArray());
    }

    public static CharBuffer toCharBuffer(Context value) throws Exception {
        String sValue = toString(value);
        CharBuffer valueBuf = CharBuffer.allocate(sValue.length());
        valueBuf.put(sValue);
        return valueBuf;
    }

}