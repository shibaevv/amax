package au.com.totemsoft.amax.model;

import java.io.ByteArrayOutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
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

import au.com.totemsoft.amax.tool.TextHttpInputMessage;
import au.com.totemsoft.amax.tool.TextHttpOutputMessage;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "amax_rule")
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class RuleEvent extends AbstractEvent<Long> {

    public final static String[] IGNORE = (String[]) ArrayUtils.addAll(AbstractEvent.IGNORE,
        new String[] {"exposureKey"});

    private static HttpMessageConverter<RuleEvent> converter;
    public void setConverter(HttpMessageConverter<RuleEvent> converter) {
        RuleEvent.converter = converter;
    }

    @JsonProperty
    private String expression;

    @JsonProperty
    private List<RuleInputEvent> inputs;

    @JsonProperty
    private Context context;

    // TODO: improve wrapper
    @JsonProperty
    private ExposureKeyEvent exposureKey;

    public RuleEvent() {
        super();
    }
    public RuleEvent(Long id) {
        super(id);
    }
    public RuleEvent(Long id, Context context) {
        super(id);
        this.context = context;
    }
    public RuleEvent(Long id, ExposureKeyEvent exposureKey) {
        super(id);
        this.exposureKey = exposureKey;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
    public Long getId() {
        return super.getId();
    }

    @Column(name = "expression")
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @OneToMany(mappedBy = "rule", fetch = FetchType.EAGER)
    public List<RuleInputEvent> getInputs() {
        if (inputs == null) {
            inputs = new ArrayList<>();
        }
        return inputs;
    }
    public void setInputs(List<RuleInputEvent> inputs) {
        this.inputs = inputs;
    }

    @SuppressWarnings("unchecked")
    public Collection<RuleInputEvent> inputs(final boolean generated) {
        return inputs == null ? Collections.emptyList() : CollectionUtils.select(inputs, new Predicate() {
            public boolean evaluate(Object o) {
                final int type = ((RuleInputEvent) o).getType();
                return RuleInputTypeEnum.isInput(type) || (generated && RuleInputTypeEnum.isInputGenerated(type));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Collection<RuleInputEvent> outputs(final boolean generated) {
        return inputs == null ? Collections.emptyList() : CollectionUtils.select(inputs, new Predicate() {
            public boolean evaluate(Object o) {
                final int type = ((RuleInputEvent) o).getType();
                return RuleInputTypeEnum.isOutput(type) || (generated && RuleInputTypeEnum.isOutputGenerated(type));
            }
        });
    }

    @Transient
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    @Transient
    public ExposureKeyEvent getExposureKey() {
        return exposureKey;
    }
    public void setExposureKey(ExposureKeyEvent exposureKey) {
        this.exposureKey = exposureKey;
    }

    @Override
    public String toString() {
        return String.format("%1$s: expression=%2$s", super.toString(), expression);
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