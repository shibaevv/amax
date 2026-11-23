package au.com.totemsoft.amax.expression;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.com.totemsoft.amax.model.Pair;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
public class ExpressionUtils {

    // to get access to any spring bean (that provides some calculation functions)
    @Autowired private BeanResolver beanResolver;

    /** expressionParser */
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    public <T> T evaluate(IAnalytics analytics, Map<String, Object> parameters, Class<T> desiredResultType)
        throws ParseException, EvaluationException {
        return evaluate(getEvaluationContext(), analytics.getExpression(), parameters, desiredResultType);
    }
    public <T> T evaluate(final EvaluationContext context, String expression, Map<String, Object> parameters, Class<T> desiredResultType)
        throws ParseException, EvaluationException {
        for (Entry<String, Object> entry : parameters.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        return expressionParser.parseExpression(expression).getValue(context, desiredResultType);
    }

    /**
     * 
     * @param analytics
     * @param parameter
     * @param desiredResultType
     * @return
     * @throws ParseException an exception occurred during parsing
     * @throws EvaluationException if there is a problem during evaluation
     */
    public <T> T evaluate(IAnalytics analytics, Pair<String, Object> parameter, Class<T> desiredResultType)
        throws ParseException, EvaluationException {
        return evaluate(getEvaluationContext(), analytics.getExpression(), parameter, desiredResultType);
    }
    public <T> T evaluate(final EvaluationContext context, String expression, Pair<String, Object> parameter, Class<T> desiredResultType)
        throws ParseException, EvaluationException {
        context.setVariable(parameter.getKey(), parameter.getValue());
        return expressionParser.parseExpression(expression).getValue(context, desiredResultType);
    }

    public void registerFunction(StandardEvaluationContext context, Method method, String... names) {
        for (String name : names) {
            context.registerFunction(name.toLowerCase(), method);
            context.registerFunction(name.toUpperCase(), method);
        }
    }

    public StandardEvaluationContext getEvaluationContext() throws SecurityException {
        StandardEvaluationContext context = new StandardEvaluationContext();
        // lookup beans from an expression using the (@) symbol
        context.setBeanResolver(beanResolver);
        // registering user defined functions
        context.registerFunction("percent", percentMethod);
        context.registerFunction("absPercent", absPercentMethod);
        context.registerFunction("absMove", absMoveMethod);
        context.registerFunction("absMovePercent", absMovePercentMethod);
        // case sensitive spel function name
        registerFunction(context, avg, "avg", "avr", "mean");
        registerFunction(context, divide, "div", "divide", "divd");
        registerFunction(context, max, "max");
        registerFunction(context, min, "min");
        registerFunction(context, multiply, "multiply", "product", "mult");
        registerFunction(context, sum, "sum");
        registerFunction(context, compare, "compare");
        return context;
    }

    /**
     * registering user defined functions
     */
    public static final Method percentMethod, absPercentMethod, absMoveMethod, absMovePercentMethod
        , avg, divide, max, min, multiply, sum, compare;
    static {
        try {
            percentMethod = StatUtils.class.getDeclaredMethod("percent", BigDecimal.class, BigDecimal.class);
            absPercentMethod = StatUtils.class.getDeclaredMethod("absPercent", BigDecimal.class, BigDecimal.class);
            absMoveMethod = StatUtils.class.getDeclaredMethod("absMove", BigDecimal.class, BigDecimal.class);
            absMovePercentMethod = StatUtils.class.getDeclaredMethod("absMovePercent", BigDecimal.class, BigDecimal.class);
            //
            avg = StatUtils.class.getDeclaredMethod("avg", BigDecimal[].class);
            divide = StatUtils.class.getDeclaredMethod("divide", BigDecimal.class, BigDecimal.class);
            max = StatUtils.class.getDeclaredMethod("max", BigDecimal[].class);
            min = StatUtils.class.getDeclaredMethod("min", BigDecimal[].class);
            multiply = StatUtils.class.getDeclaredMethod("multiply", BigDecimal[].class);
            sum = StatUtils.class.getDeclaredMethod("sum", BigDecimal[].class);
            compare = StatUtils.class.getDeclaredMethod("compare", BigDecimal.class, BigDecimal.class);
        }
        catch (NoSuchMethodException e) {
            throw new SecurityException(e.getMessage(), e);
        }
    }

}
