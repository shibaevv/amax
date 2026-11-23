package au.com.totemsoft.amax.expression;

import au.com.totemsoft.amax.IBase;

public interface IAnalytics extends IBase<Long>/*, IDomain*/ {

    String[] IGNORE = new String[] {
        IBase.ID,
    };

    /**
     * 
     * @return
     */
    String getName();

    /**
     * 
     * @return
     */
    int getPriority();

    /**
     * <p>Some examples:
     * <pre>
     *     3 + 4
     *     name.firstName
     * </pre>
     * @return the raw expression string to parse
     */
    String getExpression();

}
