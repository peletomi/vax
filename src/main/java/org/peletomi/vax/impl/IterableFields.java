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
package org.peletomi.vax.impl;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.peletomi.vax.impl.util.BeanUtils;

import com.google.common.collect.Iterators;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class IterableFields implements Iterable<Field> {

    private final Object instance;

    public IterableFields(final Object instance) {
        super();
        this.instance = instance;
    }

    @Override
    public Iterator<Field> iterator() {

        return Iterators.filter(new FieldIterator(instance), BeanUtils.IS_VALUE);
    }

}
