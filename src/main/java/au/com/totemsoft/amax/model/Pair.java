package au.com.totemsoft.amax.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

@MappedSuperclass
public class Pair<T1 extends Serializable, T2> implements Serializable {

    private static final long serialVersionUID = 1104207767574373983L;

    @Id
    @Column(name = "key", nullable = false)
    @JsonProperty
    private T1 key;

    @Id
    @Column(name = "value", nullable = false)
    @JsonProperty
    private T2 value;

    public Pair() {
        super();
    }

    public Pair(T1 key, T2 value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the key
     */
    public T1 getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    protected void setKey(T1 key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public T2 getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    protected void setValue(T2 value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (this == other) {
            return true; // super implementation
        }
        if (other != null) {
            Pair<T1, T2> castOther = (Pair<T1, T2>) other;
            return new EqualsBuilder().append(this.key, castOther.key).append(this.value, castOther.value).isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.key).append(this.value).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("key", this.key).append("value", this.value).toString();
    }

}
