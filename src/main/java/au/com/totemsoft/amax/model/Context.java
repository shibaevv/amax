package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.totemsoft.amax.tool.TextHttpInputMessage;
import au.com.totemsoft.amax.tool.TextHttpOutputMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class Context {

    private static HttpMessageConverter<Context> converter;
    public static void setConverter(HttpMessageConverter<Context> converter) {
        Context.converter = converter;
    }

    @JsonProperty
    private Date date;

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