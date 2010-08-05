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
        return new MethodFilterIterator(new MethodIterator(this.instance));
    }

}
