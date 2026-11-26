package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "amax_rule_input")
@JsonInclude(Include.NON_NULL)
public class RuleInputEvent extends AbstractEvent<Long> {

    public static final String[] IGNORE = ArrayUtils.addAll(AbstractEvent.IGNORE,
        "ruleId");

    private static HttpMessageConverter<RuleInputEvent> converter;
    public void setConverter(HttpMessageConverter<RuleInputEvent> converter) {
        RuleInputEvent.converter = converter;
    }

    @EqualsAndHashCode.Include
    @JsonProperty
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @JsonProperty private String name;

    @Column(name = "rule_id")
    @JsonIgnore private long ruleId;

    @Column(name = "key_id", nullable = true)
    @JsonProperty private Long exposureKeyId;

    @Column(name = "output_key_id", nullable = true)
    @JsonProperty private Long outputExposureKeyId;

    @Column(name="output_type")
    @JsonProperty private int outputType;

    @Transient
    public boolean isInput() {
        return RuleInputTypeEnum.isInput(outputType) || RuleInputTypeEnum.isInputGenerated(outputType);
    }
    @Transient
    public boolean isInputGenerated() {
        return RuleInputTypeEnum.isInputGenerated(outputType);
    }
    @Transient
    public boolean isOutput() {
        return RuleInputTypeEnum.isOutput(outputType) || RuleInputTypeEnum.isOutputGenerated(outputType);
    }
    @Transient
    public boolean isOutputGenerated() {
        return RuleInputTypeEnum.isOutputGenerated(outputType);
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
