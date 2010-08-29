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

import static com.google.common.base.Predicates.and;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.peletomi.vax.impl.util.BeanUtils;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class IterableMethods implements Iterable<Method> {

    private final Object instance;

    public IterableMethods(final Object instance) {
        super();
        this.instance = instance;
    }

    @Override
    public Iterator<Method> iterator() {
        return Iterators.filter(new MethodIterator(instance),
                and(BeanUtils.IS_VALUE, Predicates.or(BeanUtils.IS_GETTER, BeanUtils.IS_SETTER)));
    }

}
