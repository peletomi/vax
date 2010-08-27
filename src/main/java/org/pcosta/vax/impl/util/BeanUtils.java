package org.pcosta.vax.impl.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.pcosta.vax.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class BeanUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BeanUtils.class);

    private static final String GET = "get";

    private static final String IS = "is";

    private static final String SET = "set";

    public static final Predicate<AnnotatedElement> IS_VALUE = new Predicate<AnnotatedElement>() {
        @Override
        public boolean apply(final AnnotatedElement element) {
            return element.getAnnotation(Value.class) != null;
        }
    };

    public static final Predicate<Method> IS_GETTER = new Predicate<Method>() {
        @Override
        public boolean apply(final Method method) {
            checkNotNull(method);
            final String name = method.getName();
            return name.startsWith(GET) || name.startsWith(IS) || name.startsWith(SET);
        }
    };

    public static final Predicate<Method> IS_SETTER = new Predicate<Method>() {
        @Override
        public boolean apply(final Method method) {
            checkNotNull(method);
            return method.getName().startsWith(SET);
        }
    };


    public static String getMethodName(final Method method) {
        String result = null;
        if (method.getName().startsWith(GET) || method.getName().startsWith(SET)) {
            result = method.getName().substring(3);
        } else if (method.getName().startsWith(IS)) {
            result = method.getName().substring(2);
        }
        if (result != null) {
            result = result.substring(0, 1).toLowerCase() + result.substring(1);
        }
        return result;
    }

    public static Method getGetter(final Method method) {
        if (IS_GETTER.apply(method)) {
            return method;
        }

        final String name = getMethodName(method);
        Method getter = null;
        try {
            getter = method.getDeclaringClass().getMethod(addPrefix(GET, name), method.getReturnType());
            if (getter == null) {
                getter = method.getDeclaringClass().getMethod(addPrefix(IS, name), method.getReturnType());
            }
        } catch (final SecurityException e) {
            LOG.warn(e.getMessage(), e);
        } catch (final NoSuchMethodException e) {
            LOG.warn(e.getMessage(), e);
        }
        return getter;
    }

    public static Method getSetter(final Method method) {
        if (IS_SETTER.apply(method)) {
            return method;
        }

        final String name = getMethodName(method);
        Method getter = null;
        try {
            getter = method.getDeclaringClass().getMethod(addPrefix(GET, name));
        } catch (final SecurityException e) {
            LOG.warn(e.getMessage(), e);
        } catch (final NoSuchMethodException e) {
            LOG.warn(e.getMessage(), e);
        }
        return getter;
    }

    static String addPrefix(final String prefix, final String string) {
        checkNotNull("prefix must not be null", prefix);
        checkNotNull("string must not be null", string);
        return prefix + string.substring(0, 1).toUpperCase() + string.substring(1);
    }

}
