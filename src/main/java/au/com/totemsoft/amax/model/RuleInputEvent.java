package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.totemsoft.amax.tool.TextHttpInputMessage;
import au.com.totemsoft.amax.tool.TextHttpOutputMessage;

@Entity
@Table(name = "amax_rule_input")
@JsonInclude(Include.NON_NULL)
public class RuleInputEvent extends AbstractEvent<Long> {

    public static final String[] IGNORE = {"rule", "sequence"};

    private static HttpMessageConverter<RuleInputEvent> converter;
    public void setConverter(HttpMessageConverter<RuleInputEvent> converter) {
        RuleInputEvent.converter = converter;
    }

    @JsonProperty private String name;

    @JsonIgnore private RuleEvent rule;

    @JsonProperty private Long exposureKeyId;

    @JsonProperty private Long outputExposureKeyId;

    @JsonProperty private int type;

    public RuleInputEvent() {
        super();
    }
    public RuleInputEvent(Long id) {
        super(id);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return super.getId();
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "rule_id")
    public RuleEvent getRule() {
        return rule;
    }

    public void setRule(RuleEvent rule) {
        this.rule = rule;
    }

    @Column(name = "key_id", nullable = true)
    public Long getExposureKeyId() {
        return exposureKeyId;
    }

    public void setExposureKeyId(Long exposureKeyId) {
        this.exposureKeyId = exposureKeyId;
    }

    @Column(name = "output_key_id", nullable = true)
    public Long getOutputExposureKeyId() {
        return outputExposureKeyId;
    }

    public void setOutputExposureKeyId(Long outputExposureKeyId) {
        this.outputExposureKeyId = outputExposureKeyId;
    }

    @Column(name="output_type")
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    @Transient
    public boolean isInput() {
        return RuleInputTypeEnum.isInput(type) || RuleInputTypeEnum.isInputGenerated(type);
    }
    @Transient
    public boolean isInputGenerated() {
        return RuleInputTypeEnum.isInputGenerated(type);
    }
    @Transient
    public boolean isOutput() {
        return RuleInputTypeEnum.isOutput(type) || RuleInputTypeEnum.isOutputGenerated(type);
    }
    @Transient
    public boolean isOutputGenerated() {
        return RuleInputTypeEnum.isOutputGenerated(type);
    }

    @Override
    public String toString() {
        return String.format("%1$s: %2$6s", super.toString(), isInput() ? name : RuleInputTypeEnum.valueOf(type).getCode());
    }

    /**
     * Helper method
     * @param value
     * @return
     */
    public static RuleInputEvent fromString(String value) throws Exception {
        return converter.read(RuleInputEvent.class, new TextHttpInputMessage(value.getBytes()));
    }

    public static String toString(RuleInputEvent value) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        converter.write(value, MediaType.APPLICATION_JSON, new TextHttpOutputMessage(baos));
        return new String(baos.toByteArray());
    }

    public static CharBuffer toCharBuffer(RuleInputEvent value) throws Exception {
        String sValue = toString(value);
        CharBuffer valueBuf = CharBuffer.allocate(sValue.length());
        valueBuf.put(sValue);
        return valueBuf;
    }

}