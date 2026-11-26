package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.totemsoft.amax.tool.BeanUtils;
import au.com.totemsoft.amax.tool.TextHttpInputMessage;
import au.com.totemsoft.amax.tool.TextHttpOutputMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "series_data")
@JsonInclude(Include.NON_NULL)
public class SeriesDataEvent extends AbstractEvent<Long> {

    public static final SeriesDataEvent NULL = new SeriesDataEvent();

    public static final String[] IGNORE = ArrayUtils.addAll(AbstractEvent.IGNORE,
        "date");  // transient properties (from context)

    private static HttpMessageConverter<SeriesDataEvent> converter;
    public void setConverter(HttpMessageConverter<SeriesDataEvent> converter) {
        SeriesDataEvent.converter = converter;
    }

    @EqualsAndHashCode.Include
    @JsonProperty
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "series_data_id")
    private Long id;

    @JsonProperty
    @Column(name = "key_id", nullable = false)
    private long exposureKeyId;

    @JsonProperty
    @Column(name = "series_data_value", nullable = false)
    private BigDecimal exposure;

    @JsonProperty
    @Column(name = "currency_code", nullable = false)
    private String currency;

    @Column(name = "series_data_date", nullable = false)
    private Date date;

    @JsonProperty
    @Transient
    private long ruleId;

    @JsonProperty
    @Transient
    private Context context;

    public Date getDate() {
        return context == null ? null : context.getDate();
    }
    public void setDate(Date date) {
        //getContext().setDate(date);
    }

    /**
     * Helper method
     * @param value
     * @return
     */
    public static SeriesDataEvent fromString(String value) throws Exception {
        return converter.read(SeriesDataEvent.class, new TextHttpInputMessage(value.getBytes()));
    }

    public static String toString(SeriesDataEvent value) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        converter.write(value, MediaType.APPLICATION_JSON, new TextHttpOutputMessage(baos));
        return new String(baos.toByteArray());
    }

    public static CharBuffer toCharBuffer(SeriesDataEvent value) throws Exception {
        String sValue = toString(value);
        CharBuffer valueBuf = CharBuffer.allocate(sValue.length());
        valueBuf.put(sValue);
        return valueBuf;
    }

    /**
     *
     */
    public static class SerieDataEventComparator implements Comparator<SeriesDataEvent> {
        private static SerieDataEventComparator instance = new SerieDataEventComparator(false);
        public static SerieDataEventComparator getInstance() {return instance;}
        private static SerieDataEventComparator instanceReverse = new SerieDataEventComparator(true);
        public static SerieDataEventComparator getInstanceReverse() {return instanceReverse;}
        private final boolean reverse;
        private SerieDataEventComparator(boolean reverse) {this.reverse = reverse;}
        public int compare(SeriesDataEvent sd1, SeriesDataEvent sd2) {
            return BeanUtils.compare(sd1 == null ? null : sd1.exposure, sd2 == null ? null : sd2.exposure) * (reverse ? -1 : 1);
        }
    }

}