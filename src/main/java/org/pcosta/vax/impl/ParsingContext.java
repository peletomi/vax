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

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class ParsingContext {

    private final String[] ancestorKeys;

    private final Object instance;

    private final boolean isPartOfCollection;

    private final int position;

    public ParsingContext(final Object instance) {
        this.instance = instance;
        this.ancestorKeys = new String[0];
        this.isPartOfCollection = false;
        this.position = -1;
    }

    public ParsingContext(final String[] ancestorKeys, final Object instance) {
        this.instance = instance;
        this.ancestorKeys = ancestorKeys;
        this.isPartOfCollection = false;
        this.position = -1;
    }

    public ParsingContext(final String[] ancestorKeys, final Object instance, final boolean isPartOfCollection,
            final int position) {
        super();
        this.instance = instance;
        this.ancestorKeys = ancestorKeys;
        this.isPartOfCollection = isPartOfCollection;
        this.position = position;
    }

    /**
     * Returns the keys of the field ancestors.
     *
     * @return
     */
    public String[] getAncestorKeys() {
        return this.ancestorKeys;
    }

    /**
     * Returns the instance under inspection;
     *
     * @return
     */
    public Object getInstance() {
        return this.instance;
    }

    /**
     * Shows whether the instance was part of a collection.
     *
     * @return
     */
    public boolean isPartOfCollection() {
        return this.isPartOfCollection;
    }

    /**
     * Returns the position of the instance in the collection. If it was not
     * part of a collection it returns -1.
     *
     * @return
     */
    public int getPosition() {
        return this.position;
    }
}
