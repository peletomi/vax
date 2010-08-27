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

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.pcosta.vax.impl.exception.ValidationException;
import org.pcosta.vax.testobject.Address;
import org.pcosta.vax.testobject.CountryCode;
import org.pcosta.vax.testobject.Customer;
import org.pcosta.vax.testobject.Person;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class StringArrayExtractorTest {

    private static final String FIRST_NAME = "John";

    private static final String FIRST_NAME_KEY = "firstName";

    private static final String LAST_NAME = "Doe";

    private static final String LAST_NAME_KEY = "lastName";

    private static final String CITY = "Miskolc";

    private static final String CITY_KEY = "city";

    private static final String STREET = "Kossuth u. 6";

    private static final String STREET_KEY = "streetWithNumber";

    private static final int ZIP = 12345;

    private static final String ZIP_KEY = "zip";

    private static final String COUNTRY_CODE = "hu";

    private static final String COUNTRY_CODE_KEY = "countryCode";

    private static final Integer AGE = 28;

    private static final String AGE_KEY = "age";

    private static final String NICK_NAME = "johnny";

    private static final String NICK_NAME_KEY = "nickName";

    private final StringArrayExtractor extractor = new StringArrayExtractor();

    private Person person;

    private Address address;

    @Before
    public void setUp() throws Exception {
        person = new Person();
        person.setFirstName(FIRST_NAME);
        person.setLastName(LAST_NAME);
        person.setAge(AGE);

        address = new Address();
        address.setCity(CITY);
        address.setCountry(CountryCode.HU);
        address.setStreet(STREET);
        address.setZip(ZIP);
    }

    @Test
    public void testNull() throws Exception {
        final Map<String, String[]> values = extractor.marshal(null);
        assertThat(values.isEmpty(), is(true));
    }

    @Test
    public void testNotAnnotated() throws Exception {
        final Map<String, String[]> values = extractor.marshal("");
        assertThat(values.isEmpty(), is(true));
    }

    @Test
    public void testMarshalPerson() throws Exception {
        final Map<String, String[]> values = extractor.marshal(person);
        assertThat(values.isEmpty(), is(false));
        assertThat(values, hasEntry(FIRST_NAME_KEY, new String[] { FIRST_NAME }));
        assertThat(values, hasEntry(LAST_NAME_KEY, new String[] { LAST_NAME }));
        assertThat(values, hasEntry(AGE_KEY, new String[] { AGE.toString() }));
    }

    @Test
    public void testOptional() throws Exception {
        person.setNickName(NICK_NAME);
        final Map<String, String[]> values = extractor.marshal(person);
        assertThat(values.isEmpty(), is(false));
        assertThat(values, hasEntry(FIRST_NAME_KEY, new String[] { FIRST_NAME }));
        assertThat(values, hasEntry(LAST_NAME_KEY, new String[] { LAST_NAME }));
        assertThat(values, hasEntry(AGE_KEY, new String[] { AGE.toString() }));
        assertThat(values, hasEntry(NICK_NAME_KEY, new String[] { NICK_NAME }));
    }

    @Test
    public void testOptionalRecurse() throws Exception {
        Assert.fail("TODO");
    }

    @Test
    public void testRecurseWithAdapters() throws Exception {
        final Customer customer = new Customer();
        customer.setPerson(person);
        customer.setAddress(address);

        final Map<String, String[]> values = extractor.marshal(customer);

        assertThat(values.isEmpty(), is(false));
        assertThat(values, hasEntry(FIRST_NAME_KEY, new String[] { FIRST_NAME }));
        assertThat(values, hasEntry(LAST_NAME_KEY, new String[] { LAST_NAME }));
        assertThat(values, hasEntry(STREET_KEY, new String[] { STREET }));
        assertThat(values, hasEntry(CITY_KEY, new String[] { CITY }));
        assertThat(values, hasEntry(ZIP_KEY, new String[] { Integer.toString(ZIP) }));
        assertThat(values, hasEntry(COUNTRY_CODE_KEY, new String[] { COUNTRY_CODE }));
    }

    @Test
    public void testMultipleAdapters() throws Exception {
        Assert.fail("TODO");
    }

    @Test
    public void testInjectedAdapters() throws Exception {
        Assert.fail("TODO");
    }

    @Test
    public void testRequiredMissing() throws Exception {
        person.setAge(null);
        person.setLastName(null);

        try {
            extractor.marshal(person);
        } catch (final ValidationException e) {
            assertThat(e.getViolations().size(), is(2));
            return;
        }
        Assert.fail("expecting exception");
    }
}
