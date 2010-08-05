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
package org.pcosta.vax.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public abstract class AbstractAttributeIterator<Attribute> implements Iterator<Attribute> {

    protected Class<?> clazz;

    private final List<Attribute> fields;

    public AbstractAttributeIterator(final Object instance) {
        super();
        if (instance == null) {
            this.fields = Collections.emptyList();
        } else {
            this.clazz = instance.getClass();
            this.fields = new ArrayList<Attribute>(Arrays.asList(this.getAttributes()));
        }
    }

    @Override
    public boolean hasNext() {
        return !(this.clazz == null || Object.class.equals(this.clazz) && this.fields.isEmpty());
    }

    @Override
    public Attribute next() {
        final Attribute result = this.fields.remove(0);
        if (this.clazz != null && this.fields.isEmpty() && !Object.class.equals(this.clazz)) {
            this.clazz = this.clazz.getSuperclass();
            this.fields.addAll(Arrays.asList(this.getAttributes()));
        }
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected abstract Attribute[] getAttributes();

}
