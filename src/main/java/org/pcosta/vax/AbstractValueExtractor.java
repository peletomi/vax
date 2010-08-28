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
import org.pcosta.vax.impl.exception.AdapterException;
import org.pcosta.vax.impl.exception.ValidationException;
import org.pcosta.vax.impl.util.BeanUtils;

import com.google.common.collect.ImmutableList;

// TODO ordering of parameters
/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public abstract class AbstractValueExtractor<Extracted, Factory extends FrontEndFactory<Extracted>> {

    private static final String UNEXP_TYPE_TEMPL = "expected collection or array but was [%s]";

    private final Factory frontEndFactory;

    public AbstractValueExtractor(final Factory frontEndFactory) {
        super();
        this.frontEndFactory = frontEndFactory;
    }

    private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

    private ValueKeyGenerator valueKeyGenerator = new DefaultValueKeyGenerator();

    @SuppressWarnings("rawtypes")
    private Map<String, ValueAdapter> valueAdapters = Collections.emptyMap();

    /**
     *
     * @param instance
     * @throws {@link IllegalArgumentException}
     * @throws {@link VaxException}
     * @throws {@link AdapterException}
     * @return
     */
    public Extracted marshal(final Object instance) {

        @SuppressWarnings("rawtypes")
        final Map<String, ValueAdapter> adapters = this.getValueAdapterMap();
        final ValueKeyGenerator keyGenerator = this.getValueKeyGenerator();

        final ExtractorFrontEnd<Extracted> frontEnd = this.frontEndFactory.create();
        frontEnd.init();

        final List<String> violations = new ArrayList<String>();
        final Queue<ParsingContext> parsingContext = new ConcurrentLinkedQueue<ParsingContext>();
        parsingContext.offer(new ParsingContext(instance));
        while (!parsingContext.isEmpty()) {
            final ParsingContext context = parsingContext.poll();
            for (final Field field : new IterableFields(context.getInstance())) {
                violations.addAll(this.handleMarshaling(keyGenerator, adapters, context, parsingContext, frontEnd, field,
                        this.getName(field), this.getValue(context.getInstance(), field)));
            }
            for (final Method method : new IterableMethods(context.getInstance())) {
                violations.addAll(this.handleMarshaling(keyGenerator, adapters, context, parsingContext, frontEnd, method,
                        this.getName(method), this.getValue(context.getInstance(), method)));
            }
        }

        if (!violations.isEmpty()) {
            final ValidationException e = new ValidationException();
            e.setViolations(violations);
            throw e;
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
    public synchronized void setValueAdapters(final Collection<? extends ValueAdapter> valueAdapters) {
        if (valueAdapters != null) {
            final Map<String, ValueAdapter> result = new HashMap<String, ValueAdapter>(valueAdapters.size());
            for (final ValueAdapter adapter : valueAdapters) {
                result.put(adapter.getClass().getName(), adapter);
            }
            this.valueAdapters = result;
        }
    }

    private final List<String> handleMarshaling(final ValueKeyGenerator keyGenerator,
            @SuppressWarnings("rawtypes") final Map<String, ValueAdapter> adapters,
            final ParsingContext currentContext, final Queue<ParsingContext> parsingContext,
            final ExtractorFrontEnd<Extracted> frontEnd, final AnnotatedElement element, final String name,
            final Object value) {

        final List<String> violations = new ArrayList<String>();

        if (violations.isEmpty()) {
            if (isCollection(element, value)) {
                if (this.doRecurse(element)) {
                    this.addToParsingContext(keyGenerator, name, currentContext, parsingContext, value);
                } else {
                    violations.addAll(this.addToFrontEnd(keyGenerator, adapters, frontEnd, element, currentContext, name, value));
                }
            } else {
                final Object adaptedValue = this.applyAdapters(ParsingDirection.MARSHALING, adapters, element, value);
                violations.addAll(this.validate(currentContext, name, element, adaptedValue));
                if (this.doRecurse(element)) {
                    parsingContext
                    .offer(new ParsingContext(this.merge(currentContext.getAncestorKeys(), name), adaptedValue));
                } else {
                    frontEnd.addValue(this.merge(currentContext.getAncestorKeys(), name), adaptedValue);
                }
            }
        }

        return violations;
    }

    private List<String> addToFrontEnd(
            final ValueKeyGenerator keyGenerator,
            @SuppressWarnings("rawtypes") final Map<String, ValueAdapter> adapters,
            final ExtractorFrontEnd<Extracted> frontEnd, final AnnotatedElement element,
            final ParsingContext currentContext, final String name, final Object value) {

        final List<String> violations = new ArrayList<String>();
        final Collection<Object> instances = getCollectionFromValue(value);
        if (instances != null) {
            int i = 0;
            for (final Object instance : instances) {
                final Object adaptedValue = this.applyAdapters(ParsingDirection.MARSHALING, adapters, element, instance);
                violations.addAll(this.validate(currentContext, name, element, adaptedValue));
                frontEnd.addValue(this.merge(currentContext.getAncestorKeys(), keyGenerator.generateKey(name, i)), adaptedValue);
                ++i;
            }
        }
        return violations;
    }

    private void addToParsingContext(final ValueKeyGenerator keyGenerator, final String name,
            final ParsingContext currentContext, final Queue<ParsingContext> parsingContext, final Object value) {
        if (value == null) {
            return;
        }

        final Collection<Object> instances = getCollectionFromValue(value);
        int i = 0;
        for (final Object instance : instances) {
            parsingContext
                    .offer(new ParsingContext(this.merge(currentContext.getAncestorKeys(), keyGenerator.generateKey(name, i)),
                            instance, true, i));
            ++i;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Collection<Object> getCollectionFromValue(final Object value) {
        Collection<Object> instances = null;
        if (value.getClass().isArray()) {
            instances = Arrays.asList((Object[])value);
        } else if (value instanceof Collection) {
            instances = (Collection) value;
        } else {
            throw new IllegalArgumentException(String.format(UNEXP_TYPE_TEMPL, value.getClass().getName()));
        }
        return instances;
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
    private boolean isCollection(final AnnotatedElement element, final Object value) {
        final Value annotation = element.getAnnotation(Value.class);
        return value != null && annotation.collection() && (value instanceof Collection || value.getClass().isArray());
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

    private String getName(final Field field) {
        return this.getName(field.getName(), field);
    }

    private String getName(final Method method) {
        return this.getName(BeanUtils.getMethodName(method), method);
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

    private List<String> validate(final ParsingContext parsingContext, final String name,
            final AnnotatedElement element, final Object value) {
        List<String> result = Collections.emptyList();
        final Value annotation = element.getAnnotation(Value.class);
        if (annotation.required() && (value == null || "".equals(value.toString()))) {
            result = ImmutableList.copyOf(
                      new String[] {String.format("value [%s] required but not set in class [%s]",
                              name, parsingContext.getInstance().getClass().getName()) });
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object applyAdapters(final ParsingDirection direction,
            final Map<String, ValueAdapter> adapters, final AnnotatedElement element, final Object value) {
        Object result = value;
        final List<ValueAdapter> adapterList = this.createAdapterList(adapters, element);
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

    /**
     * Gathers and constructs the list of adapters.
     *
     * @param adapters
     * @param element
     * @return
     */
    @SuppressWarnings("rawtypes")
    private List<ValueAdapter> createAdapterList(
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

    private Object getValue(final Object instance, final Field field) {
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

    private Object getValue(final Object instance, final Method method) {
        Object result = null;
        try {
            final Method getter = this.getGetterMethod(method);
            result = getter.invoke(instance);
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (final InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

    private Method getGetterMethod(final Method method) {
        final Method result = BeanUtils.getGetter(method);
        if (result == null) {
            throw new IllegalStateException(String.format("coul not find getter for method [%s]", method.getName()));
        }
        return method;
    }

    private Method getSetterMethod(final Method method) {
        final Method result = BeanUtils.getSetter(method);
        if (result == null) {
            throw new IllegalStateException(String.format("coul not find setter for method [%s]", method.getName()));
        }
        return method;
    }

}
