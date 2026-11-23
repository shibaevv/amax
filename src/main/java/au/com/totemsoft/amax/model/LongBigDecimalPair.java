package au.com.totemsoft.amax.model;

import java.math.BigDecimal;

public class LongBigDecimalPair extends Pair<Long, BigDecimal> {

    private static final long serialVersionUID = 6323339867520543216L;

    public LongBigDecimalPair() {
        super();
    }

    public LongBigDecimalPair(Long key, BigDecimal value) {
        super(key, value);
    }

    @Override
    public void setKey(Long key) {
        super.setKey(key);
    }

    @Override
    public void setValue(BigDecimal value) {
        super.setValue(value);
    }

}
