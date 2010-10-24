package org.peletomi.vax;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.peletomi.vax.impl.ParsingContext;
import org.peletomi.vax.impl.util.BeanUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class Unmarshaler<Extracted, Factory extends FrontEndFactory<Extracted>> extends
        AbstractMarshalUnmarshal<Extracted, Factory> {

    public Unmarshaler(final ExtractorFrontEnd<Extracted> frontEnd, final ValueKeyGenerator keyGenerator,
            @SuppressWarnings("rawtypes") final Map<String, ValueAdapter> adapters,
            final Queue<ParsingContext> parsingContext) {
        super(frontEnd, keyGenerator, adapters, parsingContext);
    }

    @Override
    protected void process() {
        final Class<?> type = getType();
        final Object value = getValue(type);
        violations.addAll(validate(getName(), value));
        if (value != null) {
            setValue(value);
            currentContext.incrementNonNullCount();
        }
    }

    public void finished(final ParsingContext parsingContext) {
        // if there was no value, then remove the current instance from the parent instance
        if (parsingContext.getNonNullCount() == 0 && parsingContext.getParentContext() != null) {
            BeanUtils.setValue(parsingContext.getParentContext().getInstance(), parsingContext.getParentElement(), null);
        }
    }

    private void setValue(final Object value) {
        BeanUtils.setValue(currentContext.getInstance(), element, value);
    }

    private Object getValue(final Class<?> type) {
        Object value;
        if (type.isArray()) {
            value = getArrayValue(type);
        } else if (Collection.class.isInstance(type)) {
            // TODO
            value = null;
        } else if (BeanUtils.doRecurse(element)) {
            value = BeanUtils.getInstance(type);
            final ParsingContext context = new ParsingContext(currentContext, element, getName(), value);
            parsingContext.add(context);
        } else {
            value = getSingleValue(type, frontEnd.get(getKey(currentContext.getAncestorKeys(), getName())));
        }
        return value;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object getSingleValue(final Class<?> type, final Object object) {
        Object value = null;
        if (object != null) {
            if (object.getClass().isArray()) {
                // TODO get the first
                // stuff the rest back
                value = null;
            } else if (Collection.class.isInstance(object)) {
                // get the first
                final List list = Lists.newArrayList((Collection) object);
                if (list.isEmpty()) {
                    value = null;
                } else {
                    value = list.remove(0);
                }
                // TODO stuff the rest back
            } else {
                value = object;
                ;
            }
            value = convertValue(type, value);
        }
        return value;
    }

    private Object convertValue(final Class<?> type, final Object value) {
        Object result = null;
        if (value != null) {
            if (Integer.class.isAssignableFrom(type)) {
                result = Integer.parseInt(value.toString());
            } else if (Long.class.isAssignableFrom(type)) {
                result = Long.parseLong(value.toString());
            } else if (Double.class.isAssignableFrom(type)) {
                result = Double.parseDouble(value.toString());
            } else if (Float.class.isAssignableFrom(type)) {
                result = Float.parseFloat(value.toString());
            } else if (Byte.class.isAssignableFrom(type)) {
                result = Byte.parseByte(value.toString());
            } else if (Short.class.isAssignableFrom(type)) {
                result = Short.parseShort(value.toString());
            } else if (Character.class.isAssignableFrom(type)) {
                result = value.toString().charAt(0);
            } else {
                result = value.toString();
            }
        }
        return result;
    }

    private String[] getKey(final ImmutableList<String> ancestorKeys, final String name) {
        final String[] keys = new String[ancestorKeys.size() + 1];
        for (int i = 0; i < ancestorKeys.size(); i++) {
            keys[i] = ancestorKeys.get(i);
        }
        keys[keys.length - 1] = name;
        return keys;
    }

    private Object getArrayValue(final Class<?> type) {
        // TODO Auto-generated method stub
        return null;
    }

    private Class<?> getType() {
        Class<?> type;
        if (element instanceof Field) {
            type = ((Field) element).getType();
        } else {
            final Method getter = BeanUtils.getGetter((Method) element);
            type = getter.getReturnType();
        }
        return type;
    }
}
