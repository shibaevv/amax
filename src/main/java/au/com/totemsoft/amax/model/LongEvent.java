package au.com.totemsoft.amax.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LongEvent {

    @JsonProperty
    private long value;

    public LongEvent() {
        super();
    }
    public LongEvent(long value) {
        this.value = value;
    }

    public long get() {
        return value;
    }
    public void set(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": value=" + value;
    }

}