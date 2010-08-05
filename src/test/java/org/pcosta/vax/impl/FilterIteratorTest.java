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

import static org.hamcrest.core.Is.is;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pcosta.vax.testobject.Employee;
/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class FilterIteratorTest {

    @Test
    public void testNull() throws Exception {
        final FieldFilterIterator iterator = new FieldFilterIterator(new FieldIterator(null));
        Assert.assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void testNoAnnotations() throws Exception {
        final FieldFilterIterator iterator = new FieldFilterIterator(new FieldIterator(""));
        Assert.assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void testFields() throws Exception {
        final List<Field> fields = new ArrayList<Field>();
        for (final Field field : new IterableFields(new Employee())) {
            fields.add(field);
        }
        Assert.assertThat(fields.size(), is(2));
    }

    @Test
    public void testMethods() throws Exception {
        final List<Method> fields = new ArrayList<Method>();
        for (final Method field : new IterableMethods(new Employee())) {
            fields.add(field);
        }
        Assert.assertThat(fields.size(), is(3));
    }
}
