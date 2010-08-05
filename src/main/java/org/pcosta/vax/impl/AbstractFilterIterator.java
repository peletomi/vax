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

import java.util.Iterator;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public abstract class AbstractFilterIterator<Element> implements Iterator<Element> {

    private final Iterator<Element> iterator;

    private Element next;

    public AbstractFilterIterator(final Iterator<Element> iterator) {
        super();
        this.iterator = iterator;
        this.next = this.getNext();
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public Element next() {
        final Element result = this.next;
        this.next = this.getNext();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Element getNext() {
        Element result = null;
        while (this.iterator.hasNext()) {
            final Element element = this.iterator.next();
            if (this.accept(element)) {
                result = element;
                break;
            }
        }
        return result;
    }

    public abstract boolean accept(final Element element);

}
