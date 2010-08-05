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

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pcosta.vax.testobject.Person;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class StringArrayExtractorTest {

    private static final String FIRST_NAME = "John";

    private static final String LAST_NAME = "Doe";

    private final StringArrayExtractor extractor = new StringArrayExtractor();

    private Person person;

    @Before
    public void setUp() throws Exception {
        this.person = new Person();
        this.person.setFirstName(FIRST_NAME);
        this.person.setLastName(LAST_NAME);
    }

    @Test
    public void testNull() throws Exception {
        final Map<String, String[]> values = this.extractor.marshal(null);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testNotAnnotated() throws Exception {
        final Map<String, String[]> values = this.extractor.marshal("");
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void testMarshalPerson() throws Exception {
        final Map<String, String[]> values = this.extractor.marshal(this.person);
        Assert.assertTrue(!values.isEmpty());
    }
}
