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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pcosta.vax.ExceptionHandler;
import org.pcosta.vax.ExtractorFrontEnd;

import com.google.common.base.Joiner;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public class StringArrayExtractorFrontEnd implements ExtractorFrontEnd<Map<String, String[]>> {

    private Map<String[], String[]> values = Collections.emptyMap();

    private String keySeparator = ".";

    private boolean skipBlanks = true;

    private boolean qualified = false;

    @Override
    public void init() {
        values = new HashMap<String[], String[]>();
    }

    @Override
    public void addValue(final String[] key, final Object value) {
        List<String> valueList = new ArrayList<String>();
        if (value == null) {
            valueList.add(null);
        } else if (value.getClass().isArray()) {
            final Object[] values = (Object[]) value;
            for (int i = 0; i < values.length; i++) {
                valueList.add(values[i] == null ? null : values[i].toString());
            }
        } else if (value instanceof Collection) {
            @SuppressWarnings("rawtypes")
            final Collection values = (Collection) value;
            int i = 0;
            for (final Object v : values) {
                valueList.add(v == null ? null : v.toString());
                ++i;
            }
        } else {
            valueList.add(value.toString());
        }
        if (skipBlanks) {
            final List<String> filtered = new ArrayList<String>();
            for (final String string : valueList) {
                if (!isBlank(string)) {
                    filtered.add(string);
                }
            }
            valueList = filtered;
            if (!valueList.isEmpty()) {
            }
        }
        if ((skipBlanks && !valueList.isEmpty()) || !skipBlanks) {
            values.put(key, valueList.toArray(new String[valueList.size()]));
        }
    }

    @Override
    public boolean contains(final String[] key) {
        return values.containsKey(Joiner.on(keySeparator).join(key, keySeparator));
    }

    @Override
    public void setExceptionHandler(final ExceptionHandler exceptionHandler) {
    }

    @Override
    public Map<String, String[]> getExtracted() {
        final Map<String, String[]> result = new HashMap<String, String[]>(values.size());
        for (final Entry<String[], String[]> entry : values.entrySet()) {
            final String[] keys = entry.getKey();
            String key;
            if (qualified) {
                key = Joiner.on(keySeparator).join(keys);
            } else {
                key = keys[keys.length - 1];
            }
            result.put(key, entry.getValue());
        }
        return result;
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

    protected boolean isBlank(final String value) {
        return value == null || "".equals(value);
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(final boolean qualified) {
        this.qualified = qualified;
    }
}
