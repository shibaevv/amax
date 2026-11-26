package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "amax_rule")
@JsonInclude(Include.NON_NULL)
public class RuleEvent extends AbstractEvent<Long> {

    public static final String[] IGNORE = ArrayUtils.addAll(AbstractEvent.IGNORE,
        "exposureKey");

    private static HttpMessageConverter<RuleEvent> converter;
    public void setConverter(HttpMessageConverter<RuleEvent> converter) {
        RuleEvent.converter = converter;
    }

    @EqualsAndHashCode.Include
    @JsonProperty
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonProperty
    @Column(name = "expression")
    private String expression;

    @JsonProperty
    @OneToMany(mappedBy = "ruleId", fetch = FetchType.EAGER)
    private List<RuleInputEvent> inputs;

    @JsonProperty
    @Transient
    private Context context;

    // TODO: improve wrapper
    @JsonProperty
    @Transient
    private ExposureKeyEvent exposureKey;

    @SuppressWarnings("unchecked")
    public Collection<RuleInputEvent> inputs(final boolean generated) {
        return inputs == null ? Collections.emptyList() : CollectionUtils.select(inputs, new Predicate() {
            public boolean evaluate(Object o) {
                final int type = ((RuleInputEvent) o).getOutputType();
                return RuleInputTypeEnum.isInput(type) || (generated && RuleInputTypeEnum.isInputGenerated(type));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Collection<RuleInputEvent> outputs(final boolean generated) {
        return inputs == null ? Collections.emptyList() : CollectionUtils.select(inputs, new Predicate() {
            public boolean evaluate(Object o) {
                final int type = ((RuleInputEvent) o).getOutputType();
                return RuleInputTypeEnum.isOutput(type) || (generated && RuleInputTypeEnum.isOutputGenerated(type));
            }
        });
    }

    /**
     * Helper method
     * @param value
     * @return
     */
    public static RuleEvent fromString(String value) throws Exception {
        return converter.read(RuleEvent.class, new TextHttpInputMessage(value.getBytes()));
    }

    public static String toString(RuleEvent value) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        converter.write(value, MediaType.APPLICATION_JSON, new TextHttpOutputMessage(baos));
        return new String(baos.toByteArray());
    }

    public static CharBuffer toCharBuffer(RuleEvent value) throws Exception {
        String sValue = toString(value);
        CharBuffer valueBuf = CharBuffer.allocate(sValue.length());
        valueBuf.put(sValue);
        return valueBuf;
    }

}
