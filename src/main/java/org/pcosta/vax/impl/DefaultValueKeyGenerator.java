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

import org.pcosta.vax.ValueKeyGenerator;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class DefaultValueKeyGenerator implements ValueKeyGenerator {

    @Override
    public String generateKey(final String key, final int position) {
        return key;
    }

}
