package au.com.totemsoft.amax.model;

/** TagGroupEnum - database copy */
public enum TagGroupEnum {
    NONE(null),
    ENTITY("Entity"),
    GROUP("Group"),
    DIVISION("Division"),
    DESK("Desk"),
    ASSET_CLASS("Asset Class"),
    REPORT_TYPE("Report Type"),
    RISK_CATEGORY("Risk Category"),
    MOVE_TYPE("Move Type"),
    MOVE("Move");
    private final String name;
    private TagGroupEnum(String name) {
        this.name = name;
    }
    public int getId() {
        return ordinal();
    }
    public Long getLongId() {
        return new Long(ordinal());
    }
    public String getName() {
        return name;
    }
    public boolean isEntity() {
        return getId() == ENTITY.ordinal();
    }
    public boolean isMove() {
        return getId() == MOVE.ordinal();
    }

    public static TagGroupEnum findByTagGroupId(Long tagGroupId) {
        for(TagGroupEnum tagGroup : TagGroupEnum.values()) {
            if(tagGroup.getId() == tagGroupId.intValue()) {
                return tagGroup;
            }
        }
        return null;
    }
}
