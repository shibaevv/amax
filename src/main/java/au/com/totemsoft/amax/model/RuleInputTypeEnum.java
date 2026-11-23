package au.com.totemsoft.amax.model;

public enum RuleInputTypeEnum {

    RULE_INPUT(0, "IN", "Rule Input"),
    RULE_OUTPUT(1, "OUT", "Rule Output"),
    RULE_GENERATED_INPUT(2, "G_IN", "Rule Generated Input"),
    RULE_GENERATED_OUTPUT(3, "G_OUT", "Rule Generated Output"),
    ;

    private final int type;

    private final String code;

    private final String description;

    /**
     * @param type
     * @param description
     */
    private RuleInputTypeEnum(int type, String code, String description) {
        this.type = type;
        this.code = code;
        this.description = description;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Helper method(s)
     * @return
     */
    public static boolean isInput(int type) {
        return RULE_INPUT.type == type;
    }
    public static boolean isOutput(int type) {
        return RULE_OUTPUT.type == type;
    }
    public static boolean isInputGenerated(int type) {
        return RULE_GENERATED_INPUT.type == type;
    }
    public static boolean isOutputGenerated(int type) {
        return RULE_GENERATED_OUTPUT.type == type;
    }

    public static RuleInputTypeEnum valueOf(int type) {
        for (RuleInputTypeEnum item : values()) {
            if (item.type == type) {
                return item;
            }
        }
        throw new IllegalArgumentException("Unknown type #" + type);
    }

}
