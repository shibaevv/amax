package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.NaturalId;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.totemsoft.amax.IExposureKey;
import au.com.totemsoft.amax.tool.BeanUtils;
import au.com.totemsoft.amax.tool.TextHttpInputMessage;
import au.com.totemsoft.amax.tool.TextHttpOutputMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "series_data_key")
@JsonInclude(Include.NON_NULL)
public class ExposureKeyEvent extends AbstractEvent<Long> {

    public static final String[] IGNORE = ArrayUtils.addAll(AbstractEvent.IGNORE,
        "inputType", "seriesData", "children"); // transient properties

    private static HttpMessageConverter<ExposureKeyEvent> converter;
    public static void setConverter(HttpMessageConverter<ExposureKeyEvent> converter) {
        ExposureKeyEvent.converter = converter;
    }

    @EqualsAndHashCode.Include
    @JsonProperty
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private Long id;

    @JsonProperty
    @Column(name = "key_type", nullable = false)
    private String type;

    @JsonProperty
    @NaturalId(mutable = true)
    @Column(name = "key_name")
    private String name;

    @JsonProperty
    @Column(name = "currency_code", nullable = false)
    private String currency;

    // from RuleInputEvent
    @JsonProperty
    @Column(name="input_type")
    //@Transient
    private Integer inputType;

    @JsonProperty
    @Column(name = "output_key_id", nullable = true)
    private Long outputExposureKeyId;

    @JsonProperty
    @Transient
    private long ruleId;

    // transient
    @JsonIgnore
    @Transient
    private boolean exclude;

    @JsonProperty
    @Transient
    private SeriesDataEvent seriesData;

    @JsonProperty
    @Transient
    private List<ExposureKeyEvent> children;

    @JsonProperty
    @Transient
    private Context context;

    @Transient
    public boolean isWildcard() {
        return ExposureKeyTypeEnum.isWildcard(type);
    }
    @Transient
    public boolean isTemplate() {
        return ExposureKeyTypeEnum.isTemplate(type);
    }

    public String entityName() {
        return name.substring(0, name.indexOf(IExposureKey.SEPARATOR));
    }

    public String[] tagNames() {
        return name.split(IExposureKey.SEPARATOR_REGEX);
    }

    public boolean containTag(int idx, String tag) {
        return tag.equals(tagNames()[idx]);
    }

    @Transient
    public boolean isInput() {
        return RuleInputTypeEnum.isInput(inputType) || RuleInputTypeEnum.isInputGenerated(inputType);
    }
    @Transient
    public boolean isInputGenerated() {
        return RuleInputTypeEnum.isInputGenerated(inputType);
    }
    @Transient
    public boolean isOutput() {
        return RuleInputTypeEnum.isOutput(inputType) || RuleInputTypeEnum.isOutputGenerated(inputType);
    }
    @Transient
    public boolean isOutputGenerated() {
        return RuleInputTypeEnum.isOutputGenerated(inputType);
    }

    /**
     * Helper method
     * @param value
     * @return
     */
    public static ExposureKeyEvent fromString(String value) throws Exception {
        return converter.read(ExposureKeyEvent.class, new TextHttpInputMessage(value.getBytes()));
    }

    public static String toString(ExposureKeyEvent value) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        converter.write(value, MediaType.APPLICATION_JSON, new TextHttpOutputMessage(baos));
        return new String(baos.toByteArray());
    }

    public static CharBuffer toCharBuffer(ExposureKeyEvent value) throws Exception {
        String sValue = toString(value);
        CharBuffer valueBuf = CharBuffer.allocate(sValue.length());
        valueBuf.put(sValue);
        return valueBuf;
    }

    /**
     *
     */
    public static class ExposureKeyEventComparator implements Comparator<ExposureKeyEvent> {
        private static ExposureKeyEventComparator instance = new ExposureKeyEventComparator(false);
        public static ExposureKeyEventComparator getInstance() {return instance;}
        private static ExposureKeyEventComparator instanceReverse = new ExposureKeyEventComparator(true);
        public static ExposureKeyEventComparator getInstanceReverse() {return instanceReverse;}
        private final boolean reverse;
        private ExposureKeyEventComparator(boolean reverse) {this.reverse = reverse;}
        public int compare(ExposureKeyEvent ek1, ExposureKeyEvent ek2) {
            BigDecimal a = ek1 == null || ek1.seriesData == null ? null : ek1.seriesData.getExposure();
            BigDecimal b = ek2 == null || ek2.seriesData == null ? null : ek2.seriesData.getExposure();
            return BeanUtils.compare(a, b) * (reverse ? -1 : 1);
        }
    }

}