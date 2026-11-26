package au.com.totemsoft.amax.model;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import au.com.totemsoft.amax.IBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(Include.NON_NULL)
public abstract class AbstractEvent<T> implements IBase<T> {

    protected static final String[] IGNORE = {"context", "sequence"};

    @JsonIgnore
    @Transient
    private long sequence;

}
