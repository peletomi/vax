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

import java.lang.reflect.Field;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class FieldIterator extends AbstractAttributeIterator<Field> {

    public FieldIterator(final Object instance) {
        super(instance);
    }

    @Override
    protected Field[] getAttributes() {
        return this.clazz.getDeclaredFields();
    }

}
