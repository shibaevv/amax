package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import au.com.totemsoft.amax.tool.BeanUtils;
import au.com.totemsoft.amax.tool.TextHttpInputMessage;
import au.com.totemsoft.amax.tool.TextHttpOutputMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "series_data")
@JsonInclude(Include.NON_NULL)
public class SeriesDataEvent extends AbstractEvent<Long> {

    public final static SeriesDataEvent NULL = new SeriesDataEvent();

    public final static String[] IGNORE = (String[]) ArrayUtils.addAll(AbstractEvent.IGNORE,
        new String[] {"date"});  // transient properties (from context)

    private static HttpMessageConverter<SeriesDataEvent> converter;
    public void setConverter(HttpMessageConverter<SeriesDataEvent> converter) {
        SeriesDataEvent.converter = converter;
    }

    @JsonProperty private long exposureKeyId;

    @JsonProperty private BigDecimal exposure;

    @JsonProperty private String currency;

    @JsonProperty private Context context;

    public SeriesDataEvent() {
        super();
    }
    public SeriesDataEvent(long exposureKeyId) {
        this.exposureKeyId = exposureKeyId;
    }
    public SeriesDataEvent(long exposureKeyId, Context context) {
        this.exposureKeyId = exposureKeyId;
        this.context = context;
    }

    @Id
    @Column(name = "series_data_id")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column(name = "key_id", nullable = false)
    public long getExposureKeyId() {
        return exposureKeyId;
    }
    public void setExposureKeyId(long exposureKeyId) {
        this.exposureKeyId = exposureKeyId;
    }

    @Column(name = "series_data_date", nullable = false)
    public Date getDate() {
        return context == null ? null : context.getDate();
    }
    public void setDate(Date date) {
        //getContext().setDate(date);
    }

    @Column(name = "series_data_value", nullable = false)
    public BigDecimal getExposure() {
        return exposure;
    }
    public void setExposure(BigDecimal exposure) {
        this.exposure = exposure;
    }

    @Column(name = "currency_code", nullable = false)
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Transient
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return String.format("%1$s: exposureKeyId=%2$10d, exposure=%3$20f, currency=%4$3s", super.toString(), exposureKeyId, exposure, currency);
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