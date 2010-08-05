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

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public final class Util {

    private static final String NULL_VALUE = "null";

    private Util() {

    }

    public static String join(final String[] values, final String separator) {
        return join(Arrays.asList(values), separator);
    }

    public static String join(final Object[] values, final String separator) {
        return join(Arrays.asList(values), separator);
    }

    public static String join(final Collection<?> values, final String separator) {
        String result = null;
        if (values != null) {
            final StringBuilder builder = new StringBuilder();
            for (final Object value : values) {
                String string = NULL_VALUE;
                if (value != null) {
                    string = value.toString();
                }
                builder.append(string).append(separator);
            }
            if (!values.isEmpty()) {
                builder.deleteCharAt(builder.length() - 1);
            }
            result = builder.toString();
        }
        return result;
    }

}
