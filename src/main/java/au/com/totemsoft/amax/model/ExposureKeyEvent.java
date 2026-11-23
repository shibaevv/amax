package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table(name = "series_data_key")
@JsonInclude(Include.NON_NULL)
public class ExposureKeyEvent extends AbstractEvent<Long> {

    public final static String[] IGNORE = (String[]) ArrayUtils.addAll(AbstractEvent.IGNORE,
        new String[] {"alias", "inputType", "seriesData", "children"}); // transient properties

    private static HttpMessageConverter<ExposureKeyEvent> converter;
    public void setConverter(HttpMessageConverter<ExposureKeyEvent> converter) {
        ExposureKeyEvent.converter = converter;
    }

    @JsonProperty private String type;
    @JsonProperty private String name;
    @JsonProperty private String currency;

    // from RuleInputEvent
    @JsonProperty private Integer inputType;
    @JsonProperty private Long outputExposureKeyId;

    // transient
    @JsonIgnore   private boolean exclude = false;
    @JsonProperty private SeriesDataEvent seriesData;
    @JsonProperty private List<ExposureKeyEvent> children;
    @JsonProperty private Context context;

    public ExposureKeyEvent() {
        super();
    }
    public ExposureKeyEvent(Long id, Long ruleId, Context context) {
        super(id, ruleId);
        this.context = context;
    }

    @Id
    @Column(name = "key_id")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column(name = "key_type", nullable = false)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Transient
    public boolean isWildcard() {
        return ExposureKeyTypeEnum.isWildcard(type);
    }
    @Transient
    public boolean isTemplate() {
        return ExposureKeyTypeEnum.isTemplate(type);
    }

    @NaturalId(mutable = true)
    @Column(name = "key_name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    @Column(name = "currency_code", nullable = false)
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="input_type")
    //@Transient
    public Integer getInputType() {
        return inputType;
    }
    public void setInputType(Integer inputType) {
        this.inputType = inputType;
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

    @Column(name = "output_key_id", nullable = true)
    public Long getOutputExposureKeyId() {
        return outputExposureKeyId;
    }

    public void setOutputExposureKeyId(Long outputExposureKeyId) {
        this.outputExposureKeyId = outputExposureKeyId;
    }

    @Transient
    public boolean isExclude() {
        return exclude;
    }
    public void setExclude(boolean exclude) {
        this.exclude = exclude;
    }

    @Transient
    public SeriesDataEvent getSeriesData() {
        return seriesData;
    }
    public void setSeriesData(SeriesDataEvent seriesData) {
        this.seriesData = seriesData;
    }

    @Transient
    public List<ExposureKeyEvent> getChildren() {
        return children;
    }
    public void setChildren(List<ExposureKeyEvent> children) {
        this.children = children;
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
        return String.format("%1$s: %2$6s=%3$s [%4$s] %5$3s", super.toString(), RuleInputTypeEnum.valueOf(inputType).getCode(), name, type, currency);
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