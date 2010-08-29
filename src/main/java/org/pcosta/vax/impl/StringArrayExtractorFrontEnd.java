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

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.pcosta.vax.ExceptionHandler;
import org.pcosta.vax.ExtractorFrontEnd;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class StringArrayExtractorFrontEnd implements ExtractorFrontEnd<Map<String, String[]>> {

    private Multimap<String[], String> values;

    private String keySeparator = ".";

    private boolean skipBlanks = true;

    private boolean qualified = false;

    private static final Predicate<String> IS_NOT_BLANK = not(or(isNull(), equalTo("")));

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
        values = HashMultimap.create();
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
            values.putAll(key, resultList);
        }
    }

    @Override
    public boolean contains(final String[] key) {
        return values.containsKey(key);
    }

    @Override
    public void setExceptionHandler(final ExceptionHandler exceptionHandler) {
    }

    @Override
    public Map<String, String[]> getExtracted() {
        final Multimap<String, String> result = HashMultimap.create(values.size(), 5);
        for (final String[] keys : values.keySet()) {
            String key;
            if (qualified) {
                key = Joiner.on(keySeparator).join(keys);
            } else {
                key = keys[keys.length - 1];
            }
            result.putAll(key, values.get(keys));
        }
        return Maps.transformValues(result.asMap(), TO_ARRAY);
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
}
