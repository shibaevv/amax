package au.com.totemsoft.amax.model;

import org.apache.commons.lang3.StringUtils;

public enum ExposureKeyTypeEnum {

    RAW("R"),
    AGGREGATED("A"),
    TEMPLATE("T"),
    WILDCARD("W"),
    ;

    private final String exposureKeyType;

    /**
     * @param name
     * @param ordinal
     */
    private ExposureKeyTypeEnum(String exposureKeyType) {
        this.exposureKeyType = exposureKeyType;
    }

    /**
     * @return the exposureKeyType
     */
    public String getType() {
        return exposureKeyType;
    }
    public String getExposureKeyType() {
        return exposureKeyType;
    }

    /**
     * @return
     */
    public String getName() {
        return name();
    }

    /**
     * @return
     */
    public String getDescription() {
        return StringUtils.capitalize(name().toLowerCase());
    }

    /**
     * CXF JAXRS checks for static fromString(String s) method, so types with no valueOf(String) factory methods can also be dealt with
     * @param s
     * @return
     */
    public static ExposureKeyTypeEnum fromString(String s) {
        if (StringUtils.isNotBlank(s)) {
            final boolean exposureKeyType = s.length() == 1;
            for (ExposureKeyTypeEnum item : values()) {
                if (exposureKeyType) {
                    if (item.getExposureKeyType().equals(s)) {
                        return item;
                    }
                } else {
                    if (item.name().equals(s)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Helper method(s)
     * @return
     */
    public static boolean isRaw(String type) {
        return RAW.getType().equals(type);
    }
    public static boolean isAggregated(String type) {
        return AGGREGATED.getType().equals(type);
    }
    public static boolean isTemplate(String type) {
        return TEMPLATE.getType().equals(type);
    }
    public static boolean isWildcard(String type) {
        return WILDCARD.getType().equals(type);
    }

}
