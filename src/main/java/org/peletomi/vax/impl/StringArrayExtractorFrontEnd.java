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
package org.peletomi.vax.impl;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.peletomi.vax.ExtractorFrontEnd;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class StringArrayExtractorFrontEnd implements ExtractorFrontEnd<Map<String, String[]>> {

    private Map<ImmutableList<String>, LinkedList<String>> values;

    private String keySeparator = ".";

    private boolean skipBlanks = true;

    private boolean qualified = false;

    private static final Predicate<String> IS_NOT_BLANK = new Predicate<String>() {
        @Override
        public boolean apply(final String input) {
            return !isNullOrEmpty(input);
        }
    };

    private static final Function<Object, String> TO_STRING = new Function<Object, String>() {
        @Override
        public String apply(final Object from) {
            return from == null ? null : from.toString();
        }
    };

    private static final Function<Collection<String>, String[]> TO_ARRAY = new Function<Collection<String>, String[]>() {
        @Override
        public String[] apply(final Collection<String> from) {
            return from.toArray(new String[from.size()]);
        }
    };

    StringArrayExtractorFrontEnd() {
        // package protected so only the factory can create one
    }

    @Override
    public void init() {
        values = new HashMap<ImmutableList<String>, LinkedList<String>>();
    }

    @Override
    public void init(final Map<String, String[]> values) {
        this.values = new HashMap<ImmutableList<String>, LinkedList<String>>();
        for (final Entry<String, String[]> entry : values.entrySet()) {
            final ImmutableList<String> keys = ImmutableList.copyOf(Splitter.on(getKeySeparator()).split(entry.getKey()));
            add(this.values, keys, Arrays.asList(entry.getValue()));
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addValue(final String[] key, final Object value) {
        final Collection<Object> valueList;
        if (value != null && value.getClass().isArray()) {
            valueList = newArrayList((Object[]) value);
        } else if (value instanceof Collection) {
            valueList = (Collection) value;
        } else {
            valueList = newArrayList(value);
        }
        List<String> resultList = newArrayList(Collections2.transform(valueList, TO_STRING));
        if (skipBlanks) {
            resultList = newArrayList(filter(resultList, IS_NOT_BLANK));
        }
        if ((skipBlanks && !resultList.isEmpty()) || !skipBlanks) {
            final ImmutableList<String> keys = ImmutableList.copyOf(key);
            add(values, keys, resultList);
        }
    }

    @Override
    public boolean contains(final String[] key) {
        return values.containsKey(ImmutableList.copyOf(key));
    }

    @Override
    public Map<String, String[]> getExtracted() {
        final Map<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>>();
        for (final ImmutableList<String> keys : values.keySet()) {
            String key;
            if (qualified) {
                key = Joiner.on(keySeparator).join(keys);
            } else {
                key = Iterables.getLast(keys);
            }
            if (!result.containsKey(key)) {
                result.put(key, new LinkedList<String>());
            }
            result.put(key, values.get(keys));
        }
        return Maps.transformValues(result, TO_ARRAY);
    }

    public String getKeySeparator() {
        return keySeparator;
    }

    public void setKeySeparator(final String keySeparator) {
        this.keySeparator = keySeparator;
    }

    public boolean isSkipBlanks() {
        return skipBlanks;
    }

    public void setSkipBlanks(final boolean skipBlanks) {
        this.skipBlanks = skipBlanks;
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(final boolean qualified) {
        this.qualified = qualified;
    }

    @Override
    public Object get(final String[] keys) {
        Object value;
        if (qualified) {
            value = values.get(ImmutableList.copyOf(keys));
        } else {
            value = values.get(ImmutableList.copyOf(Arrays.copyOfRange(keys, keys.length - 1, keys.length)));
        }
        return value;
    }

    private void add(final Map<ImmutableList<String>, LinkedList<String>> values, final ImmutableList<String> keys, final List<String> list) {
        if (!values.containsKey(keys)) {
            values.put(keys, new LinkedList<String>());
        }
        values.get(keys).addAll(list);
    }

}
