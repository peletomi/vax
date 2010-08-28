package org.pcosta.vax.impl.util;

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;

public final class MapUtil {

    private static final String COMMA = ", ";
    private static final String NULL = "null";
    private static final String ARROW = "=>";

    private MapUtil() {
    }

    public static void assertMapEquals(final Map<String, String[]> actual, final String... expected) {
        if (expected.length % 2 != 0) {
            fail("expected values must have an even length");
        }
        final Map<String, String[]> map = new HashMap<String, String[]>();
        for (int i = 0; i < expected.length; i++) {
            map.put(expected[i], new String[] { expected[++i] });
        }
        assertMapEquals(actual, map);
    }

    public static void assertMapEquals(final Map<String, String[]> actual, final Map<String, String[]> expected) {
        if ( (actual == null && expected != null)
                || (actual != null && expected == null) ) {
            fail(String.format("both objects should be null expected [%s] actual [%s]", expected, actual));
        }
        if ( actual.size() != expected.size()) {
            fail(String.format("sizes differ actual [%s] expected [%s]", toString(actual), toString(expected)));
        }
        for (final Entry<String, String[]> entry : expected.entrySet()) {
                if (actual.containsKey(entry.getKey())) {
                    assertThat(actual, hasEntry(entry.getKey(), entry.getValue()));
                } else {
                    fail(String.format("actual does not contain key [%s] actual [%s] expected [%s]",
                            entry.getKey(), toString(actual), toString(expected)));
                }
        }
    }

    public static String toString(final Map<String, String[]> map) {
        final Map<String, String> actual = new HashMap<String, String>();
        for (final Entry<String, String[]> entry : map.entrySet()) {
            actual.put(entry.getKey(), Joiner.on(COMMA).useForNull(NULL).join(entry.getValue()));
        }
        return Joiner.on(COMMA).withKeyValueSeparator(ARROW).useForNull(NULL).join(actual);
    }
}
