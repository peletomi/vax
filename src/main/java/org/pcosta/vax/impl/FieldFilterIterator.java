package org.pcosta.vax.impl;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.pcosta.vax.annotation.Value;

public class FieldFilterIterator extends AbstractFilterIterator<Field> {

    public FieldFilterIterator(final Iterator<Field> iterator) {
        super(iterator);
    }

    @Override
    public boolean accept(final Field field) {
        return field.getAnnotation(Value.class) != null;
    }

}
