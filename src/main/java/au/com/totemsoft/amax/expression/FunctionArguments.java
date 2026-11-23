package au.com.totemsoft.amax.expression;

import java.lang.reflect.Method;

/**
 * 
 * @author vchibaev (Valeri SHIBAEV)
 */
public class FunctionArguments {

    private final Method function;
    
    private final Object[] arguments;

    public FunctionArguments(Method function, Object[] arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    /**
     * @return the function
     */
    public Method getFunction() {
        return function;
    }

    /**
     * @return the arguments
     */
    public Object[] getArguments() {
        return arguments;
    }

}