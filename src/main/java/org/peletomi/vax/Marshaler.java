package org.peletomi.vax;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.peletomi.vax.impl.ParsingContext;
import org.peletomi.vax.impl.ParsingDirection;
import org.peletomi.vax.impl.util.BeanUtils;


public final class Marshaler<Extracted, Factory extends FrontEndFactory<Extracted>>
    extends AbstractMarshalUnmarshal<Extracted, Factory> {

    private static final String UNEXP_TYPE_TEMPL = "expected collection or array but was [%s]";

    public Marshaler(final ExtractorFrontEnd<Extracted> frontEnd, final ValueKeyGenerator keyGenerator,
            @SuppressWarnings("rawtypes") final Map<String, ValueAdapter> adapters, final Queue<ParsingContext> parsingContext) {
        super(frontEnd, keyGenerator, adapters, parsingContext);
    }

    @Override
    protected void process() {
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
                parsingContext.offer(new ParsingContext(currentContext, element, name, adaptedValue));
            } else {
                addToFrontEnd(name, adaptedValue);
            }
        }
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
            parsingContext.offer(new ParsingContext(currentContext, element, keyGenerator.generateKey(name, i), instance, true, i));
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

}
