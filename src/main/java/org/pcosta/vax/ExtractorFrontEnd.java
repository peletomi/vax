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
public interface ExtractorFrontEnd<Extracted> {

    /**
     * Initializes the internal container.
     */
    void init();

    /**
     * Initializes the internal container.
     */
    void init(Extracted values);

    /**
     * Adds a key value pair to the container.
     *
     * @param key
     * @param value
     */
    void addValue(String[] key, Object value);

    /**
     * True if the key is already set, false otherwise.
     *
     * @param key
     */
    boolean contains(String[] key);

    /**
     * Returns the extracted value;
     *
     * @param keys
     * @return
     */
    Object get(String[] keys);

    /**
     * Returns the extracted values stored in the internal container.
     *
     * @return
     */
    Extracted getExtracted();

}
