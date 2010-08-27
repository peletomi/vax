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

import org.pcosta.vax.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class DefaultExceptionHandler implements ExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public void handleKeyConflict(final Object key) {
        LOG.warn("key [{}] is already present", key);
    }

}
