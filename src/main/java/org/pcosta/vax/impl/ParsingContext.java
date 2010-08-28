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

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class ParsingContext {

    private final ImmutableList<String> ancestorKeys;

    private final Object instance;

    private final boolean isPartOfCollection;

    private final int position;

    public ParsingContext(final Object instance) {
        this.instance = instance;
        ancestorKeys = ImmutableList.of();
        isPartOfCollection = false;
        position = -1;
    }

    public ParsingContext(final List<String> ancestorKeys, final Object instance) {
        this.instance = instance;
        this.ancestorKeys = ImmutableList.copyOf(ancestorKeys);
        isPartOfCollection = false;
        position = -1;
    }

    public ParsingContext(final List<String> ancestorKeys, final Object instance, final boolean isPartOfCollection,
            final int position) {
        super();
        this.instance = instance;
        this.ancestorKeys = ImmutableList.copyOf(ancestorKeys);
        this.isPartOfCollection = isPartOfCollection;
        this.position = position;
    }

    public ParsingContext(
            final ParsingContext oldContext, final String key, final Object instance, final boolean isPartOfCollection,
            final int position) {
        super();
        this.instance = instance;
        final List<String> keys = Lists.newArrayList(oldContext.getAncestorKeys());
        keys.add(key);
        ancestorKeys = ImmutableList.copyOf(keys);
        this.isPartOfCollection = isPartOfCollection;
        this.position = position;
    }

    public ParsingContext(
            final ParsingContext oldContext, final String key, final Object instance) {
        super();
        this.instance = instance;
        final List<String> keys = Lists.newArrayList(oldContext.getAncestorKeys());
        keys.add(key);
        ancestorKeys = ImmutableList.copyOf(keys);
        isPartOfCollection = false;
        position = -1;
    }

    /**
     * Returns the keys of the field ancestors.
     *
     * @return
     */
    public ImmutableList<String> getAncestorKeys() {
        return ancestorKeys;
    }

    /**
     * Returns the instance under inspection;
     *
     * @return
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * Shows whether the instance was part of a collection.
     *
     * @return
     */
    public boolean isPartOfCollection() {
        return isPartOfCollection;
    }

    /**
     * Returns the position of the instance in the collection. If it was not
     * part of a collection it returns -1.
     *
     * @return
     */
    public int getPosition() {
        return position;
    }
}
