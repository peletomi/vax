package org.peletomi.vax.impl.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.peletomi.vax.ValueAdapter;
import org.peletomi.vax.annotation.Value;
import org.peletomi.vax.annotation.ValueJavaAdapter;
import org.peletomi.vax.impl.ParsingDirection;
import org.peletomi.vax.impl.exception.AdapterException;
import org.peletomi.vax.impl.exception.ValidationException;
import org.peletomi.vax.impl.exception.VaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class BeanUtils {

    private static final String SETTER_NOT_FOUND = "coul not find setter for method [%s]";

    private static final String GETTER_NOT_FOUND = "coul not find getter for method [%s]";

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
            throw new IllegalStateException(String.format(GETTER_NOT_FOUND, method.getName()));
        } catch (final NoSuchMethodException e) {
            LOG.warn(e.getMessage(), e);
            throw new IllegalStateException(String.format(GETTER_NOT_FOUND, method.getName()));
        }
        return getter;
    }

    public static Method getSetter(final Method method) {
        if (IS_SETTER.apply(method)) {
            return method;
        }

        final String name = getMethodName(method);
        final Method getter = getGetter(method);
        Method setter = null;
        try {
            setter = method.getDeclaringClass().getMethod(addPrefix(SET, name), getter.getReturnType());
        } catch (final SecurityException e) {
            LOG.warn(e.getMessage(), e);
            throw new IllegalStateException(String.format(SETTER_NOT_FOUND, method.getName()));
        } catch (final NoSuchMethodException e) {
            LOG.warn(e.getMessage(), e);
            throw new IllegalStateException(String.format(SETTER_NOT_FOUND, method.getName()));
        }
        return setter;
    }

    public static Object getValue(final Object instance, final Field field) {
        Object result = null;
        final boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            result = field.get(instance);
        }
        catch (final IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        field.setAccessible(accessible);
        return result;
    }

    public static Object getValue(final Object instance, final Method method) {
        Object result = null;
        try {
            final Method getter = getGetter(method);
            result = getter.invoke(instance);
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (final InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

    /**
     * Returns the name of the element from the annotation. If not set it
     * returns the default.
     *
     * @param defaultName
     * @param element
     * @return
     */
    public static String getName(final String defaultName, final AnnotatedElement element) {
        String name = defaultName;
        final Value annotation = element.getAnnotation(Value.class);
        if (annotation != null && !"".equals(annotation.name())) {
            name = annotation.name();
        }
        return name;
    }

    /**
     * Return whether the value is a collection, or an array.
     *
     * @return
     */
    public static boolean isCollection(final AnnotatedElement element, final Object value) {
        final Value annotation = element.getAnnotation(Value.class);
        return value != null && annotation.collection() && (value instanceof Collection || value.getClass().isArray());
    }

    /**
     * Return whether the recurse attribute is set.
     *
     * @param element
     * @return
     */
    public static boolean doRecurse(final AnnotatedElement element) {
        final Value annotation = element.getAnnotation(Value.class);
        return annotation != null && annotation.recurse();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object applyAdapters(final ParsingDirection direction,
            final Map<String, ValueAdapter> adapters, final AnnotatedElement element, final Object value) {
        Object result = value;
        final List<ValueAdapter> adapterList = createAdapterList(adapters, element);
        // reverse list for unmarshaling
        if (direction == ParsingDirection.UNMARSHALING) {
            Collections.reverse(adapterList);
        }
        for (final ValueAdapter adapter : adapterList) {
            try {
                if (direction == ParsingDirection.MARSHALING) {
                    result = adapter.marshal(result);
                } else {
                    result = adapter.unmarshal(result);
                }
            } catch (final Exception e) {
                throw new AdapterException(e);
            }
        }
        return result;
    }

    public static void setValue(final Object instance, final AnnotatedElement element, final Object value) {
        if (element instanceof Field) {
            setFieldValue(instance, element, value);
        } else {
            setMethodValue(instance, element, value);
        }
    }

    private static void setMethodValue(final Object instance, final AnnotatedElement element, final Object value) {
        final Method setter = BeanUtils.getSetter((Method) element);
        try {
            setter.invoke(instance, value);
        } catch (final IllegalAccessException e) {
            throw new VaxException(e);
        } catch (final InvocationTargetException e) {
            throw new VaxException(e);
        }
    }

    private static  void setFieldValue(final Object instance, final AnnotatedElement element, final Object value) {
        final Field field = (Field) element;
        final boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (final IllegalArgumentException e) {
            throw new VaxException(e);
        } catch (final IllegalAccessException e) {
            throw new VaxException(e);
        }
        field.setAccessible(accessible);
    }

    static String addPrefix(final String prefix, final String string) {
        checkNotNull("prefix must not be null", prefix);
        checkNotNull("string must not be null", string);
        return prefix + string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Gathers and constructs the list of adapters.
     *
     * @param adapters
     * @param element
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static List<ValueAdapter> createAdapterList(
            final Map<String, ValueAdapter> adapters, final AnnotatedElement element) {
        List<ValueAdapter> adapterList = Collections.emptyList();
        final ValueJavaAdapter annotation = element.getAnnotation(ValueJavaAdapter.class);
        if (annotation != null) {
            adapterList = new ArrayList<ValueAdapter>(adapters.size());
            for (final Class<? extends ValueAdapter<?, ?>> clazz : annotation.value()) {
                if (adapters.containsKey(clazz.getName())) {
                    adapterList.add(adapters.get(clazz.getName()));
                } else {
                    try {
                        final ValueAdapter adapter = clazz.newInstance();
                        adapterList.add(adapter);
                    } catch (final InstantiationException e) {
                        throw new AdapterException(e);
                    } catch (final IllegalAccessException e) {
                        throw new AdapterException(e);
                    }
                }
            }
        }
        return adapterList;
    }

    public static <T> T getInstance(final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (final InstantiationException e) {
            throw new ValidationException(e);
        } catch (final IllegalAccessException e) {
            throw new ValidationException(e);
        }
    }
}
