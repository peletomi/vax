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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.pcosta.vax.impl.util.MapUtil.assertMapEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.pcosta.vax.impl.exception.ValidationException;
import org.pcosta.vax.testobject.Address;
import org.pcosta.vax.testobject.CountryCode;
import org.pcosta.vax.testobject.CountryCodeTranslationAdapter;
import org.pcosta.vax.testobject.CountryInfo;
import org.pcosta.vax.testobject.Customer;
import org.pcosta.vax.testobject.GuestList;
import org.pcosta.vax.testobject.Person;
import org.pcosta.vax.testobject.TodoList;

import com.google.common.collect.Lists;

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

    private static final String COUNTRY_NAME_KEY = "countryName";

    private static final String HUNGARY = "Magyarország";

    private final StringArrayExtractor extractor = new StringArrayExtractor();

    private Person person;

    private Address address;

    private static final ValueKeyGenerator GENERATOR = new ValueKeyGenerator() {
        @Override
        public String generateKey(final String key, final int position) {
            return String.format("%s_%d", key, position);
        }
    };

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
        assertMapEquals(values, FIRST_NAME_KEY, FIRST_NAME, LAST_NAME_KEY, LAST_NAME, AGE_KEY, AGE.toString());
    }

    @Test
    public void testOptional() throws Exception {
        person.setNickName(NICK_NAME);
        final Map<String, String[]> values = extractor.marshal(person);
        assertMapEquals(values, FIRST_NAME_KEY, FIRST_NAME, LAST_NAME_KEY, LAST_NAME, AGE_KEY, AGE.toString(),
                NICK_NAME_KEY, NICK_NAME);
    }

    @Test
    public void testOptionalRecurse() throws Exception {
        final Customer customer = new Customer();
        customer.setPerson(person);

        final Map<String, String[]> values = extractor.marshal(customer);

        assertThat(values.isEmpty(), is(false));
        assertMapEquals(values, FIRST_NAME_KEY, FIRST_NAME, LAST_NAME_KEY, LAST_NAME, AGE_KEY, AGE.toString());
    }

    @Test
    public void testRecurseWithAdapters() throws Exception {
        final Customer customer = new Customer();
        customer.setPerson(person);
        customer.setAddress(address);

        final Map<String, String[]> values = extractor.marshal(customer);

        assertThat(values.isEmpty(), is(false));
        assertMapEquals(values, FIRST_NAME_KEY, FIRST_NAME, LAST_NAME_KEY, LAST_NAME, AGE_KEY, AGE.toString(),
                STREET_KEY, STREET, CITY_KEY, CITY, ZIP_KEY, Integer.toString(ZIP), COUNTRY_CODE_KEY, COUNTRY_CODE);
    }

    @Test
    public void testMultipleAdapters() throws Exception {
        final CountryInfo info = new CountryInfo();
        info.setCountryCode(CountryCode.HU);

        final Map<String, String[]> values = extractor.marshal(info);
        assertMapEquals(values, COUNTRY_CODE_KEY, COUNTRY_CODE, COUNTRY_NAME_KEY, HUNGARY);

    }

    @Test
    public void testInjectedAdapters() throws Exception {
        final Map<String, String> translations = new HashMap<String, String>();
        translations.put(COUNTRY_CODE, HUNGARY.toUpperCase());

        final CountryCodeTranslationAdapter adapter = new CountryCodeTranslationAdapter();
        adapter.setTranslations(translations);

        final CountryInfo info = new CountryInfo();
        info.setCountryCode(CountryCode.HU);

        extractor.setValueAdapters(Lists.newArrayList(adapter));
        final Map<String, String[]> values = extractor.marshal(info);
        assertMapEquals(values, COUNTRY_CODE_KEY, COUNTRY_CODE, COUNTRY_NAME_KEY, HUNGARY.toUpperCase());
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

    @Test
    public void testKeyGeneratorSimple() throws Exception {
        extractor.setValueKeyGenerator(GENERATOR);

        final GuestList guestList = new GuestList();
        guestList.setVipNames(new String[] {"john", "bob" });
        guestList.setGuests(Lists.newArrayList("paul", "bill"));

        final Map<String, String[]> values = extractor.marshal(guestList);
        assertMapEquals(values, "vipName_0" , "john", "vipName_1" , "bob", "guest_0" , "paul", "guest_1" , "bill");
    }

    @Test
    public void testKeyGeneratorCompound() throws Exception {
        extractor.setValueKeyGenerator(GENERATOR);

        final TodoList list = new TodoList();
        list.addItem("task1", new Date(), 1);
        list.addItem("task2", new Date(), 3);
        list.addItem("task3", new Date(), 5);

        final Map<String, String[]> values = extractor.marshal(list);
        assertMapEquals(values, COUNTRY_CODE_KEY, COUNTRY_CODE, COUNTRY_NAME_KEY, HUNGARY.toUpperCase());
    }
}
