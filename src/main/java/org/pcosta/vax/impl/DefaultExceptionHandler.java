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

    @Override
    public void handleRequiredMissing(final Object key) {
        throw new IllegalArgumentException(String.format("value [%s] is not set but is required", key));
    }

    @Override
    public void handleUnexpectedType(final String message) {
        throw new IllegalArgumentException(message);
    }

    @Override
    public void handleFieldAccessException(final Exception e, final String fieldName) {
        throw new IllegalStateException(String.format("could not get value from field [%s]", fieldName), e);
    }

    @Override
    public void handleAdapterException(final Exception e) {
        throw new IllegalArgumentException(e);
    }

    @Override
    public void handleAdapterInstantiationException(final Exception e) {
        throw new IllegalArgumentException(e);
    }

    @Override
    public void handleAnnotationOnIllegalField(final String message) {
        throw new IllegalArgumentException(message);
    }

}
