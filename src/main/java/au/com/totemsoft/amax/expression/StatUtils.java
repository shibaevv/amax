package au.com.totemsoft.amax.expression;

import java.math.BigDecimal;
import java.math.RoundingMode;

import au.com.totemsoft.amax.tool.BeanUtils;

/**
 * StatUtils provides static methods for computing statistics based on data stored in BigDecimal[] arrays.
 */
public final class StatUtils {

    private StatUtils() {
        super();
    }

    /**
     * percent user defined function
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal percent(BigDecimal v1, BigDecimal v2) {
        return v1 == null || v2 == null || v2.signum() == 0 ? null :
            v1.movePointRight(2).divide(v2, 4, RoundingMode.HALF_UP);
    }
    public static BigDecimal absPercent(BigDecimal v1, BigDecimal v2) {
        return v1 == null || v2 == null || v2.signum() == 0 ? null :
            v1.movePointRight(2).divide(v2, 4, RoundingMode.HALF_UP).abs();
    }

    /**
     * absMove user defined function
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal absMove(final BigDecimal v1, final BigDecimal v2) {
        return v1 == null || v2 == null || v1.signum() == 0 ? null :
            v1.subtract(v2);
    }

    /**
     * absMovePercent user defined function
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal absMovePercent(final BigDecimal v1, final BigDecimal v2) {
        return percent(absMove(v1, v2), v1);
    }

    /**
     * 
     * @param values
     * @return
     */
    private static int scale(final BigDecimal... values) {
        int scale = 0;
        for (BigDecimal value : values) {
            if (value != null && scale < value.scale()) {
                scale = value.scale();
            }
        }
        return scale;
    }

    public static BigDecimal avg(final BigDecimal... values) {
        //return org.apache.commons.math3.stat.StatUtils.mean(values);
        BigDecimal sum = sum(values);
        int scale = scale(values);
        return sum.divide(new BigDecimal(values.length), scale, RoundingMode.HALF_UP);
    }
    public static BigDecimal divide(final BigDecimal v1, final BigDecimal v2) {
        int scale = scale(v1, v2);
        return v1.divide(v2, scale, RoundingMode.HALF_UP);
    }
    public static BigDecimal min(final BigDecimal... values) {
        //return org.apache.commons.math3.stat.StatUtils.min(values);
        BigDecimal min = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null && value.compareTo(min) < 0) {
                min = value; // TODO: check for null or throw error
            }
        }
        return min;
    }
    public static BigDecimal max(final BigDecimal... values) {
        //return org.apache.commons.math3.stat.StatUtils.max(values);
        BigDecimal max = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null && value.compareTo(max) < 0) {
                max = value; // TODO: check for null or throw error
            }
        }
        return max;
    }
    public static BigDecimal multiply(final BigDecimal... values) {
        //return org.apache.commons.math3.stat.StatUtils.sum(values);
        BigDecimal product = null;
        for (BigDecimal value : values) {
            if (value == null) {
                continue; // TODO: check for null or throw error ???
            }
            if (product == null) {
                product = value;
            }
            else {
                product = product.multiply(value); // TODO: check for null or throw error
            }
        }
        return product;
    }
    public static BigDecimal sum(final BigDecimal... values) {
        //return org.apache.commons.math3.stat.StatUtils.sum(values);
        BigDecimal sum = null;
        for (BigDecimal value : values) {
            if (value == null) {
                continue; // TODO: check for null or throw error ???
            }
            if (sum == null) {
                sum = value;
            }
            else {
                sum = sum.add(value); // TODO: check for null or throw error
            }
        }
        return sum;
    }

    public static int compare(final BigDecimal v1, final BigDecimal v2) {
        return BeanUtils.compare(v1, v2);
    }

}
