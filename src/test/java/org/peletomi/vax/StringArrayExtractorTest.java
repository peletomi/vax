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
package org.peletomi.vax;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.peletomi.vax.impl.util.MapUtil.assertMapEquals;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.peletomi.vax.impl.StringArrayExtractorFrontEndFactory;
import org.peletomi.vax.impl.exception.ValidationException;
import org.peletomi.vax.testobject.Address;
import org.peletomi.vax.testobject.CountryCode;
import org.peletomi.vax.testobject.CountryCodeTranslationAdapter;
import org.peletomi.vax.testobject.CountryInfo;
import org.peletomi.vax.testobject.Customer;
import org.peletomi.vax.testobject.GuestList;
import org.peletomi.vax.testobject.Person;
import org.peletomi.vax.testobject.TodoList;

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

        final Person actual = extractor.unmarshal(Person.class, values);
        assertThat(actual, equalTo(person));
    }

    @Test
    public void testOptional() throws Exception {
        person.setNickName(NICK_NAME);
        final Map<String, String[]> values = extractor.marshal(person);
        assertMapEquals(values, FIRST_NAME_KEY, FIRST_NAME, LAST_NAME_KEY, LAST_NAME, AGE_KEY, AGE.toString(),
                NICK_NAME_KEY, NICK_NAME);

        final Person actual = extractor.unmarshal(Person.class, values);
        assertThat(actual, equalTo(person));
    }

    @Test
    public void testOptionalRecurse() throws Exception {
        final Customer customer = new Customer();
        customer.setPerson(person);

        final Map<String, String[]> values = extractor.marshal(customer);

        assertThat(values.isEmpty(), is(false));
        assertMapEquals(values, FIRST_NAME_KEY, FIRST_NAME, LAST_NAME_KEY, LAST_NAME, AGE_KEY, AGE.toString());

        final Customer actual = extractor.unmarshal(Customer.class, values);
        assertThat(actual, equalTo(customer));
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
    public void testKeyGeneratorCompoundQualified() throws Exception {
        extractor.setValueKeyGenerator(GENERATOR);
        extractor.setFrontEndFactory(StringArrayExtractorFrontEndFactory.instance().qualified(true));

        final TodoList list = new TodoList();
        list.addItem("task1", 1);
        list.addItem("task2", 3);
        list.addItem("task3", 5);

        final Map<String, String[]> values = extractor.marshal(list);
        assertMapEquals(values,
                "item_0.task", "task1", "item_0.priority", "1",
                "item_1.task", "task2", "item_1.priority", "3",
                "item_2.task", "task3", "item_2.priority", "5"
                );
    }

    @Test
    public void testKeyGeneratorCompound() throws Exception {
        final TodoList list = new TodoList();
        list.addItem("task1", 1);
        list.addItem("task2", 3);
        list.addItem("task3", 5);

        final Map<String, String[]> expected = new HashMap<String, String[]>();
        expected.put("task", new String[] {"task1", "task2", "task3" });
        expected.put("priority", new String[] {"1", "3", "5" });

        final Map<String, String[]> values = extractor.marshal(list);
        assertMapEquals(values, expected);
    }

    @Test
    public void testUnmarshallingWithMissingRequiredFields() throws Exception {
        try {
            extractor.unmarshal(Person.class, new HashMap<String, String[]>());
        } catch (final ValidationException e) {
            assertThat(e.getViolations().size(), is(3));
            return;
        }
        Assert.fail("expecting exception");
    }
}
