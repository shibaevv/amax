package au.com.totemsoft.amax;

public interface IBase<T> {

    /** ID property name */
    String ID = "id";

    /** IGNORE property(s) */
    String[] IGNORE = new String[] {};

    /** SEPARATOR */
    String SEPARATOR = "|";
    String SEPARATOR_REGEX = "\\|";

    // gmer
    String WILDCARD = "{*}";
    String WILDCARD_REGEX = ".+";

    // aeg
    String WILDCARD_TAG = "[ALL]";
    String WILDCARD_TAG_REPLACE = "[?]";

    String WILDCARD_SQL = "%";
    String WILDCARD_SQL_PRESERVE = "[" + WILDCARD_SQL + "]";

    /** represents any character sequence */
    String LUCENE_WILDCARD = "*";
    /** represents a single character */
    String LUCENE_WILDCARD_1 = "?";

    T getId();

}
