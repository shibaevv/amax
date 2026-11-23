package au.com.totemsoft.amax;

public interface IExposureKey extends IBase<Long> {

    /** SEPARATOR */
    String SEPARATOR = ".";
    String SEPARATOR_REGEX = "\\.";

    /** @see TagGroupEnum */
    String PATTERN = "{1}.{2}.{3}.{4}.{5}.{6}.{7}.{8}.{9}";

}
