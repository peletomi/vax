/**
 * Licensed under MPL / LGPL dual-license.
 *
 * Copyright (c) 2010 Tamas Eppel <Tamas.Eppel@gmail.com>
 *
 * You should have received a copy of the licenses
 * along with this program.
 * If not, see:
 *
 *    <http://www.gnu.org/licenses/>
 *    <http://www.mozilla.org/MPL/MPL-1.1.html>
 */
package org.pcosta.vax;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.pcosta.vax.annotation.Value;
import org.pcosta.vax.annotation.ValueJavaAdapter;
import org.pcosta.vax.impl.DefaultExceptionHandler;
import org.pcosta.vax.impl.DefaultValueKeyGenerator;
import org.pcosta.vax.impl.IterableFields;
import org.pcosta.vax.impl.IterableMethods;
import org.pcosta.vax.impl.ParsingContext;
import org.pcosta.vax.impl.ParsingDirection;

// TODO ordering of parameters
/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public abstract class AbstractValueExtractor<Extracted, Factory extends FrontEndFactory<Extracted>> {

    private static final String UNEXP_TYPE_TEMPL = "expected collection or array but was [%s]";

    private static final String NOT_GETTER_TEMPL = "Value annotation must be on a field, getter or a setter method, annotatied [%s]";

    private static final String GET = "get";

    private static final String IS = "is";

    private static final String SET = "set";

    private final Factory frontEndFactory;

    public AbstractValueExtractor(final Factory frontEndFactory) {
        super();
        this.frontEndFactory = frontEndFactory;
    }

    private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

    private ValueKeyGenerator valueKeyGenerator = new DefaultValueKeyGenerator();

    @SuppressWarnings("rawtypes")
    private Map<String, ValueAdapter> valueAdapters = Collections.emptyMap();

    public Extracted marshal(final Object instance) {

        @SuppressWarnings("rawtypes")
        final Map<String, ValueAdapter> adapters = this.getValueAdapterMap();
        final ExceptionHandler exHandler = this.getExceptionHandler();
        final ValueKeyGenerator keyGenerator = this.getValueKeyGenerator();

        final ExtractorFrontEnd<Extracted> frontEnd = this.frontEndFactory.create();
        frontEnd.init();

        final Queue<ParsingContext> parsingContext = new ConcurrentLinkedQueue<ParsingContext>();
        parsingContext.offer(new ParsingContext(instance));
        while (!parsingContext.isEmpty()) {
            final ParsingContext context = parsingContext.poll();
            for (final Field field : new IterableFields(context.getInstance())) {
                this.handleMarshaling(exHandler, keyGenerator, adapters, context, parsingContext, frontEnd, field,
                        this.getName(exHandler, field), this.getValue(exHandler, context.getInstance(), field));
            }
            for (final Method method : new IterableMethods(context.getInstance())) {
                this.handleMarshaling(exHandler, keyGenerator, adapters, context, parsingContext, frontEnd, method,
                        this.getName(exHandler, method), this.getValue(exHandler, context.getInstance(), method));
            }
        }

        return frontEnd.getExtracted();
    }

    public synchronized ExceptionHandler getExceptionHandler() {
        return this.exceptionHandler;
    }

    public synchronized void setExceptionHandler(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public synchronized ValueKeyGenerator getValueKeyGenerator() {
        return this.valueKeyGenerator;
    }

    public synchronized void setValueKeyGenerator(final ValueKeyGenerator valueKeyGenerator) {
        this.valueKeyGenerator = valueKeyGenerator;
    }

    @SuppressWarnings("rawtypes")
    private synchronized Map<String, ValueAdapter> getValueAdapterMap() {
        return this.valueAdapters;
    }

    @SuppressWarnings("rawtypes")
    public synchronized Collection<ValueAdapter> getValueAdapters() {
        return this.valueAdapters.values();
    }

    @SuppressWarnings("rawtypes")
    public synchronized void setValueAdapters(final Collection<ValueAdapter> valueAdapters) {
        if (valueAdapters != null) {
            final Map<String, ValueAdapter> result = new HashMap<String, ValueAdapter>(valueAdapters.size());
            for (final ValueAdapter adapter : valueAdapters) {
                result.put(adapter.getClass().getName(), adapter);
            }
            this.valueAdapters = result;
        }
    }

    private void handleMarshaling(final ExceptionHandler exHandler, final ValueKeyGenerator keyGenerator,
            @SuppressWarnings("rawtypes") final Map<String, ValueAdapter> adapters,
            final ParsingContext currentContext, final Queue<ParsingContext> parsingContext,
            final ExtractorFrontEnd<Extracted> frontEnd, final AnnotatedElement element, final String name,
            final Object value) {
        final Object adaptedValue = this.applyAdapters(exHandler, ParsingDirection.MARSHALING, adapters, element, value);
        this.validate(element, adaptedValue);
        if (this.doRecurse(element)) {
            if (isCollection(adaptedValue)) {
                this.addToParsingContext(exHandler, name, currentContext, parsingContext, adaptedValue);
            } else {
                parsingContext
                .offer(new ParsingContext(this.merge(currentContext.getAncestorKeys(), name), adaptedValue));
            }
        } else {
            frontEnd.addValue(this.merge(currentContext.getAncestorKeys(), name), adaptedValue);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addToParsingContext(final ExceptionHandler exHandler, final String name,
            final ParsingContext currentContext, final Queue<ParsingContext> parsingContext, final Object value) {
        Collection<Object> instances = null;
        if (value.getClass().isArray()) {
            instances = Arrays.asList(value);
        } else if (value instanceof Collection) {
            instances = (Collection) value;
        } else {
            exHandler.handleUnexpectedType(String.format(UNEXP_TYPE_TEMPL, value.getClass().getName()));
        }
        if (instances != null) {
            int i = 0;
            for (final Object instance : instances) {
                parsingContext
                        .offer(new ParsingContext(this.merge(currentContext.getAncestorKeys(), name), instance, true, i));
                ++i;
            }
        }
    }

    private String[] merge(final String[] source, final String string) {
        final String[] result = Arrays.copyOf(source, source.length + 1);
        result[source.length] = string;
        return result;
    }

    /**
     * Return whether the value is a collection, or an array.
     *
     * @return
     */
    private boolean isCollection(final Object value) {
        return value instanceof Collection || value.getClass().isArray();
    }

    /**
     * Return whether the recurse attribute is set.
     *
     * @param element
     * @return
     */
    private boolean doRecurse(final AnnotatedElement element) {
        final Value annotation = element.getAnnotation(Value.class);
        return annotation != null && annotation.recurse();
    }

    private String getName(final ExceptionHandler exHandler, final Field field) {
        return this.getName(field.getName(), field);
    }

    private String getName(final ExceptionHandler exHandler, final Method method) {
        return this.getName(this.getMethodName(exHandler, method), method);
    }

    /**
     * Returns the name of the element from the annotation. If not set it
     * returns the default.
     *
     * @param defaultName
     * @param element
     * @return
     */
    private String getName(final String defaultName, final AnnotatedElement element) {
        String name = defaultName;
        final Value annotation = element.getAnnotation(Value.class);
        if (annotation != null && !"".equals(annotation.name())) {
            name = annotation.name();
        }
        return name;
    }

    private void validate(final AnnotatedElement element, final Object value) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object applyAdapters(final ExceptionHandler exHandler, final ParsingDirection direction,
            final Map<String, ValueAdapter> adapters, final AnnotatedElement element, final Object value) {
        Object result = value;
        final List<ValueAdapter> adapterList = this.createAdapterList(exHandler, adapters, element);
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
                exHandler.handleAdapterException(e);
            }
        }
        return result;
    }

    /**
     * Gathers and constructs the list of adapters.
     *
     * @param exHandler
     * @param adapters
     * @param element
     * @return
     */
    @SuppressWarnings("rawtypes")
    private List<ValueAdapter> createAdapterList(final ExceptionHandler exHandler,
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
                        exHandler.handleAdapterInstantiationException(e);
                    } catch (final IllegalAccessException e) {
                        exHandler.handleAdapterInstantiationException(e);
                    }
                }
            }
        }
        return adapterList;
    }

    private Object getValue(final ExceptionHandler exHandler, final Object instance, final Field field) {
        Object result = null;
        final boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            result = field.get(instance);
        } catch (final IllegalArgumentException e) {
            exHandler.handleFieldAccessException(e, field.getName());
        } catch (final IllegalAccessException e) {
            exHandler.handleFieldAccessException(e, field.getName());
        }
        field.setAccessible(accessible);
        return result;
    }

    private Object getValue(final ExceptionHandler exHandler, final Object instance, final Method method) {
        Object result = null;
        try {
            final Method getter = this.getGetterMethod(exHandler, method);
            result = getter.invoke(instance);
        } catch (final IllegalArgumentException e) {
            exHandler.handleFieldAccessException(e, method.getName());
        } catch (final IllegalAccessException e) {
            exHandler.handleFieldAccessException(e, method.getName());
        } catch (final InvocationTargetException e) {
            exHandler.handleFieldAccessException(e, method.getName());
        }
        return result;
    }

    private Method getGetterMethod(final ExceptionHandler exHandler, final Method method) {
        // TODO
        return method;
    }

    private Method getSetterMethod(final ExceptionHandler exHandler, final Method method) {
        // TODO
        return method;
    }

    private String getMethodName(final ExceptionHandler exHandler, final Method method) {
        String result = null;
        if (method.getName().startsWith(GET) || method.getName().startsWith(SET)) {
            result = method.getName().substring(3);
        } else if (method.getName().startsWith(IS)) {
            result = method.getName().substring(2);
        } else {
            exHandler.handleAnnotationOnIllegalField(String.format(NOT_GETTER_TEMPL, method.getName()));
        }
        if (result != null) {
            result = result.substring(0, 1).toLowerCase() + result.substring(1);
        }
        return result;
    }
}
