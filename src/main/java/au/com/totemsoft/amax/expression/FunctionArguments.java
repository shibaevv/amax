package au.com.totemsoft.amax.expression;

import java.lang.reflect.Method;

public class FunctionArguments {

    private final Method function;
    
    private final Object[] arguments;

    public FunctionArguments(Method function, Object[] arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public Method getFunction() {
        return function;
    }

    public Object[] getArguments() {
        return arguments;
    }

}
