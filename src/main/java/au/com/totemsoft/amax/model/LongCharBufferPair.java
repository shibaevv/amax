package au.com.totemsoft.amax.model;

import java.nio.CharBuffer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LongCharBufferPair extends Pair<Long, CharBuffer> {

    /** serialVersionUID */
    private static final long serialVersionUID = 8531777776396248038L;

    public LongCharBufferPair() {
        super();
    }

    public LongCharBufferPair(Long key, CharBuffer value) {
        super(key, value);
    }

    @Override
    public void setKey(Long key) {
        super.setKey(key);
    }

    @Override
    public void setValue(CharBuffer value) {
        super.setValue(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true; // super implementation
        }
        if (other != null) {
            LongCharBufferPair castOther = (LongCharBufferPair) other;
            return new EqualsBuilder().append(this.getKey(), castOther.getKey()).isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getKey()).toHashCode();
    }

}
