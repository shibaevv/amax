package au.com.totemsoft.amax.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import au.com.totemsoft.amax.IBase;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public abstract class AbstractEvent<T> implements IBase<T> {

    /** "ownerId", "context", "sequence" */
    protected static final String[] IGNORE = {"ruleId", "context", "sequence"};

    @JsonProperty
    private T id;

    @JsonIgnore
    private long sequence;

    @JsonProperty
    private Long ruleId;

    protected AbstractEvent() {
        super();
    }
    protected AbstractEvent(T id) {
        this.id = id;
    }
    protected AbstractEvent(T id, Long ruleId) {
        this.id = id;
        this.ruleId = ruleId;
    }

    public T getId() {
        return id;
    }
    public void setId(T id) {
        this.id = id;
    }

    public long get() {
        return sequence;
    }
    public void set(long sequence) {
        this.sequence = sequence;
    }

    public Long getRuleId() {
        return ruleId;
    }
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true; // super implementation
        }
        if (other != null) {
            @SuppressWarnings("unchecked")
            AbstractEvent<T> castOther = (AbstractEvent<T>) other;
            if (id != null && castOther.id != null) {
                return id.equals(castOther.id);
            }
        }
        return false;
    }

    public int hashCode() {
        return id == null ? super.hashCode() : id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%1$20s: id=%2$10d, ownerId=%3$10d", getClass().getSimpleName(), id, ruleId);
    }

}