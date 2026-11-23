package au.com.totemsoft.amax.tool;

import org.springframework.beans.BeansException;

public class BeanUtils {

    private BeanUtils() {
        super();
    }

    public static <T extends Comparable<T>> int compare(T a, T b) {
        if (a == b) {
            return 0;
        }
        if (a == null || b == null) {
            return a == null ? 1 : -1;
        }
        return a.compareTo(b);
    }

    /**
     * 
     * @param source
     * @param target
     * @param ignoreProperties - changed to ... on AO request
     * @return target
     * @throws BeansException
     */
    public static <T1, T2> T2 copyProperties(T1 source, T2 target, String... ignoreProperties) throws BeansException {
        if (source != null && target != null) {
            org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
        }
        return target;
    }

}
