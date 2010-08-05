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

import java.lang.reflect.Method;
import java.util.Iterator;

import org.pcosta.vax.annotation.Value;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class MethodFilterIterator extends AbstractFilterIterator<Method> {

    private static final String GET = "get";

    private static final String IS = "is";

    private static final String SET = "set";

    public MethodFilterIterator(final Iterator<Method> iterator) {
        super(iterator);
    }

    @Override
    public boolean accept(final Method method) {
        final String name = method.getName();
        return method.getAnnotation(Value.class) != null
                && (name.startsWith(GET) || name.startsWith(IS) || name.startsWith(SET));
    }
}
