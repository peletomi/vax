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
package org.pcosta.vax;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public interface ExceptionHandler {

    void handleKeyConflict(final Object key);

    void handleRequiredMissing(final Object key);

    void handleUnexpectedType(final String message);

    void handleFieldAccessException(final Exception e, final String fieldName);

    void handleAdapterException(final Exception e);

    void handleAdapterInstantiationException(final Exception e);

    void handleAnnotationOnIllegalField(String message);

}
