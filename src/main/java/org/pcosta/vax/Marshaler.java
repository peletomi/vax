package org.pcosta.vax;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.pcosta.vax.annotation.Value;
import org.pcosta.vax.impl.ParsingContext;
import org.pcosta.vax.impl.ParsingDirection;
import org.pcosta.vax.impl.util.BeanUtils;

import com.google.common.collect.ImmutableList;

public class Marshaler<Extracted, Factory extends FrontEndFactory<Extracted>> {

    private static final String UNEXP_TYPE_TEMPL = "expected collection or array but was [%s]";

    private final ExtractorFrontEnd<Extracted> frontEnd;

    private final ValueKeyGenerator keyGenerator;

    @SuppressWarnings("rawtypes")
    private final Map<String, ValueAdapter> adapters;

    private final List<String> violations = new ArrayList<String>();

    private final Queue<ParsingContext> parsingContext;

    private ParsingContext currentContext;

    private AnnotatedElement element;

    public Marshaler(final ExtractorFrontEnd<Extracted> frontEnd, final ValueKeyGenerator keyGenerator,
            @SuppressWarnings("rawtypes") final Map<String, ValueAdapter> adapters,
            final Queue<ParsingContext> parsingContext) {
        super();
        this.frontEnd = frontEnd;
        this.keyGenerator = keyGenerator;
        this.adapters = adapters;
        this.parsingContext = parsingContext;
    }

    public void marshal(final AnnotatedElement element, final ParsingContext currentContext) {
        clear();
        checkNotNull(element);
        checkNotNull(currentContext);
        this.element = element;
        this.currentContext = currentContext;
        marshal();
        clear();
    }

    public List<String> getViolations() {
        return violations;
    }

    private void marshal() {
        final String name = getName();
        final Object value = getValue(currentContext.getInstance());

        if (BeanUtils.isCollection(element, value)) {
            if (BeanUtils.doRecurse(element)) {
                addCollectionToParsingContext(name, value);
            } else {
                addCollectionToFrontEnd(name, value);
            }
        } else {
            final Object adaptedValue = BeanUtils.applyAdapters(ParsingDirection.MARSHALING, adapters, element, value);
            violations.addAll(validate(name, adaptedValue));
            if (BeanUtils.doRecurse(element)) {
                parsingContext.offer(new ParsingContext(currentContext, name, adaptedValue));
            } else {
                addToFrontEnd(name, adaptedValue);
            }
        }
    }

    private String getName() {
        String name;
        if (element instanceof Method) {
            final Method method = (Method)element;
            name = BeanUtils.getName(BeanUtils.getMethodName(method), method);
        } else {
            final Field field = (Field)element;
            name = BeanUtils.getName(field.getName(), field);
        }
        return name;
    }

    private Object getValue(final Object instance) {
        Object value;
        if (element instanceof Method) {
            final Method method = (Method)element;
            value = BeanUtils.getValue(instance, method);
        } else {
            final Field field = (Field)element;
            value = BeanUtils.getValue(instance, field);
        }
        return value;
    }

    private List<String> validate(final String name, final Object value) {
        List<String> result = Collections.emptyList();
        final Value annotation = element.getAnnotation(Value.class);
        if (annotation.required() && (value == null || "".equals(value.toString()))) {
            result = ImmutableList.copyOf(
                      new String[] {String.format("value [%s] required but not set in class [%s]",
                              name, currentContext.getInstance().getClass().getName()) });
        }
        return result;
    }

    private void addToFrontEnd(final String name, final Object value) {
        final List<String> keys = new ArrayList<String>(currentContext.getAncestorKeys());
        keys.add(name);
        frontEnd.addValue(keys.toArray(new String[keys.size()]), value);
    }

    private List<String> addCollectionToFrontEnd(final String name, final Object value) {
        final List<String> violations = new ArrayList<String>();
        final Collection<Object> instances = getCollectionFromValue(value);
        if (instances != null) {
            int i = 0;
            for (final Object instance : instances) {
                final Object adaptedValue = BeanUtils.applyAdapters(ParsingDirection.MARSHALING, adapters, element, instance);
                addToFrontEnd(keyGenerator.generateKey(name, i++), adaptedValue);
            }
        }
        return violations;
    }

    private void addCollectionToParsingContext(final String name, final Object value) {
        if (value == null) {
            return;
        }

        final Collection<Object> instances = getCollectionFromValue(value);
        int i = 0;
        for (final Object instance : instances) {
            parsingContext.offer(new ParsingContext(currentContext, keyGenerator.generateKey(name, i), instance, true, i));
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

    private void clear() {
        currentContext = null;
        element = null;
    }
}
